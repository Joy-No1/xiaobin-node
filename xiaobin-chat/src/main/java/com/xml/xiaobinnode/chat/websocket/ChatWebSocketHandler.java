package com.xml.xiaobinnode.chat.websocket;

import cn.hutool.json.JSONUtil;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.chat.service.OnlineStatusService;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.util.JwtUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty WebSocket 消息处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ChatWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChatService chatService;
    private final RedisTemplate<String, String> redisTemplate;
    private final OnlineStatusService onlineStatusService;

    /** Channel -> userId 映射 */
    private static final ConcurrentHashMap<Channel, Long> CHANNEL_USER_MAP = new ConcurrentHashMap<>();
    /** userId -> Channel 映射 */
    private static final ConcurrentHashMap<Long, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // WebSocket握手完成，等待客户端发送认证消息
            log.info("WebSocket连接建立: {}", ctx.channel().remoteAddress());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String text = frame.text();
        try {
            Map<String, Object> messageMap = JSONUtil.toBean(text, Map.class);
            String type = (String) messageMap.get("type");

            switch (type) {
                case "AUTH" -> handleAuth(ctx, messageMap);
                case "MESSAGE" -> handleMessage(ctx, messageMap);
                case "PING" -> handlePing(ctx);
                default -> sendError(ctx, "未知消息类型: " + type);
            }
        } catch (Exception e) {
            log.error("消息处理失败", e);
            sendError(ctx, "消息格式错误");
        }
    }

    /**
     * 处理认证消息
     */
    private void handleAuth(ChannelHandlerContext ctx, Map<String, Object> msg) {
        String token = (String) msg.get("token");
        try {
            String userId = JwtUtils.getUserId(token);
            log.info("用户认证成功: userId={}", userId);

            Long uid = Long.valueOf(userId);
            String connectionId = ctx.channel().id().asLongText();

            CHANNEL_USER_MAP.put(ctx.channel(), uid);
            USER_CHANNEL_MAP.put(uid, ctx.channel());

            // 使用在线状态服务管理（心跳 + TTL）
            onlineStatusService.userOnline(uid, connectionId);

            sendMessage(ctx, Map.of("type", "AUTH_SUCCESS", "userId", userId));
        } catch (Exception e) {
            sendError(ctx, "认证失败: Token无效");
        }
    }

    /**
     * 处理聊天消息
     */
    private void handleMessage(ChannelHandlerContext ctx, Map<String, Object> msg) {
        Long senderId = CHANNEL_USER_MAP.get(ctx.channel());
        if (senderId == null) {
            sendError(ctx, "请先认证");
            return;
        }

        Long receiverId = Long.valueOf(msg.get("receiverId").toString());
        String content = (String) msg.get("content");
        String messageType = (String) msg.getOrDefault("messageType", "TEXT");
        Integer duration = msg.get("duration") != null ? Integer.valueOf(msg.get("duration").toString()) : null;

        try {
            ChatMessage chatMessage = chatService.sendMessage(senderId, receiverId, content, messageType, duration);

            // 发送确认给发送者
            sendMessage(ctx, Map.of(
                    "type", "MESSAGE_SENT",
                    "messageId", chatMessage.getId(),
                    "conversationId", chatMessage.getConversationId(),
                    "createdAt", chatMessage.getCreatedAt().toString()
            ));

            // 推送消息给接收者（如果在线）
            Channel receiverChannel = USER_CHANNEL_MAP.get(receiverId);
            if (receiverChannel != null && receiverChannel.isActive()) {
                sendMessage(receiverChannel, Map.of(
                        "type", "NEW_MESSAGE",
                        "messageId", chatMessage.getId(),
                        "conversationId", chatMessage.getConversationId(),
                        "senderId", senderId,
                        "content", content,
                        "messageType", messageType,
                        "duration", duration != null ? duration : 0,
                        "createdAt", chatMessage.getCreatedAt().toString()
                ));
            }
        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    /**
     * 处理心跳
     */
    private void handlePing(ChannelHandlerContext ctx) {
        Long userId = CHANNEL_USER_MAP.get(ctx.channel());
        if (userId != null) {
            String connectionId = ctx.channel().id().asLongText();
            // 刷新在线状态的TTL
            onlineStatusService.refreshOnlineStatus(userId, connectionId);
        }
        sendMessage(ctx, Map.of("type", "PONG"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Long userId = CHANNEL_USER_MAP.remove(ctx.channel());
        if (userId != null) {
            USER_CHANNEL_MAP.remove(userId);
            String connectionId = ctx.channel().id().asLongText();
            // 用户离线（会检查是否还有其他连接）
            onlineStatusService.userOffline(userId, connectionId);
            log.info("用户断开连接: userId={}, connectionId={}", userId, connectionId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket异常", cause);
        ctx.close();
    }

    private void sendMessage(ChannelHandlerContext ctx, Map<String, Object> msg) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(msg)));
    }

    private void sendMessage(Channel channel, Map<String, Object> msg) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(msg)));
    }

    private void sendError(ChannelHandlerContext ctx, String errorMsg) {
        sendMessage(ctx, Map.of("type", "ERROR", "message", errorMsg));
    }

    /**
     * 向指定在线用户推送消息（供 Redis 订阅者等外部组件调用）
     *
     * @param userId      目标用户ID
     * @param jsonMessage JSON格式的消息字符串
     * @return true=用户在线且消息已发送，false=用户离线
     */
    public static boolean pushToUser(Long userId, String jsonMessage) {
        Channel channel = USER_CHANNEL_MAP.get(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
            return true;
        }
        return false;
    }

    /**
     * 获取指定用户的Channel（供Service层本地推送使用）
     *
     * @param userId 用户ID
     * @return Channel，不在线返回null
     */
    public static Channel getUserChannel(Long userId) {
        return USER_CHANNEL_MAP.get(userId);
    }
}

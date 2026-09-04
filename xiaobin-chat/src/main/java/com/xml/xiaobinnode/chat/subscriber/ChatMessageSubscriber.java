package com.xml.xiaobinnode.chat.subscriber;

import cn.hutool.json.JSONUtil;
import com.xml.xiaobinnode.chat.websocket.ChatWebSocketHandler;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Redis 聊天消息订阅者
 * 监听聊天消息事件，通过 WebSocket 推送给在线用户（支持多实例部署）
 */
@Slf4j
@Component
public class ChatMessageSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        if (!CommonConstants.REDIS_CHANNEL_CHAT_MESSAGE.equals(channel)) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = JSONUtil.toBean(body, Map.class);
            Long receiverId = Long.valueOf(event.get("receiverId").toString());

            // 检查接收者是否在当前实例在线
            boolean sent = ChatWebSocketHandler.pushToUser(receiverId, body);

            if (sent) {
                log.info("聊天消息推送成功: receiverId={}, messageId={}", receiverId, event.get("messageId"));
            } else {
                log.debug("用户不在当前实例在线: receiverId={}", receiverId);
            }
        } catch (Exception e) {
            log.error("处理聊天消息推送失败: body={}", body, e);
        }
    }
}

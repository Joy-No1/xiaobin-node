package com.xml.xiaobinnode.chat.subscriber;

import cn.hutool.json.JSONUtil;
import com.xml.xiaobinnode.chat.websocket.ChatWebSocketHandler;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Redis 关注通知订阅者
 * 监听关注事件，通过 WebSocket 推送给被关注的用户
 */
@Slf4j
@Component
public class NotificationSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        if (!CommonConstants.REDIS_CHANNEL_FOLLOW_NOTIFICATION.equals(channel)) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = JSONUtil.toBean(body, Map.class);
            String type = (String) event.get("type");

            if ("NEW_FOLLOWER".equals(type)) {
                handleNewFollower(event, body);
            }
        } catch (Exception e) {
            log.error("处理关注通知失败: body={}", body, e);
        }
    }

    private void handleNewFollower(Map<String, Object> event, String body) {
        Long toUserId = Long.valueOf(event.get("toUserId").toString());

        // 组装WebSocket推送消息
        Map<String, Object> pushMessage = new LinkedHashMap<>();
        pushMessage.put("type", "NEW_FOLLOWER");
        pushMessage.put("notificationId", event.get("notificationId"));
        pushMessage.put("fromUserId", event.get("fromUserId"));
        pushMessage.put("content", event.get("content"));
        pushMessage.put("createdAt", event.get("createdAt"));

        // 关注者信息（已由community服务嵌入事件，避免本线程做Feign调用）
        Map<String, Object> fromUser = new LinkedHashMap<>();
        fromUser.put("id", event.get("fromUserId"));
        fromUser.put("nickname", event.getOrDefault("fromUserName", ""));
        fromUser.put("avatarUrl", event.getOrDefault("fromUserAvatar", ""));
        pushMessage.put("fromUser", fromUser);

        String jsonMessage = JSONUtil.toJsonStr(pushMessage);
        boolean sent = ChatWebSocketHandler.pushToUser(toUserId, jsonMessage);

        log.info("推送关注通知: toUserId={}, fromUserId={}, sent={}", toUserId, event.get("fromUserId"), sent);
    }
}

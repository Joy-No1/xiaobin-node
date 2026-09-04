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
 * 在线状态事件订阅者
 * 监听用户上线/离线事件，推送给好友
 */
@Slf4j
@Component
public class OnlineStatusSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = JSONUtil.toBean(body, Map.class);
            Long userId = Long.valueOf(event.get("userId").toString());

            if (CommonConstants.REDIS_CHANNEL_USER_ONLINE.equals(channel)) {
                handleUserOnline(userId, event);
            } else if (CommonConstants.REDIS_CHANNEL_USER_OFFLINE.equals(channel)) {
                handleUserOffline(userId, event);
            }
        } catch (Exception e) {
            log.error("处理在线状态事件失败: channel={}, body={}", channel, body, e);
        }
    }

    /**
     * 用户上线：可以推送给其好友
     */
    private void handleUserOnline(Long userId, Map<String, Object> event) {
        log.info("用户上线事件: userId={}", userId);

        // TODO: 查询该用户的好友列表，推送上线通知
        // 示例：推送给好友
        // List<Long> friends = getFriends(userId);
        // for (Long friendId : friends) {
        //     Map<String, Object> notification = new LinkedHashMap<>();
        //     notification.put("type", "USER_ONLINE");
        //     notification.put("userId", userId);
        //     notification.put("timestamp", event.get("timestamp"));
        //     ChatWebSocketHandler.pushToUser(friendId, JSONUtil.toJsonStr(notification));
        // }
    }

    /**
     * 用户离线：可以推送给其好友
     */
    private void handleUserOffline(Long userId, Map<String, Object> event) {
        log.info("用户离线事件: userId={}", userId);

        // TODO: 查询该用户的好友列表，推送离线通知
        // 示例：推送给好友
        // List<Long> friends = getFriends(userId);
        // for (Long friendId : friends) {
        //     Map<String, Object> notification = new LinkedHashMap<>();
        //     notification.put("type", "USER_OFFLINE");
        //     notification.put("userId", userId);
        //     notification.put("timestamp", event.get("timestamp"));
        //     ChatWebSocketHandler.pushToUser(friendId, JSONUtil.toJsonStr(notification));
        // }
    }
}

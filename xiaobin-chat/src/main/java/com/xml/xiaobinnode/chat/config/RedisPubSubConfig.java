package com.xml.xiaobinnode.chat.config;

import com.xml.xiaobinnode.chat.subscriber.ChatMessageSubscriber;
import com.xml.xiaobinnode.chat.subscriber.NotificationSubscriber;
import com.xml.xiaobinnode.chat.subscriber.OnlineStatusSubscriber;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 发布/订阅配置
 * 订阅关注通知、聊天消息、在线状态频道，实时推送给在线用户
 */
@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            NotificationSubscriber notificationSubscriber,
            ChatMessageSubscriber chatMessageSubscriber,
            OnlineStatusSubscriber onlineStatusSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 订阅关注通知频道
        ChannelTopic followTopic = new ChannelTopic(CommonConstants.REDIS_CHANNEL_FOLLOW_NOTIFICATION);
        container.addMessageListener(notificationSubscriber, followTopic);

        // 订阅聊天消息频道
        ChannelTopic chatTopic = new ChannelTopic(CommonConstants.REDIS_CHANNEL_CHAT_MESSAGE);
        container.addMessageListener(chatMessageSubscriber, chatTopic);

        // 订阅用户上线频道
        ChannelTopic onlineTopic = new ChannelTopic(CommonConstants.REDIS_CHANNEL_USER_ONLINE);
        container.addMessageListener(onlineStatusSubscriber, onlineTopic);

        // 订阅用户离线频道
        ChannelTopic offlineTopic = new ChannelTopic(CommonConstants.REDIS_CHANNEL_USER_OFFLINE);
        container.addMessageListener(onlineStatusSubscriber, offlineTopic);

        return container;
    }
}

package com.xml.xiaobinnode.chat.config;

import com.xml.xiaobinnode.chat.subscriber.NotificationSubscriber;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 发布/订阅配置
 * 订阅关注通知频道，实时推送给在线用户
 */
@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            NotificationSubscriber notificationSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        ChannelTopic topic = new ChannelTopic(CommonConstants.REDIS_CHANNEL_FOLLOW_NOTIFICATION);
        container.addMessageListener(notificationSubscriber, topic);

        return container;
    }
}

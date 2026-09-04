package com.xml.xiaobinnode.chat.service.impl;

import cn.hutool.json.JSONUtil;
import com.xml.xiaobinnode.chat.service.OnlineStatusService;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 在线状态管理服务实现
 * 基于 Redis + 心跳 + TTL 机制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineStatusServiceImpl implements OnlineStatusService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void userOnline(Long userId, String connectionId) {
        String onlineKey = CommonConstants.REDIS_IM_ONLINE_KEY + userId;
        String connectionsKey = CommonConstants.REDIS_IM_CONNECTIONS_KEY + userId;

        try {
            // 1. 设置在线状态（TTL: 35秒）
            redisTemplate.opsForValue().set(
                    onlineKey,
                    "1",
                    CommonConstants.ONLINE_STATUS_TTL_SECONDS,
                    TimeUnit.SECONDS
            );

            // 2. 添加连接ID到集合（支持多端登录）
            redisTemplate.opsForSet().add(connectionsKey, connectionId);
            redisTemplate.expire(connectionsKey, CommonConstants.ONLINE_STATUS_TTL_SECONDS, TimeUnit.SECONDS);

            // 3. 发布上线事件
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("userId", userId);
            event.put("connectionId", connectionId);
            event.put("timestamp", System.currentTimeMillis());
            redisTemplate.convertAndSend(
                    CommonConstants.REDIS_CHANNEL_USER_ONLINE,
                    JSONUtil.toJsonStr(event)
            );

            log.info("用户上线: userId={}, connectionId={}", userId, connectionId);
        } catch (Exception e) {
            log.error("设置用户在线状态失败: userId={}", userId, e);
        }
    }

    @Override
    public void userOffline(Long userId, String connectionId) {
        String onlineKey = CommonConstants.REDIS_IM_ONLINE_KEY + userId;
        String connectionsKey = CommonConstants.REDIS_IM_CONNECTIONS_KEY + userId;

        try {
            // 1. 从连接集合中移除
            redisTemplate.opsForSet().remove(connectionsKey, connectionId);

            // 2. 检查是否还有其他连接
            Long remainingConnections = redisTemplate.opsForSet().size(connectionsKey);
            if (remainingConnections == null || remainingConnections == 0) {
                // 没有其他连接了，删除在线状态
                redisTemplate.delete(onlineKey);
                redisTemplate.delete(connectionsKey);

                // 发布离线事件
                Map<String, Object> event = new LinkedHashMap<>();
                event.put("userId", userId);
                event.put("connectionId", connectionId);
                event.put("timestamp", System.currentTimeMillis());
                redisTemplate.convertAndSend(
                        CommonConstants.REDIS_CHANNEL_USER_OFFLINE,
                        JSONUtil.toJsonStr(event)
                );

                log.info("用户离线: userId={}, connectionId={}", userId, connectionId);
            } else {
                log.info("用户仍有其他连接在线: userId={}, remainingConnections={}", userId, remainingConnections);
            }
        } catch (Exception e) {
            log.error("设置用户离线状态失败: userId={}", userId, e);
        }
    }

    @Override
    public void refreshOnlineStatus(Long userId, String connectionId) {
        String onlineKey = CommonConstants.REDIS_IM_ONLINE_KEY + userId;
        String connectionsKey = CommonConstants.REDIS_IM_CONNECTIONS_KEY + userId;

        try {
            // 刷新在线状态的TTL
            redisTemplate.expire(onlineKey, CommonConstants.ONLINE_STATUS_TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.expire(connectionsKey, CommonConstants.ONLINE_STATUS_TTL_SECONDS, TimeUnit.SECONDS);

            // 确保连接ID仍在集合中（防止TTL过期）
            redisTemplate.opsForSet().add(connectionsKey, connectionId);

            log.debug("刷新在线状态: userId={}, connectionId={}", userId, connectionId);
        } catch (Exception e) {
            log.error("刷新在线状态失败: userId={}", userId, e);
        }
    }

    @Override
    public boolean isOnline(Long userId) {
        String onlineKey = CommonConstants.REDIS_IM_ONLINE_KEY + userId;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(onlineKey));
        } catch (Exception e) {
            log.error("检查在线状态失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public Map<Long, Boolean> batchCheckOnline(List<Long> userIds) {
        Map<Long, Boolean> result = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        try {
            for (Long userId : userIds) {
                result.put(userId, isOnline(userId));
            }
        } catch (Exception e) {
            log.error("批量检查在线状态失败", e);
        }

        return result;
    }

    @Override
    public Set<String> getUserConnections(Long userId) {
        String connectionsKey = CommonConstants.REDIS_IM_CONNECTIONS_KEY + userId;
        try {
            return redisTemplate.opsForSet().members(connectionsKey);
        } catch (Exception e) {
            log.error("获取用户连接失败: userId={}", userId, e);
            return Set.of();
        }
    }

    @Override
    public long getOnlineUserCount() {
        try {
            Set<String> keys = redisTemplate.keys(CommonConstants.REDIS_IM_ONLINE_KEY + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.error("获取在线用户数失败", e);
            return 0;
        }
    }
}

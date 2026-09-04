package com.xml.xiaobinnode.chat.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线状态管理服务
 */
public interface OnlineStatusService {

    /**
     * 用户上线
     * @param userId 用户ID
     * @param connectionId 连接ID（Channel ID）
     */
    void userOnline(Long userId, String connectionId);

    /**
     * 用户离线
     * @param userId 用户ID
     * @param connectionId 连接ID
     */
    void userOffline(Long userId, String connectionId);

    /**
     * 刷新在线状态（心跳）
     * @param userId 用户ID
     * @param connectionId 连接ID
     */
    void refreshOnlineStatus(Long userId, String connectionId);

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return true=在线，false=离线
     */
    boolean isOnline(Long userId);

    /**
     * 批量查询用户在线状态
     * @param userIds 用户ID列表
     * @return userId -> 是否在线的映射
     */
    Map<Long, Boolean> batchCheckOnline(List<Long> userIds);

    /**
     * 获取用户的所有连接ID
     * @param userId 用户ID
     * @return 连接ID集合
     */
    Set<String> getUserConnections(Long userId);

    /**
     * 获取在线用户数量
     * @return 在线用户数
     */
    long getOnlineUserCount();
}

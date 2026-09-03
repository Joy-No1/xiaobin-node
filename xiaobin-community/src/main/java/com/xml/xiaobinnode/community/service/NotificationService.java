package com.xml.xiaobinnode.community.service;

import com.xml.xiaobinnode.common.dto.NotificationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.community.document.Notification;

public interface NotificationService {

    /** 保存通知 */
    Notification save(Long userId, String type, Long fromUserId, String content);

    /** 分页查询当前用户的通知列表 */
    PageResult<NotificationDTO> list(Long userId, int page, int size);

    /** 标记通知已读 */
    void markAsRead(String notificationId, Long userId);

    /** 获取未读通知数 */
    long unreadCount(Long userId);
}

package com.xml.xiaobinnode.community.service.impl;

import com.xml.xiaobinnode.api.community.dto.NotificationDTO;
import com.xml.xiaobinnode.api.user.UserFeignClient;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.community.document.Notification;
import com.xml.xiaobinnode.community.repository.NotificationRepository;
import com.xml.xiaobinnode.community.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    @Lazy
    private UserFeignClient userFeignClient;

    @Override
    public Notification save(Long userId, String type, Long fromUserId, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setFromUserId(fromUserId);
        notification.setContent(content);
        notification.setIsRead(false);
        notification.setCreatedAt(new Date());
        return notificationRepository.save(notification);
    }

    @Override
    public PageResult<NotificationDTO> list(Long userId, int page, int size) {
        Page<Notification> notificationPage = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page - 1, size));
        List<Notification> notifications = notificationPage.getContent();
        if (notifications.isEmpty()) {
            return PageResult.of(page, size, notificationPage.getTotalElements(), Collections.emptyList());
        }

        // 批量收集 fromUserId，一次Feign调用获取所有用户信息
        Set<Long> fromUserIds = new HashSet<>();
        for (Notification n : notifications) {
            fromUserIds.add(n.getFromUserId());
        }
        Map<Long, UserVO> userMap = fetchUserVOMap(new ArrayList<>(fromUserIds));

        List<NotificationDTO> dtos = notifications.stream()
                .map(n -> toDTO(n, userMap))
                .collect(Collectors.toList());

        return PageResult.of(page, size, notificationPage.getTotalElements(), dtos);
    }

    @Override
    public void markAsRead(String notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("通知不存在"));
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该通知");
        }
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    private NotificationDTO toDTO(Notification n, Map<Long, UserVO> userMap) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setType(n.getType());
        dto.setFromUserId(n.getFromUserId());
        dto.setFromUser(userMap.get(n.getFromUserId()));
        dto.setContent(n.getContent());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    /**
     * 批量获取用户信息
     */
    private List<UserVO> fetchUserVOs(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<UserVO> users = userFeignClient.getUsersByIds(userIds);
            return users != null ? users : Collections.emptyList();
        } catch (Exception e) {
            log.warn("批量获取用户信息失败: userIds={}", userIds, e);
        }
        return Collections.emptyList();
    }

    private Map<Long, UserVO> fetchUserVOMap(List<Long> userIds) {
        List<UserVO> users = fetchUserVOs(userIds);
        return users.stream().collect(Collectors.toMap(UserVO::getId, u -> u, (a, b) -> a));
    }
}

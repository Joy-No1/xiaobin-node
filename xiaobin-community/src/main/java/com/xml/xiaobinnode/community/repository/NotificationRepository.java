package com.xml.xiaobinnode.community.repository;

import com.xml.xiaobinnode.community.document.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /** 按用户分页查询通知（时间倒序） */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /** 统计未读通知数 */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}

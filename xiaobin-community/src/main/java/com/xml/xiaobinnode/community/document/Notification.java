package com.xml.xiaobinnode.community.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 通知文档 (MongoDB)
 */
@Data
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    /** 接收通知的用户ID */
    private Long userId;

    /** 通知类型: FOLLOW（可扩展 LIKE / COMMENT 等） */
    private String type;

    /** 触发该通知的用户ID */
    private Long fromUserId;

    /** 通知内容 */
    private String content;

    /** 是否已读 */
    private Boolean isRead = false;

    private Date createdAt = new Date();
}

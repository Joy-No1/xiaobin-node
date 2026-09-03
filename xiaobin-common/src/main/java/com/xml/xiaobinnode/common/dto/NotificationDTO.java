package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 通知DTO（用于API返回和Feign远程调用）
 */
@Data
public class NotificationDTO {

    private String id;

    /** 接收通知的用户ID */
    private Long userId;

    /** 通知类型: FOLLOW */
    private String type;

    /** 触发该通知的用户ID */
    private Long fromUserId;

    /** 触发该通知的用户信息 */
    private UserVO fromUser;

    /** 通知内容 */
    private String content;

    /** 是否已读 */
    private Boolean isRead;

    private Date createdAt;
}

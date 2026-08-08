package com.xml.xiaobinnode.api.chat.dto;

import lombok.Data;

import java.util.Date;

/**
 * 会话DTO
 */
@Data
public class ConversationDTO {

    private Long id;

    private Long user1Id;

    private Long user2Id;

    /** 最后一条消息摘要 */
    private String lastMessage;

    /** 最后消息时间 */
    private Date lastMessageTime;

    private Date createdAt;

    private Date updatedAt;
}

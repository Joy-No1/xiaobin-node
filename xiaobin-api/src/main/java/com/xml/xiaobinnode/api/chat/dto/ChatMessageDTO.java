package com.xml.xiaobinnode.api.chat.dto;

import lombok.Data;

import java.util.Date;

/**
 * 聊天消息DTO
 */
@Data
public class ChatMessageDTO {

    private Long id;

    private Long conversationId;

    /** 发送者 */
    private Long senderId;

    /** 接收者 */
    private Long receiverId;

    private String content;

    /** TEXT-文字 / IMAGE-图片 / VOICE-语音 / EMOJI-表情 */
    private String messageType;

    /** 语音消息时长（秒），其他类型为null */
    private Integer duration;

    /** 0未读 1已读 */
    private Integer isRead;

    private Date createdAt;
}

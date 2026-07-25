package com.xml.xiaobinnode.chat.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 聊天消息文档 (MongoDB)
 */
@Data
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    /** 会话ID */
    private String conversationId;

    /** 发送者ID */
    private Long senderId;

    /** 接收者ID */
    private Long receiverId;

    /** 消息内容 */
    private String content;

    /** 消息类型: TEXT / IMAGE */
    private String messageType;

    /** 是否已读 */
    private Boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

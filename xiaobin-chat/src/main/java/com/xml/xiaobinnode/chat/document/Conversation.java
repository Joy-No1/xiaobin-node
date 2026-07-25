package com.xml.xiaobinnode.chat.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 会话文档 (MongoDB)
 */
@Data
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    private Long user1Id;

    private Long user2Id;

    /** 最后一条消息内容 */
    private String lastMessage;

    /** 最后消息时间 */
    private LocalDateTime lastMessageTime;

    private LocalDateTime updatedAt = LocalDateTime.now();
}

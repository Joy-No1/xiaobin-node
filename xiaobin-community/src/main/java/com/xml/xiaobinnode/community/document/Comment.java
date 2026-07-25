package com.xml.xiaobinnode.community.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 评论文档 (MongoDB)
 */
@Data
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    private String postId;

    private Long userId;

    private String content;

    /** 回复的目标用户ID */
    private Long replyToUserId;

    /** 父评论ID（回复评论时使用） */
    private String parentCommentId;

    private LocalDateTime createdAt = LocalDateTime.now();
}

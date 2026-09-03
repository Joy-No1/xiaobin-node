package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 评论DTO（用于Feign远程调用）
 */
@Data
public class CommentDTO {

    private String id;

    private String postId;

    private Long userId;

    private String content;

    /** 回复的目标用户ID */
    private Long replyToUserId;

    /** 父评论ID（回复评论时使用） */
    private String parentCommentId;

    private Date createdAt;
}

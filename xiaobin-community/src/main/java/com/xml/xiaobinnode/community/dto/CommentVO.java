package com.xml.xiaobinnode.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;

/**
 * 评论VO（含评论人信息）
 */
@Data
public class CommentVO {

    private String id;

    private String postId;

    private Long userId;

    /** 评论人信息 */
    private UserVO user;

    private String content;

    /** 回复的目标用户ID */
    private Long replyToUserId;

    /** 回复的目标用户信息 */
    private UserVO replyToUser;

    /** 父评论ID（回复评论时使用） */
    private String parentCommentId;

    private Date createdAt;
}

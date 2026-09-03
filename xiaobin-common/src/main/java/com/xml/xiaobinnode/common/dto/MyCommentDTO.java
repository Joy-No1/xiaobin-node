package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 我的评论DTO（Feign传输用）
 */
@Data
public class MyCommentDTO {

    /** 评论ID */
    private String id;

    /** 所属帖子ID */
    private String postId;

    /** 发帖人信息 */
    private UserVO postUser;

    /** 帖子内容摘要 */
    private String postContent;

    /** 评论内容 */
    private String content;

    /** 评论时间 */
    private Date createdAt;
}

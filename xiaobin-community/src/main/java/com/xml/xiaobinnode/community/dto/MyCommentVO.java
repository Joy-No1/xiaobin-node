package com.xml.xiaobinnode.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;

/**
 * 我的评论VO（我发表过的评论）
 */
@Data
public class MyCommentVO {

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

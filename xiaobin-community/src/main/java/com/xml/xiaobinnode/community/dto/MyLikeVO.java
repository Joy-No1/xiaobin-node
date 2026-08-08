package com.xml.xiaobinnode.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 我的点赞VO（我点赞过的帖子）
 */
@Data
public class MyLikeVO {

    private String postId;

    /** 发帖人信息 */
    private UserVO postUser;

    /** 帖子内容 */
    private String content;

    private List<String> images;

    private Integer likeCount;

    private Integer commentCount;

    /** 是否被编辑过 */
    private Boolean isEdited;

    /** 帖子发布时间 */
    private Date postCreatedAt;

    /** 点赞时间 */
    private Date likedAt;
}

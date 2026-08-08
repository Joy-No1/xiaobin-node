package com.xml.xiaobinnode.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 帖子详情VO（含点赞人、评论人、评论列表）
 */
@Data
public class PostVO {

    private String id;

    private Long userId;

    /** 发帖人信息 */
    private UserVO user;

    private String content;

    private List<String> images;

    private String location;

    private Integer likeCount;

    private Integer commentCount;

    /** 是否被编辑过 */
    private Boolean isEdited;

    private String status;

    /** 当前用户是否已点赞 */
    private Boolean isLiked;

    /** 点赞人列表 */
    private List<UserVO> likers;

    /** 最新评论列表（含评论人信息） */
    private List<CommentVO> recentComments;

    private Date createdAt;

    private Date updatedAt;
}

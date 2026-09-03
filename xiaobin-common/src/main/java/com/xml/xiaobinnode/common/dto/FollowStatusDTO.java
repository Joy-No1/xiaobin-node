package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 关注状态DTO
 */
@Data
public class FollowStatusDTO {

    /** 当前用户是否已关注目标用户 */
    private Boolean isFollowing;

    /** 目标用户是否已关注当前用户 */
    private Boolean isFollowedBy;

    /**
     * 关注关系状态:
     * NONE - 互不关注
     * FOLLOWING - 我关注了对方
     * FOLLOWER - 对方关注了我
     * MUTUAL - 互相关注
     */
    private String followStatus;
}

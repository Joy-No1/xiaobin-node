package com.xml.xiaobinnode.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户主页VO（含当前登录用户对该用户的关注状态，聚合转发用）
 * <p>对应 user 模块 UserProfileVO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfileVO extends UserVO {

    /** 当前用户是否已关注该用户 */
    private Boolean isFollowing;

    /** 该用户是否已关注当前用户 */
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

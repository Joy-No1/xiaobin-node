package com.xml.xiaobinnode.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;

/**
 * 我的关注VO（我关注的人列表）
 */
@Data
public class MyFollowVO {

    /** 被关注用户信息 */
    private UserVO user;

    /** 是否互相关注 */
    private Boolean isMutual;

    /** 关注时间 */
    private Date followedAt;
}

package com.xml.xiaobinnode.api.community.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;

/**
 * 我的关注DTO（Feign传输用）
 */
@Data
public class MyFollowDTO {

    /** 被关注用户信息 */
    private UserVO user;

    /** 是否互相关注 */
    private Boolean isMutual;

    /** 关注时间 */
    private Date followedAt;
}

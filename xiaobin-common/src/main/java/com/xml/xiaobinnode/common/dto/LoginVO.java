package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 登录响应VO（聚合服务转发用）
 */
@Data
public class LoginVO {

    /** JWT Token */
    private String token;

    /** 用户信息 */
    private UserVO user;
}

package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 登录请求（聚合服务转发用，字段与 user 模块 LoginRequest 一致）
 */
@Data
public class LoginRequest {

    /** 手机号或邮箱 */
    private String account;

    private String password;
}

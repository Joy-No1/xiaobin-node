package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 注册请求（聚合服务转发用，字段与 user 模块 RegisterRequest 一致）
 */
@Data
public class RegisterRequest {

    private String phone;

    private String password;

    private String nickname;
}

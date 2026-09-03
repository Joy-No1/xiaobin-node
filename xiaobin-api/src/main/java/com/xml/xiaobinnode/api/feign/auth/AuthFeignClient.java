package com.xml.xiaobinnode.api.feign.auth;

import com.xml.xiaobinnode.common.dto.LoginRequest;
import com.xml.xiaobinnode.common.dto.LoginVO;
import com.xml.xiaobinnode.common.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 认证服务 Feign（转发 user 模块 /v1/auth）
 */
@FeignClient(name = "xiaobin-user", contextId = "userAuthFeignClient", path = "/auth")
public interface AuthFeignClient {

    @PostMapping("/register")
    Object register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    LoginVO login(@RequestBody LoginRequest request);
}

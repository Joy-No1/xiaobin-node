package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.auth.AuthFeignClient;
import com.xml.xiaobinnode.common.dto.LoginRequest;
import com.xml.xiaobinnode.common.dto.LoginVO;
import com.xml.xiaobinnode.common.dto.RegisterRequest;
import com.xml.xiaobinnode.common.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聚合-认证（注册/登录）
 */
@Tag(name = "聚合-认证")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthAggController {

    private final AuthFeignClient authFeignClient;

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<Object> register(@RequestBody RegisterRequest request) {
        return Result.success(authFeignClient.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        return Result.success(authFeignClient.login(request));
    }
}

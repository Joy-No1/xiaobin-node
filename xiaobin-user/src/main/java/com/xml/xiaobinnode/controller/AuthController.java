package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.annotation.NoAuth;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.dto.LoginRequest;
import com.xml.xiaobinnode.dto.LoginVO;
import com.xml.xiaobinnode.dto.RegisterRequest;
import com.xml.xiaobinnode.entity.User;
import com.xml.xiaobinnode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "用户注册、登录")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @NoAuth
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "手机号注册新用户")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", userService.register(request));
    }

    @NoAuth
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "手机号/邮箱 + 密码登录，返回JWT Token")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }
}

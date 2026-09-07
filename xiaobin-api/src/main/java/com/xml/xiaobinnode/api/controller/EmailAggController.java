package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.user.EmailFeignClient;
import com.xml.xiaobinnode.common.annotation.NoAuth;
import com.xml.xiaobinnode.common.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 聚合-邮件验证码
 */
@Tag(name = "聚合-邮件验证码", description = "邮箱验证码发送")
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailAggController {

    private final EmailFeignClient emailFeignClient;

    @NoAuth
    @PostMapping("/verify-code")
    @Operation(summary = "发送邮箱验证码")
    public Result<Void> sendVerifyCode(@RequestBody Map<String, String> request) {
        emailFeignClient.sendVerifyCode(request);
        return Result.success(null);
    }
}

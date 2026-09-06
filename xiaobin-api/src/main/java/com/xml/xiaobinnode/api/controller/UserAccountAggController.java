package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.user.UserFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 聚合-用户账号管理
 */
@Tag(name = "聚合-用户账号管理", description = "密码、手机号、实名认证、注销等")
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserAccountAggController {

    private final UserFeignClient userFeignClient;

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.changePassword(userId, request);
        return Result.success(null);
    }

    @PutMapping("/phone")
    @Operation(summary = "更换手机号")
    public Result<Void> changePhone(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.changePhone(userId, request);
        return Result.success(null);
    }

    @GetMapping("/real-name")
    @Operation(summary = "获取实名认证状态")
    public Result<Map<String, Object>> getRealNameStatus() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userFeignClient.getRealNameStatus(userId));
    }

    @PostMapping("/real-name")
    @Operation(summary = "提交实名认证")
    public Result<Void> submitRealName(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.submitRealName(userId, request);
        return Result.success(null);
    }

    @DeleteMapping
    @Operation(summary = "注销账号")
    public Result<Void> deleteAccount(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.deleteAccount(userId, body);
        return Result.success(null);
    }
}

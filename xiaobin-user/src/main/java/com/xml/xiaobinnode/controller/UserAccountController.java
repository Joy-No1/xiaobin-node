package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.dto.ChangeEmailRequest;
import com.xml.xiaobinnode.dto.ChangePasswordRequest;
import com.xml.xiaobinnode.dto.ChangePhoneRequest;
import com.xml.xiaobinnode.dto.RealNameRequest;
import com.xml.xiaobinnode.dto.RealNameStatusVO;
import com.xml.xiaobinnode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户账号管理接口
 */
@Tag(name = "用户账号管理", description = "密码、手机号、实名认证、注销等")
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userService.changePassword(userId, request);
    }

    @PutMapping("/phone")
    @Operation(summary = "更换手机号")
    public void changePhone(@Valid @RequestBody ChangePhoneRequest request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userService.changePhone(userId, request);
    }

    @PutMapping("/email")
    @Operation(summary = "更换邮箱")
    public void changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userService.changeEmail(userId, request);
    }

    @GetMapping("/real-name")
    @Operation(summary = "获取实名认证状态")
    public RealNameStatusVO getRealNameStatus() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return userService.getRealNameStatus(userId);
    }

    @PostMapping("/real-name")
    @Operation(summary = "提交实名认证")
    public void submitRealName(@Valid @RequestBody RealNameRequest request) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userService.submitRealName(userId, request);
    }

    @DeleteMapping
    @Operation(summary = "注销账号")
    public void deleteAccount(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(UserContext.getUserId());
        String password = body.get("password");
        userService.deleteAccount(userId, password);
    }
}

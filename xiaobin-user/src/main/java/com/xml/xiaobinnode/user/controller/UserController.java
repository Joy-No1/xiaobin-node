package com.xml.xiaobinnode.user.controller;

import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.user.entity.User;
import com.xml.xiaobinnode.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口", description = "个人信息管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<User> getCurrentUser() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新个人信息")
    public Result<User> updateUser(@RequestBody User user) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.updateUser(userId, user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查看用户主页")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }
}

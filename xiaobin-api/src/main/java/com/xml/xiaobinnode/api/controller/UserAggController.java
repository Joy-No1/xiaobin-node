package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.user.UserFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.UserProfileVO;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聚合-用户（当前用户信息 / 主页 / 查找 / 批量）
 */
@Tag(name = "聚合-用户")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAggController {

    private final UserFeignClient userFeignClient;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getCurrentUser() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userFeignClient.getCurrentUser(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public Result<Object> updateCurrentUser(@RequestBody Map<String, Object> userDTO) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userFeignClient.updateCurrentUser(userId, userDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查看用户主页")
    public Result<UserProfileVO> getUserById(@PathVariable Long id) {
        return Result.success(userFeignClient.getUserById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "按手机号查找用户")
    public Result<UserVO> searchByPhone(@RequestParam String phone) {
        return Result.success(userFeignClient.searchByPhone(phone));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量获取用户信息")
    public Result<List<UserVO>> getUsersByIds(@RequestBody List<Long> userIds) {
        return Result.success(userFeignClient.getUsersByIds(userIds));
    }
}

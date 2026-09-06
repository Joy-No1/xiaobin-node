package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.user.UserFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聚合-用户设备管理
 */
@Tag(name = "聚合-用户设备管理", description = "登录设备管理")
@RestController
@RequestMapping("/api/v1/users/me/devices")
@RequiredArgsConstructor
public class UserDeviceAggController {

    private final UserFeignClient userFeignClient;

    @GetMapping
    @Operation(summary = "获取我的设备列表")
    public Result<List<UserDeviceVO>> getMyDevices(@RequestParam(required = false) String currentDeviceId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userFeignClient.getUserDevices(userId, currentDeviceId));
    }

    @DeleteMapping("/{deviceId}")
    @Operation(summary = "删除设备（退出登录）")
    public Result<Void> removeDevice(@PathVariable Long deviceId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.removeDevice(userId, deviceId);
        return Result.success(null);
    }

    @PutMapping("/{deviceId}/status")
    @Operation(summary = "禁用/启用设备")
    public Result<Void> updateDeviceStatus(@PathVariable Long deviceId, @RequestParam Integer status) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userFeignClient.updateDeviceStatus(userId, deviceId, status);
        return Result.success(null);
    }
}

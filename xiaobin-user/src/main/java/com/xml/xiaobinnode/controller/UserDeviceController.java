package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.service.UserDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户设备管理接口
 */
@Tag(name = "用户设备管理", description = "登录设备管理")
@RestController
@RequestMapping("/users/me/devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping
    @Operation(summary = "获取我的设备列表")
    public List<UserDeviceVO> getMyDevices(@RequestParam(required = false) String currentDeviceId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return userDeviceService.getUserDevices(userId, currentDeviceId);
    }

    @DeleteMapping("/{deviceId}")
    @Operation(summary = "删除设备（退出登录）")
    public void removeDevice(@PathVariable Long deviceId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userDeviceService.removeDevice(userId, deviceId);
    }

    @PutMapping("/{deviceId}/status")
    @Operation(summary = "禁用/启用设备")
    public void updateDeviceStatus(@PathVariable Long deviceId, @RequestParam Integer status) {
        Long userId = Long.valueOf(UserContext.getUserId());
        userDeviceService.updateDeviceStatus(userId, deviceId, status);
    }
}

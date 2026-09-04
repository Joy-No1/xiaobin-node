package com.xml.xiaobinnode.chat.controller;

import com.xml.xiaobinnode.chat.service.OnlineStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 在线状态查询接口
 */
@Tag(name = "在线状态", description = "查询用户在线状态")
@RestController
@RequestMapping("/chat/online")
@RequiredArgsConstructor
public class OnlineStatusController {

    private final OnlineStatusService onlineStatusService;

    @GetMapping("/check/{userId}")
    @Operation(summary = "检查单个用户是否在线")
    public Map<String, Object> checkOnline(@PathVariable Long userId) {
        boolean online = onlineStatusService.isOnline(userId);
        return Map.of("userId", userId, "online", online);
    }

    @PostMapping("/batch-check")
    @Operation(summary = "批量检查用户在线状态")
    public Map<Long, Boolean> batchCheckOnline(@RequestBody List<Long> userIds) {
        return onlineStatusService.batchCheckOnline(userIds);
    }

    @GetMapping("/count")
    @Operation(summary = "获取在线用户数量")
    public Map<String, Object> getOnlineCount() {
        long count = onlineStatusService.getOnlineUserCount();
        return Map.of("count", count);
    }

    @GetMapping("/connections/{userId}")
    @Operation(summary = "获取用户的所有连接（支持多端登录）")
    public Map<String, Object> getUserConnections(@PathVariable Long userId) {
        return Map.of(
                "userId", userId,
                "connections", onlineStatusService.getUserConnections(userId)
        );
    }
}

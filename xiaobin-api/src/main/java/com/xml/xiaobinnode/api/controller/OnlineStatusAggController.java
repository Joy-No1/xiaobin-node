package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.chat.ChatFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聚合-在线状态（经 xiaobin-api 封装转发）
 */
@Tag(name = "聚合-在线状态", description = "用户在线状态查询")
@RestController
@RequestMapping("/api/v1/online")
@RequiredArgsConstructor
public class OnlineStatusAggController {

    private final ChatFeignClient chatFeignClient;

    @GetMapping("/check/{userId}")
    @Operation(summary = "检查单个用户是否在线")
    public Result<Map<String, Object>> checkOnline(@PathVariable Long userId) {
        return Result.success(chatFeignClient.checkOnline(userId));
    }

    @PostMapping("/batch-check")
    @Operation(summary = "批量检查用户在线状态")
    public Result<Map<Long, Boolean>> batchCheckOnline(@RequestBody List<Long> userIds) {
        return Result.success(chatFeignClient.batchCheckOnline(userIds));
    }

    @GetMapping("/count")
    @Operation(summary = "获取在线用户数量")
    public Result<Map<String, Object>> getOnlineCount() {
        return Result.success(chatFeignClient.getOnlineCount());
    }

    @GetMapping("/connections/{userId}")
    @Operation(summary = "获取用户的所有连接（支持多端登录）")
    public Result<Map<String, Object>> getUserConnections(@PathVariable Long userId) {
        return Result.success(chatFeignClient.getUserConnections(userId));
    }
}

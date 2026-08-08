package com.xml.xiaobinnode.community.controller;

import com.xml.xiaobinnode.api.community.dto.NotificationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.community.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知接口", description = "关注通知、未读通知")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "通知列表", description = "分页获取当前用户的通知，按时间倒序")
    public Result<PageResult<NotificationDTO>> getNotifications(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(notificationService.list(userId, page, size));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记已读", description = "将指定通知标记为已读")
    public Result<Void> markAsRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "未读通知数", description = "获取当前用户的未读通知数量（用于角标）")
    public Result<Long> getUnreadCount() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(notificationService.unreadCount(userId));
    }
}

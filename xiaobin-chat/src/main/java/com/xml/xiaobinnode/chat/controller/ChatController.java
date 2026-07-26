package com.xml.xiaobinnode.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天接口", description = "会话列表、消息历史")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @Operation(summary = "会话列表")
    public Result<List<Conversation>> getConversations() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(chatService.getConversations(userId));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "消息历史")
    public Result<PageResult<ChatMessage>> getMessages(@PathVariable String id,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessage> messagePage = chatService.getMessages(id, page, size);
        return Result.success(PageResult.of(page, size, messagePage.getTotal(), messagePage.getRecords()));
    }

    @PutMapping("/conversations/{id}/read")
    @Operation(summary = "标记已读")
    public Result<Void> markAsRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        chatService.markAsRead(Long.valueOf(id), userId);
        return Result.success();
    }
}

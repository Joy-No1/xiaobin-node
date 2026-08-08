package com.xml.xiaobinnode.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天接口", description = "会话列表、消息历史、发消息")
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

    @PostMapping("/conversations")
    @Operation(summary = "创建会话", description = "与目标用户创建会话（幂等，已存在则返回已有会话），仅互关用户可创建")
    public Result<Conversation> createConversation(@RequestParam Long targetUserId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        if (userId.equals(targetUserId)) {
            throw new BusinessException("不能与自己创建会话");
        }
        if (!chatService.isMutualFollow(userId, targetUserId)) {
            throw new BusinessException("仅互相关注的用户才能创建会话");
        }
        return Result.success(chatService.getOrCreateConversation(userId, targetUserId));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "消息历史")
    public Result<PageResult<ChatMessage>> getMessages(@PathVariable String id,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessage> messagePage = chatService.getMessages(id, page, size);
        return Result.success(PageResult.of(page, size, messagePage.getTotal(), messagePage.getRecords()));
    }

    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "发送消息", description = "在指定会话发送消息，接收方由会话自动推断")
    public Result<ChatMessage> sendMessage(@PathVariable String id, @RequestParam String content) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(chatService.sendMessageByConversation(Long.valueOf(id), userId, content));
    }

    @PutMapping("/conversations/{id}/read")
    @Operation(summary = "标记已读")
    public Result<Void> markAsRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        chatService.markAsRead(Long.valueOf(id), userId);
        return Result.success();
    }
}

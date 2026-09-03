package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.chat.ChatFeignClient;
import com.xml.xiaobinnode.common.dto.ChatMessageDTO;
import com.xml.xiaobinnode.common.dto.ConversationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天聚合接口（对外统一封装 Result，经 Feign 转发 xiaobin-chat）
 */
@Tag(name = "聚合-聊天", description = "会话/消息（经 xiaobin-api 封装转发）")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatAggController {

    private final ChatFeignClient chatFeignClient;

    @GetMapping("/conversations")
    @Operation(summary = "会话列表")
    public Result<List<ConversationDTO>> getConversations() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(chatFeignClient.getConversations(userId));
    }

    @PostMapping("/conversations")
    @Operation(summary = "创建会话")
    public Result<ConversationDTO> createConversation(@RequestParam Long targetUserId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(chatFeignClient.createConversation(userId, targetUserId));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "消息历史")
    public Result<PageResult<ChatMessageDTO>> getMessages(@PathVariable String id,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return Result.success(chatFeignClient.getMessages(id, page, size));
    }

    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "发送消息")
    public Result<ChatMessageDTO> sendMessage(@PathVariable String id,
                                              @RequestParam String content,
                                              @RequestParam(required = false, defaultValue = "TEXT") String messageType,
                                              @RequestParam(required = false) Integer duration) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(chatFeignClient.sendMessage(userId, id, content, messageType, duration));
    }

    @PutMapping("/conversations/{id}/read")
    @Operation(summary = "标记已读")
    public Result<Void> markAsRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        chatFeignClient.markAsRead(userId, id);
        return Result.success();
    }
}

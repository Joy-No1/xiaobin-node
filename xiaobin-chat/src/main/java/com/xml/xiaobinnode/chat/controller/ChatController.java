package com.xml.xiaobinnode.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.api.chat.dto.ChatMessageDTO;
import com.xml.xiaobinnode.api.chat.dto.ConversationDTO;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.common.dto.PageResult;
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
    public List<ConversationDTO> getConversations() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return chatService.getConversations(userId);
    }

    @PostMapping("/conversations")
    @Operation(summary = "创建会话", description = "与目标用户创建会话（幂等，已存在则返回已有会话），仅互关用户可创建")
    public ConversationDTO createConversation(@RequestParam Long targetUserId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        if (userId.equals(targetUserId)) {
            throw new BusinessException("不能与自己创建会话");
        }
        if (!chatService.isMutualFollow(userId, targetUserId)) {
            throw new BusinessException("仅互相关注的用户才能创建会话");
        }
        return chatService.getOrCreateConversationDTO(userId, targetUserId);
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "消息历史")
    public PageResult<ChatMessageDTO> getMessages(@PathVariable String id,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessage> messagePage = chatService.getMessages(id, page, size);
        List<ChatMessageDTO> records = messagePage.getRecords().stream()
                .map(this::toChatMessageDTO)
                .toList();
        return PageResult.of(page, size, messagePage.getTotal(), records);
    }

    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "发送消息", description = "在指定会话发送消息，接收方由会话成员自动推断。messageType可选：TEXT文字/IMAGE图片/VOICE语音/EMOJI表情，默认TEXT；语音需传duration时长（秒）")
    public ChatMessageDTO sendMessage(@PathVariable String id,
                                      @RequestParam String content,
                                      @RequestParam(required = false, defaultValue = "TEXT") String messageType,
                                      @RequestParam(required = false) Integer duration) {
        Long userId = Long.valueOf(UserContext.getUserId());
        ChatMessage message = chatService.sendMessageByConversation(Long.valueOf(id), userId, content, messageType, duration);
        return toChatMessageDTO(message);
    }

    @PutMapping("/conversations/{id}/read")
    @Operation(summary = "标记已读")
    public void markAsRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        chatService.markAsRead(Long.valueOf(id), userId);
    }

    private ChatMessageDTO toChatMessageDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setDuration(message.getDuration());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}

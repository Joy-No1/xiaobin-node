package com.xml.xiaobinnode.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.api.chat.dto.ConversationDTO;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;

import java.util.List;

public interface ChatService {

    ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType, Integer duration);

    /** 根据会话ID发送消息，接收方由会话成员自动推断（无需前端传receiverId） */
    ChatMessage sendMessageByConversation(Long conversationId, Long senderId, String content, String messageType, Integer duration);

    Conversation getOrCreateConversation(Long user1Id, Long user2Id);

    /** 创建/获取会话（幂等），返回面向当前用户组装的会话DTO（含对方用户信息） */
    ConversationDTO getOrCreateConversationDTO(Long userId, Long targetUserId);

    /** 我的会话列表（含我在会话中的未读/置顶/免打扰状态与私聊对方用户信息） */
    List<ConversationDTO> getConversations(Long userId);

    Page<ChatMessage> getMessages(String conversationId, int page, int size);

    void markAsRead(Long conversationId, Long userId);

    boolean isMutualFollow(Long user1Id, Long user2Id);
}

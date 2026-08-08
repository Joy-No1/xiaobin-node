package com.xml.xiaobinnode.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;

import java.util.List;

public interface ChatService {

    ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType);

    /** 根据会话ID发送消息，接收方由会话自动推断（无需前端传receiverId） */
    ChatMessage sendMessageByConversation(Long conversationId, Long senderId, String content);

    Conversation getOrCreateConversation(Long user1Id, Long user2Id);

    List<Conversation> getConversations(Long userId);

    Page<ChatMessage> getMessages(String conversationId, int page, int size);

    void markAsRead(Long conversationId, Long userId);

    boolean isMutualFollow(Long user1Id, Long user2Id);
}

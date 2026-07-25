package com.xml.xiaobinnode.chat.service;

import com.xml.xiaobinnode.chat.document.ChatMessage;
import com.xml.xiaobinnode.chat.document.Conversation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {

    /**
     * 发送消息
     */
    ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType);

    /**
     * 获取或创建会话
     */
    Conversation getOrCreateConversation(Long user1Id, Long user2Id);

    /**
     * 获取会话列表
     */
    List<Conversation> getConversations(Long userId);

    /**
     * 获取消息历史
     */
    Page<ChatMessage> getMessages(String conversationId, int page, int size);

    /**
     * 标记消息已读
     */
    void markAsRead(String conversationId, Long userId);

    /**
     * 检查是否为互关用户
     */
    boolean isMutualFollow(Long user1Id, Long user2Id);
}

package com.xml.xiaobinnode.chat.service.impl;

import com.xml.xiaobinnode.chat.document.ChatMessage;
import com.xml.xiaobinnode.chat.document.Conversation;
import com.xml.xiaobinnode.chat.repository.ChatMessageRepository;
import com.xml.xiaobinnode.chat.repository.ConversationRepository;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType) {
        // 检查是否为互关用户
        if (!isMutualFollow(senderId, receiverId)) {
            throw new BusinessException("仅互相关注的用户才能聊天");
        }

        // 获取或创建会话
        Conversation conversation = getOrCreateConversation(senderId, receiverId);

        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message = messageRepository.save(message);

        // 更新会话最后消息
        conversation.setLastMessage(content.length() > 50 ? content.substring(0, 50) + "..." : content);
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return message;
    }

    @Override
    public Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        // 确保 user1Id < user2Id 保证一致性
        Long small = Math.min(user1Id, user2Id);
        Long large = Math.max(user1Id, user2Id);

        Optional<Conversation> existing = conversationRepository.findByUser1IdAndUser2Id(small, large);
        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conversation = new Conversation();
        conversation.setUser1Id(small);
        conversation.setUser2Id(large);
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Override
    public List<Conversation> getConversations(Long userId) {
        return conversationRepository.findByUser1IdOrUser2IdOrderByUpdatedAtDesc(userId, userId);
    }

    @Override
    public Page<ChatMessage> getMessages(String conversationId, int page, int size) {
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, PageRequest.of(page - 1, size));
    }

    @Override
    public void markAsRead(String conversationId, Long userId) {
        // 标记该会话中发给该用户的消息为已读
        List<ChatMessage> unreadMessages = messageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversationId, PageRequest.of(0, 100))
                .getContent()
                .stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.getRead())
                .toList();

        unreadMessages.forEach(m -> {
            m.setRead(true);
            messageRepository.save(m);
        });
    }

    @Override
    public boolean isMutualFollow(Long user1Id, Long user2Id) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(
                        CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + user1Id,
                        String.valueOf(user2Id)));
    }
}

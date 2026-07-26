package com.xml.xiaobinnode.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;
import com.xml.xiaobinnode.chat.mapper.ChatMessageMapper;
import com.xml.xiaobinnode.chat.mapper.ConversationMapper;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType) {
        if (!isMutualFollow(senderId, receiverId)) {
            throw new BusinessException("仅互相关注的用户才能聊天");
        }

        Conversation conversation = getOrCreateConversation(senderId, receiverId);

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setIsRead(0);
        messageMapper.insert(message);

        conversation.setLastMessage(content.length() > 50 ? content.substring(0, 50) + "..." : content);
        conversation.setLastMessageTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        return message;
    }

    @Override
    public Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        long small = Math.min(user1Id, user2Id);
        long large = Math.max(user1Id, user2Id);

        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUser1Id, small)
               .eq(Conversation::getUser2Id, large);
        Conversation conversation = conversationMapper.selectOne(wrapper);

        if (conversation != null) {
            return conversation;
        }

        conversation = new Conversation();
        conversation.setUser1Id(small);
        conversation.setUser2Id(large);
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public List<Conversation> getConversations(Long userId) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUser1Id, userId)
               .or()
               .eq(Conversation::getUser2Id, userId)
               .orderByDesc(Conversation::getUpdatedAt);
        return conversationMapper.selectList(wrapper);
    }

    @Override
    public Page<ChatMessage> getMessages(String conversationId, int page, int size) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, Long.valueOf(conversationId))
               .orderByDesc(ChatMessage::getCreatedAt);
        return messageMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void markAsRead(Long conversationId, Long userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId)
               .eq(ChatMessage::getReceiverId, userId)
               .eq(ChatMessage::getIsRead, 0);
        List<ChatMessage> unread = messageMapper.selectList(wrapper);
        unread.forEach(m -> {
            m.setIsRead(1);
            messageMapper.updateById(m);
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

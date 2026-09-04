package com.xml.xiaobinnode.chat.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.common.dto.ConversationDTO;
import com.xml.xiaobinnode.api.feign.user.UserFeignClient;
import com.xml.xiaobinnode.chat.entity.ChatMessage;
import com.xml.xiaobinnode.chat.entity.Conversation;
import com.xml.xiaobinnode.chat.entity.ConversationMember;
import com.xml.xiaobinnode.chat.mapper.ChatMessageMapper;
import com.xml.xiaobinnode.chat.mapper.ConversationMapper;
import com.xml.xiaobinnode.chat.mapper.ConversationMemberMapper;
import com.xml.xiaobinnode.chat.service.ChatService;
import com.xml.xiaobinnode.chat.websocket.ChatWebSocketHandler;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.exception.BusinessException;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final ConversationMemberMapper memberMapper;
    private final UserFeignClient userFeignClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public ChatMessage sendMessage(Long senderId, Long receiverId, String content, String messageType, Integer duration) {
        if (!isMutualFollow(senderId, receiverId)) {
            throw new BusinessException("仅互相关注的用户才能聊天");
        }

        Conversation conversation = getOrCreateConversation(senderId, receiverId);

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setDuration(duration);
        messageMapper.insert(message);

        // 更新会话最后一条消息摘要
        Conversation update = new Conversation();
        update.setId(conversation.getId());
        update.setLastMessageId(message.getId());
        update.setLastMessage(buildPreview(message));
        update.setLastMessageTime(new Date());
        conversationMapper.updateById(update);

        // 接收方未读数 +1
        incrementUnread(conversation.getId(), receiverId);

        // 推送消息给接收者（Redis发布订阅 + 本地兜底）
        pushMessageToReceiver(message, senderId, receiverId);

        return message;
    }

    /**
     * 推送消息给接收者
     * 策略：优先使用Redis发布订阅（支持多实例），Redis失败时本地直接推送兜底
     */
    private void pushMessageToReceiver(ChatMessage message, Long senderId, Long receiverId) {
        Map<String, Object> pushMsg = new LinkedHashMap<>();
        pushMsg.put("type", "NEW_MESSAGE");
        pushMsg.put("messageId", message.getId());
        pushMsg.put("conversationId", message.getConversationId());
        pushMsg.put("senderId", senderId);
        pushMsg.put("content", message.getContent());
        pushMsg.put("messageType", message.getMessageType());
        pushMsg.put("duration", message.getDuration() != null ? message.getDuration() : 0);
        pushMsg.put("createdAt", message.getCreatedAt().toString());
        pushMsg.put("receiverId", receiverId);

        String jsonMessage = JSONUtil.toJsonStr(pushMsg);

        try {
            // 优先使用Redis发布订阅（支持多实例部署）
            redisTemplate.convertAndSend(CommonConstants.REDIS_CHANNEL_CHAT_MESSAGE, jsonMessage);
            log.debug("消息已发布到Redis频道: receiverId={}, messageId={}", receiverId, message.getId());
        } catch (Exception e) {
            // Redis发布失败时，本地直接推送兜底
            log.warn("Redis发布消息失败，使用本地推送兜底: receiverId={}", receiverId, e);
            boolean sent = localPushToReceiver(receiverId, jsonMessage);
            if (sent) {
                log.info("本地推送成功: receiverId={}, messageId={}", receiverId, message.getId());
            } else {
                log.debug("用户不在线，消息将在下次拉取时获取: receiverId={}", receiverId);
            }
        }

        // 推送会话更新通知（给发送者和接收者）
        pushConversationUpdate(message, senderId, receiverId);
    }

    /**
     * 推送会话更新通知
     * 当有新消息时，通知双方更新会话列表
     */
    private void pushConversationUpdate(ChatMessage message, Long senderId, Long receiverId) {
        Conversation conversation = conversationMapper.selectById(message.getConversationId());
        if (conversation == null) {
            return;
        }

        // 获取发送者和接收者的未读数
        ConversationMember senderMember = memberMapper.selectOne(
                new LambdaQueryWrapper<ConversationMember>()
                        .eq(ConversationMember::getConversationId, conversation.getId())
                        .eq(ConversationMember::getUserId, senderId));

        ConversationMember receiverMember = memberMapper.selectOne(
                new LambdaQueryWrapper<ConversationMember>()
                        .eq(ConversationMember::getConversationId, conversation.getId())
                        .eq(ConversationMember::getUserId, receiverId));

        // 给发送者推送（未读数为0）
        Map<String, Object> senderUpdate = buildConversationUpdateMessage(
                conversation, senderMember != null ? senderMember.getUnreadCount() : 0);
        publishConversationUpdate(senderId, senderUpdate);

        // 给接收者推送（未读数+1）
        Map<String, Object> receiverUpdate = buildConversationUpdateMessage(
                conversation, receiverMember != null ? receiverMember.getUnreadCount() : 0);
        publishConversationUpdate(receiverId, receiverUpdate);
    }

    private Map<String, Object> buildConversationUpdateMessage(Conversation conversation, Integer unreadCount) {
        Map<String, Object> update = new LinkedHashMap<>();
        update.put("type", "CONVERSATION_UPDATED");
        update.put("conversationId", conversation.getId());
        update.put("lastMessageId", conversation.getLastMessageId());
        update.put("lastMessage", conversation.getLastMessage());
        update.put("lastMessageTime", conversation.getLastMessageTime() != null ?
                conversation.getLastMessageTime().toString() : null);
        update.put("unreadCount", unreadCount);
        return update;
    }

    private void publishConversationUpdate(Long userId, Map<String, Object> updateMsg) {
        String jsonMessage = JSONUtil.toJsonStr(updateMsg);
        try {
            // 通过Redis发布（支持多实例）
            redisTemplate.convertAndSend(CommonConstants.REDIS_CHANNEL_CHAT_MESSAGE, jsonMessage);
        } catch (Exception e) {
            // 兜底：本地直接推送
            localPushToReceiver(userId, jsonMessage);
        }
    }

    /**
     * 本地直接推送（兜底方案，仅推送给当前实例的在线用户）
     */
    private boolean localPushToReceiver(Long receiverId, String jsonMessage) {
        Channel channel = ChatWebSocketHandler.getUserChannel(receiverId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(jsonMessage));
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ChatMessage sendMessageByConversation(Long conversationId, Long senderId, String content, String messageType, Integer duration) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        // 校验发送者是该会话成员，并推断接收方（当前仅 PRIVATE 会话，取另一位成员）
        Long receiverId = getOtherMemberId(conversationId, senderId);
        if (receiverId == null) {
            throw new BusinessException("无权在该会话发送消息");
        }
        return sendMessage(senderId, receiverId, content, messageType, duration);
    }

    /**
     * 构造会话列表展示的最后一条消息预览
     * 图片/语音等非文本消息不展示原始URL
     */
    private String buildPreview(ChatMessage message) {
        String preview;
        if ("IMAGE".equals(message.getMessageType())) {
            preview = "[图片]";
        } else if ("VOICE".equals(message.getMessageType())) {
            preview = "[语音]";
        } else {
            preview = message.getContent();
        }
        return preview.length() > 50 ? preview.substring(0, 50) + "..." : preview;
    }

    @Override
    @Transactional
    public Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        String privateKey = buildPrivateKey(user1Id, user2Id);

        Conversation conversation = selectByPrivateKey(privateKey);
        if (conversation != null) {
            return conversation;
        }

        try {
            conversation = new Conversation();
            conversation.setType("PRIVATE");
            conversation.setPrivateKey(privateKey);
            conversationMapper.insert(conversation);

            insertMember(conversation.getId(), user1Id);
            insertMember(conversation.getId(), user2Id);
            return conversation;
        } catch (DuplicateKeyException e) {
            // 并发下唯一键冲突，回查已有会话
            return selectByPrivateKey(privateKey);
        }
    }

    @Override
    @Transactional
    public ConversationDTO getOrCreateConversationDTO(Long userId, Long targetUserId) {
        Conversation conversation = getOrCreateConversation(userId, targetUserId);
        ConversationMember myMember = memberMapper.selectOne(
                new LambdaQueryWrapper<ConversationMember>()
                        .eq(ConversationMember::getConversationId, conversation.getId())
                        .eq(ConversationMember::getUserId, userId));
        UserVO otherUser = userFeignClient.getUserById(targetUserId);
        return buildConversationDTO(conversation, myMember, otherUser);
    }

    @Override
    public List<ConversationDTO> getConversations(Long userId) {
        List<ConversationMember> myMembers = memberMapper.selectList(
                new LambdaQueryWrapper<ConversationMember>()
                        .eq(ConversationMember::getUserId, userId));
        if (myMembers.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> conversationIds = myMembers.stream()
                .map(ConversationMember::getConversationId)
                .toList();
        List<Conversation> conversations = conversationMapper.selectBatchIds(conversationIds);
        Map<Long, Conversation> conversationMap = conversations.stream()
                .collect(Collectors.toMap(Conversation::getId, Function.identity(), (a, b) -> a));

        // 一次查全所有相关成员，按会话分组，用于找对方用户ID
        List<ConversationMember> allMembers = memberMapper.selectList(
                new LambdaQueryWrapper<ConversationMember>()
                        .in(ConversationMember::getConversationId, conversationIds));
        Map<Long, List<ConversationMember>> membersByConversation = allMembers.stream()
                .collect(Collectors.groupingBy(ConversationMember::getConversationId));

        Set<Long> otherUserIds = new HashSet<>();
        myMembers.forEach(m -> findOtherMember(membersByConversation.get(m.getConversationId()), userId)
                .ifPresent(cm -> otherUserIds.add(cm.getUserId())));

        Map<Long, UserVO> userMap = Optional.ofNullable(userFeignClient.getUsersByIds(otherUserIds.stream().toList()))
                .orElse(new ArrayList<>())
                .stream().collect(Collectors.toMap(UserVO::getId, Function.identity(), (a, b) -> a));

        List<ConversationDTO> result = new ArrayList<>();
        for (ConversationMember myMember : myMembers) {
            Conversation conversation = conversationMap.get(myMember.getConversationId());
            if (conversation == null) {
                continue;
            }
            UserVO otherUser = findOtherMember(membersByConversation.get(myMember.getConversationId()), userId)
                    .map(cm -> userMap.get(cm.getUserId()))
                    .orElse(null);
            result.add(buildConversationDTO(conversation, myMember, otherUser));
        }

        // 置顶优先，其次最后消息时间倒序（无消息的排最后）
        result.sort((a, b) -> {
            int pin = Integer.compare(b.getIsPinned(), a.getIsPinned());
            if (pin != 0) {
                return pin;
            }
            if (a.getLastMessageTime() == null && b.getLastMessageTime() == null) {
                return 0;
            }
            if (a.getLastMessageTime() == null) {
                return 1;
            }
            if (b.getLastMessageTime() == null) {
                return -1;
            }
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });
        return result;
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
        // 已读进度推进到会话中最大消息ID，未读数清零
        ChatMessage latest = messageMapper.selectOne(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getConversationId, conversationId)
                        .orderByDesc(ChatMessage::getId)
                        .last("LIMIT 1"));
        Long lastReadMessageId = latest != null ? latest.getId() : null;

        LambdaUpdateWrapper<ConversationMember> update = new LambdaUpdateWrapper<>();
        update.eq(ConversationMember::getConversationId, conversationId)
              .eq(ConversationMember::getUserId, userId)
              .set(ConversationMember::getLastReadMessageId, lastReadMessageId)
              .set(ConversationMember::getUnreadCount, 0);
        memberMapper.update(null, update);
    }

    @Override
    public boolean isMutualFollow(Long user1Id, Long user2Id) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(
                        CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + user1Id,
                        String.valueOf(user2Id)));
    }

    private String buildPrivateKey(Long user1Id, Long user2Id) {
        long small = Math.min(user1Id, user2Id);
        long large = Math.max(user1Id, user2Id);
        return small + "_" + large;
    }

    private Conversation selectByPrivateKey(String privateKey) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getType, "PRIVATE")
               .eq(Conversation::getPrivateKey, privateKey);
        return conversationMapper.selectOne(wrapper);
    }

    private ConversationDTO buildConversationDTO(Conversation conversation, ConversationMember myMember, UserVO otherUser) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType());
        dto.setLastMessageId(conversation.getLastMessageId());
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        if (myMember != null) {
            dto.setLastReadMessageId(myMember.getLastReadMessageId());
            dto.setUnreadCount(myMember.getUnreadCount());
            dto.setIsPinned(myMember.getIsPinned());
            dto.setIsMuted(myMember.getIsMuted());
        }
        dto.setOtherUser(otherUser);
        return dto;
    }

    private void insertMember(Long conversationId, Long userId) {
        ConversationMember member = new ConversationMember();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setUnreadCount(0);
        member.setIsPinned(0);
        member.setIsMuted(0);
        memberMapper.insert(member);
    }

    private void incrementUnread(Long conversationId, Long receiverId) {
        LambdaUpdateWrapper<ConversationMember> update = new LambdaUpdateWrapper<>();
        update.eq(ConversationMember::getConversationId, conversationId)
              .eq(ConversationMember::getUserId, receiverId)
              .setSql("unread_count = unread_count + 1");
        memberMapper.update(null, update);
    }

    /**
     * 会话中除指定用户外的另一位成员ID（当前仅 PRIVATE 会话）
     *
     * @return 不存在或用户非该会话成员时返回 null
     */
    private Long getOtherMemberId(Long conversationId, Long userId) {
        List<ConversationMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<ConversationMember>()
                        .eq(ConversationMember::getConversationId, conversationId));
        return findOtherMember(members, userId).map(ConversationMember::getUserId).orElse(null);
    }

    private Optional<ConversationMember> findOtherMember(List<ConversationMember> members, Long userId) {
        return Optional.ofNullable(members).orElse(List.of()).stream()
                .filter(cm -> !cm.getUserId().equals(userId))
                .findFirst();
    }
}

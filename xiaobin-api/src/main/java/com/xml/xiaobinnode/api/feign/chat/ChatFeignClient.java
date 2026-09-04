package com.xml.xiaobinnode.api.feign.chat;

import com.xml.xiaobinnode.common.dto.ChatMessageDTO;
import com.xml.xiaobinnode.common.dto.ConversationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 聊天服务 Feign 接口
 * <p>服务间调用直接返回裸数据，统一结果包装由网关完成。
 */
@FeignClient(name = "xiaobin-chat", path = "/chat")
public interface ChatFeignClient {

    /** 会话列表 */
    @GetMapping("/conversations")
    List<ConversationDTO> getConversations(@RequestHeader("X-User-Id") Long userId);

    /** 创建会话（幂等，仅互关用户可创建） */
    @PostMapping("/conversations")
    ConversationDTO createConversation(@RequestHeader("X-User-Id") Long userId,
                                       @RequestParam("targetUserId") Long targetUserId);

    /** 消息历史（倒序，前端需反转） */
    @GetMapping("/conversations/{id}/messages")
    PageResult<ChatMessageDTO> getMessages(@PathVariable("id") String conversationId,
                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size);

    /** 发送消息（接收方由会话自动推断，messageType 默认 TEXT，VOICE 需传 duration） */
    @PostMapping("/conversations/{id}/messages")
    ChatMessageDTO sendMessage(@RequestHeader("X-User-Id") Long userId,
                               @PathVariable("id") String conversationId,
                               @RequestParam("content") String content,
                               @RequestParam(value = "messageType", required = false, defaultValue = "TEXT") String messageType,
                               @RequestParam(value = "duration", required = false) Integer duration);

    /** 标记已读 */
    @PutMapping("/conversations/{id}/read")
    void markAsRead(@RequestHeader("X-User-Id") Long userId,
                    @PathVariable("id") String conversationId);

    /** 检查单个用户在线状态 */
    @GetMapping("/online/check/{userId}")
    Map<String, Object> checkOnline(@PathVariable("userId") Long userId);

    /** 批量检查用户在线状态 */
    @PostMapping("/online/batch-check")
    Map<Long, Boolean> batchCheckOnline(@RequestBody List<Long> userIds);

    /** 获取在线用户数量 */
    @GetMapping("/online/count")
    Map<String, Object> getOnlineCount();

    /** 获取用户的所有连接（多端登录） */
    @GetMapping("/online/connections/{userId}")
    Map<String, Object> getUserConnections(@PathVariable("userId") Long userId);
}

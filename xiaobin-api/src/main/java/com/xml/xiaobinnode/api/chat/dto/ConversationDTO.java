package com.xml.xiaobinnode.api.chat.dto;

import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.Data;

import java.util.Date;

/**
 * 会话DTO
 * <p>面向当前用户组装：会话本体 + 我在会话中的状态（未读/置顶/免打扰）+ 私聊对方用户信息。
 */
@Data
public class ConversationDTO {

    private Long id;

    /** 会话类型：PRIVATE-私聊 */
    private String type;

    /** 最后一条消息ID */
    private Long lastMessageId;

    /** 最后一条消息摘要 */
    private String lastMessage;

    /** 最后消息时间 */
    private Date lastMessageTime;

    private Date createdAt;

    private Date updatedAt;

    /** 我在会话中的最后已读消息ID */
    private Long lastReadMessageId;

    /** 我的未读消息数量 */
    private Integer unreadCount;

    /** 我是否置顶：0-否，1-是 */
    private Integer isPinned;

    /** 我是否免打扰：0-否，1-是 */
    private Integer isMuted;

    /** 私聊对方用户信息 */
    private UserVO otherUser;
}

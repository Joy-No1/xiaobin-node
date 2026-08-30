package com.xml.xiaobinnode.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 会话参与者实体：每个用户在会话中的独立状态（已读进度、未读、置顶、免打扰）
 */
@Data
@TableName("conversation_member")
public class ConversationMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Long userId;

    /** 最后已读消息ID */
    private Long lastReadMessageId;

    /** 未读消息数量 */
    private Integer unreadCount;

    /** 是否置顶：0-否，1-是 */
    private Integer isPinned;

    /** 是否免打扰：0-否，1-是 */
    private Integer isMuted;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}

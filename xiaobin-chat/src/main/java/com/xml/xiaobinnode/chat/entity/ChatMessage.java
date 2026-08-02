package com.xml.xiaobinnode.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 聊天消息实体
 */
@Data
@TableName("chat_message")
public class ChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private String messageType;

    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}

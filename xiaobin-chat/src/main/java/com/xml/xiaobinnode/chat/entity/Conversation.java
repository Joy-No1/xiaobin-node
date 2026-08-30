package com.xml.xiaobinnode.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;


/**
 * 聊天会话实体
 */
@Data
@TableName("conversation")
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话类型：PRIVATE-私聊 */
    private String type;

    /** 私聊幂等键：minUserId_maxUserId */
    private String privateKey;

    private Long lastMessageId;

    private String lastMessage;

    private Date lastMessageTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}

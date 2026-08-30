package com.xml.xiaobinnode.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 聊天消息实体
 */
@Data
@TableName("message")
public class ChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Long senderId;

    private String content;

    /** 消息类型: TEXT-文字, IMAGE-图片, VOICE-语音, EMOJI-表情 */
    private String messageType;

    /** 语音消息时长（秒），其他类型为null */
    private Integer duration;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}

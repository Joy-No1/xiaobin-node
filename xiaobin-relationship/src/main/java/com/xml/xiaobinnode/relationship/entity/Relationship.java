package com.xml.xiaobinnode.relationship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 情侣关系实体
 */
@Data
@TableName("relationship")
public class Relationship {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起方 ID */
    private Long initiatorId;

    /** 接收方 ID */
    private Long receiverId;

    /** 状态: PENDING-待确认, CONFIRMED-已确认, DISSOLVED-已解除 */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 确认时间 */
    private LocalDateTime confirmedAt;

    /** 解除时间 */
    private LocalDateTime dissolvedAt;
}

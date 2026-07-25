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

    /** 用户1 ID */
    private Long user1Id;

    /** 用户2 ID */
    private Long user2Id;

    /** 状态: PENDING-待确认, CONFIRMED-已确认, DISSOLVED-已解除 */
    private String status;

    /** 发起方用户ID */
    private Long initiatedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 确认时间 */
    private LocalDateTime confirmedAt;

    /** 解除时间 */
    private LocalDateTime dissolvedAt;
}

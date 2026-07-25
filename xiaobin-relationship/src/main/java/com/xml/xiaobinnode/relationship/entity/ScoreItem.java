package com.xml.xiaobinnode.relationship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自定义加减分项目实体
 */
@Data
@TableName("score_item")
public class ScoreItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 关系ID */
    private Long relationshipId;

    /** 项目名称 */
    private String itemName;

    /** 分数值（正数为加分，负数为扣分由type控制） */
    private Integer scoreValue;

    /** 类型: ADD-加分, SUBTRACT-扣分 */
    private String type;

    /** 图标 */
    private String icon;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

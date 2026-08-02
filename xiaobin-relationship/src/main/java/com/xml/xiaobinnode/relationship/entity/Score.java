package com.xml.xiaobinnode.relationship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 好感度分数实体
 */
@Data
@TableName("score")
public class Score {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关系ID */
    private Long relationshipId;

    /** 打分人ID */
    private Long scorerId;

    /** 被打分人ID */
    private Long targetId;

    /** 当前分数（初始100） */
    private Integer currentScore;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}

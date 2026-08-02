package com.xml.xiaobinnode.relationship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 打分记录实体
 */
@Data
@TableName("score_record")
public class ScoreRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关系ID */
    private Long relationshipId;

    /** 打分人ID */
    private Long scorerId;

    /** 被打分人ID */
    private Long targetId;

    /** 打分项目ID（可为空，表示手动输入原因） */
    private Long scoreItemId;

    /** 分数变化值 */
    private Integer scoreChange;

    /** 打分原因 */
    private String reason;

    /** 打分前分数 */
    private Integer scoreBefore;

    /** 打分后分数 */
    private Integer scoreAfter;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}

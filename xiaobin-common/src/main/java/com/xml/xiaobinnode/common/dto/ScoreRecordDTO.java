package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 打分记录DTO
 */
@Data
public class ScoreRecordDTO {

    private Long id;

    private Long relationshipId;

    /** 打分人 */
    private Long scorerId;

    /** 被打分人 */
    private Long targetId;

    private Long scoreItemId;

    /** 分数变化 */
    private Integer scoreChange;

    /** 原因 */
    private String reason;

    /** 打分前分数 */
    private Integer scoreBefore;

    /** 打分后分数 */
    private Integer scoreAfter;

    private Date createdAt;
}

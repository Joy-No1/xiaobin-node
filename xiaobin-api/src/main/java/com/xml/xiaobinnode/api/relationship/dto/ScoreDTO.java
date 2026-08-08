package com.xml.xiaobinnode.api.relationship.dto;

import lombok.Data;

/**
 * 好感度分数DTO
 */
@Data
public class ScoreDTO {

    private Long id;

    private Long relationshipId;

    /** 打分人 */
    private Long scorerId;

    /** 被打分人 */
    private Long targetId;

    /** 当前分数 */
    private Integer currentScore;
}

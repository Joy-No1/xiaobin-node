package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 加减分项DTO
 */
@Data
public class ScoreItemDTO {

    private Long id;

    private Long userId;

    private Long relationshipId;

    /** 项目名称 */
    private String itemName;

    /** 分值 */
    private Integer scoreValue;

    /** ADD / SUBTRACT */
    private String type;

    /** 图标 */
    private String icon;
}

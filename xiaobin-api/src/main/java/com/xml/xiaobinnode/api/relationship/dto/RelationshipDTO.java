package com.xml.xiaobinnode.api.relationship.dto;

import lombok.Data;

import java.util.Date;

/**
 * 情侣关系DTO
 */
@Data
public class RelationshipDTO {

    private Long id;

    /** 发起方ID */
    private Long initiatorId;

    /** 接收方ID */
    private Long receiverId;

    /** PENDING / CONFIRMED / DISSOLVED */
    private String status;

    private Date createdAt;

    private Date confirmedAt;

    private Date dissolvedAt;
}

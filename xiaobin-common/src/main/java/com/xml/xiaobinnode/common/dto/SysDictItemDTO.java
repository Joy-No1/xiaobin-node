package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 字典项DTO（对应 user 模块 SysDictItem 实体，供聚合转发）
 */
@Data
public class SysDictItemDTO {

    private Integer id;

    private String typeCode;

    private String itemCode;

    private String itemName;

    private String description;

    private Integer sortOrder;

    private String status;

    private Date createdAt;

    private Date updatedAt;
}

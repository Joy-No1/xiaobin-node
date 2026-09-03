package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 字典类型DTO（对应 user 模块 SysDictType 实体，供聚合转发）
 */
@Data
public class SysDictTypeDTO {

    private Integer id;

    private String typeCode;

    private String typeName;

    private String description;

    private Integer sortOrder;

    private String status;

    private Date createdAt;

    private Date updatedAt;
}

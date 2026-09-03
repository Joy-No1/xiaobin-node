package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 行政区划DTO（对应 user 模块 SysRegion 实体，供聚合转发）
 */
@Data
public class SysRegionDTO {

    private Integer id;

    private String code;

    private String name;

    private String parentCode;

    private String level;

    private String cityCode;
}

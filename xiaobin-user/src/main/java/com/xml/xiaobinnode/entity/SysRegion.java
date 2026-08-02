package com.xml.xiaobinnode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 中国行政区表
 * @TableName sys_region
 */
@TableName(value ="sys_region")
@Data
public class SysRegion implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 上级编码
     */
    @TableField(value = "parent_code")
    private String parentCode;

    /**
     * 省市区级别
     */
    @TableField(value = "level")
    private String level;

    /**
     * 城市行政编码
     */
    @TableField(value = "city_code")
    private String cityCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
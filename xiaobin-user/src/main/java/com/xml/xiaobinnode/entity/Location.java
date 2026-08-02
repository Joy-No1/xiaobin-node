package com.xml.xiaobinnode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户个人所在地信息表
 * @TableName location
 */
@TableName(value ="location")
@Data
public class Location implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 省级行政区名称
     */
    private String province;

    /**
     * 市级行政区名称
     */
    private String city;

    /**
     * 区县级行政区名称
     */
    private String district;

    /**
     * 创建时间
     */
    private Date created_at;

    /**
     * 更新时间
     */
    private Date updated_at;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
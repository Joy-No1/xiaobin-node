package com.xml.xiaobinnode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 字典项表
 * @TableName sys_dict_item
 */
@TableName(value ="sys_dict_item")
@Data
public class SysDictItem implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字典类型编码（关联sys_dict_type.type_code）
     */
    @TableField(value = "type_code")
    private String typeCode;

    /**
     * 字典项编码
     */
    @TableField(value = "item_code")
    private String itemCode;

    /**
     * 字典项名称
     */
    @TableField(value = "item_name")
    private String itemName;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 排序（升序）
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 状态: ACTIVE/DISABLED
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

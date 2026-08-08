package com.xml.xiaobinnode.relationship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 情侣关系实体
 */
@Data
@TableName("relationship")
public class Relationship {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起方 ID */
    private Long initiatorId;

    /** 接收方 ID */
    private Long receiverId;

    /** 状态: PENDING-待确认, CONFIRMED-已确认, DISSOLVED-已解除 REJECTED-已拒绝 EXPIRED-已过期 */
    private String status;

    /** 关系类型编码（关联sys_dict_item.item_code, type_code=RELATION_TYPE），例: COUPLE-情侣, BESTIE-闺蜜, BUDDY-死党 */
    private String relationType;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /** 确认时间 */
    private Date confirmedAt;

    /** 解除时间 */
    private Date dissolvedAt;
}

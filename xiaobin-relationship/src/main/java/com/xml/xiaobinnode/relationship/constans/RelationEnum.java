package com.xml.xiaobinnode.relationship.constans;

import lombok.Getter;

/**
 * 情侣关系状态枚举
 *
 * @author xiaobin
 */
@Getter
public enum RelationEnum {

    /** 待确认 —— 一方发起关系请求，等待对方确认 */

    PENDING("PENDING", "待确认"),

    /** 已确认 —— 对方同意，关系正式建立 */

    CONFIRMED("CONFIRMED", "已确认"),

    /** 已拒绝 —— 接收方明确拒绝关系请求 */

    REJECTED("REJECTED", "已拒绝"),

    /** 已过期 —— 关系请求超过有效期，未被处理 */

    EXPIRED("EXPIRED", "已过期"),

    /** 已解除 —— 已建立的关系被任一方解除 */

    DISSOLVED("DISSOLVED", "已解除");

    private final String code;
    private final String desc;

    RelationEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

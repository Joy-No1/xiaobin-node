package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 我的关系VO（含对方用户信息，对应 relationship 模块 RelationshipVO）
 */
@Data
public class RelationshipVO implements Serializable {

    private RelationshipDTO relationship;

    private UserVO userVO;
}

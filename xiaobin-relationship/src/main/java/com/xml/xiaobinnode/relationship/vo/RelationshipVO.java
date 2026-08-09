package com.xml.xiaobinnode.relationship.vo;

import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.relationship.entity.Relationship;
import lombok.Data;

import java.io.Serializable;

@Data
public class RelationshipVO implements Serializable {

    private Relationship relationship;
    private UserVO userVO;
}

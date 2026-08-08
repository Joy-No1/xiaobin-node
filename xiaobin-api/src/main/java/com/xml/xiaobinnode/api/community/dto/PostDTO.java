package com.xml.xiaobinnode.api.community.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 帖子DTO（用于Feign远程调用）
 */
@Data
public class PostDTO {

    private String id;

    private Long userId;

    private String content;

    private List<String> images;

    private String location;

    private Integer likeCount;

    private Integer commentCount;

    /** 是否被编辑过 */
    private Boolean isEdited;

    /** ACTIVE / DELETED */
    private String status;

    private Date createdAt;

    private Date updatedAt;
}

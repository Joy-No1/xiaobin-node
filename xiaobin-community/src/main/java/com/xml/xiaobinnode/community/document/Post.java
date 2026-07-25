package com.xml.xiaobinnode.community.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子文档 (MongoDB)
 */
@Data
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    private Long userId;

    private String content;

    private List<String> images;

    private String location;

    private Integer likeCount = 0;

    private Integer commentCount = 0;

    /** ACTIVE / DELETED */
    private String status = "ACTIVE";

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}

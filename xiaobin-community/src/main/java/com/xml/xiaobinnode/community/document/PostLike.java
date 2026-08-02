package com.xml.xiaobinnode.community.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 点赞文档 (MongoDB)
 */
@Data
@Document(collection = "post_likes")
public class PostLike {

    @Id
    private String id;

    private String postId;

    private Long userId;

    private Date createdAt = new Date();
}

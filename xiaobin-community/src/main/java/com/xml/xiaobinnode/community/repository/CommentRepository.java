package com.xml.xiaobinnode.community.repository;

import com.xml.xiaobinnode.community.document.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByPostIdOrderByCreatedAtAsc(String postId, Pageable pageable);

    long countByPostId(String postId);
}

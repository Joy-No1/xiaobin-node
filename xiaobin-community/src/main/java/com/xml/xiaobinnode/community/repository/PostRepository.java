package com.xml.xiaobinnode.community.repository;

import com.xml.xiaobinnode.community.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status, Pageable pageable);
}

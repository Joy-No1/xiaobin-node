package com.xml.xiaobinnode.community.repository;

import com.xml.xiaobinnode.community.document.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByPostIdOrderByCreatedAtAsc(String postId, Pageable pageable);

    long countByPostId(String postId);

    /** 获取帖子的最新N条评论 */
    List<Comment> findTop5ByPostIdOrderByCreatedAtDesc(String postId);

    /** 获取帖子的所有评论人ID（去重） */
    List<Comment> findByPostId(String postId);

    /** 获取某个用户的所有评论（时间倒序） */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}

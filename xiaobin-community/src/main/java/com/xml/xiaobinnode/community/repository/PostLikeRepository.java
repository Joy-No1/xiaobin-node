package com.xml.xiaobinnode.community.repository;

import com.xml.xiaobinnode.community.document.PostLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends MongoRepository<PostLike, String> {

    Optional<PostLike> findByPostIdAndUserId(String postId, Long userId);

    long countByPostId(String postId);

    void deleteByPostIdAndUserId(String postId, Long userId);

    /** 获取帖子的所有点赞 */
    List<PostLike> findByPostId(String postId);
}

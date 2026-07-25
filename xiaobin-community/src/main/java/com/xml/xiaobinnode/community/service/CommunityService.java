package com.xml.xiaobinnode.community.service;

import com.xml.xiaobinnode.community.document.Comment;
import com.xml.xiaobinnode.community.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {

    // 帖子
    Post createPost(Long userId, String content, List<String> images, String location);
    Post getPostById(String postId);
    Page<Post> getPostList(int page, int size);
    void deletePost(String postId, Long userId);

    // 点赞
    void likePost(String postId, Long userId);
    void unlikePost(String postId, Long userId);
    boolean isLiked(String postId, Long userId);

    // 评论
    Comment addComment(String postId, Long userId, String content, Long replyToUserId, String parentCommentId);
    void deleteComment(String commentId, Long userId);
    Page<Comment> getComments(String postId, int page, int size);

    // 关注
    void follow(Long followerId, Long followeeId);
    void unfollow(Long followerId, Long followeeId);
    boolean isMutualFollow(Long userId1, Long userId2);

    // 文件上传
    String uploadFile(MultipartFile file);
    List<String> uploadFiles(List<MultipartFile> files);
}

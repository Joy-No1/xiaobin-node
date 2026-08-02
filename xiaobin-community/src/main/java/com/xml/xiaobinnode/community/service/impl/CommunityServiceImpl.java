package com.xml.xiaobinnode.community.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.community.document.Comment;
import com.xml.xiaobinnode.community.document.Post;
import com.xml.xiaobinnode.community.document.PostLike;
import com.xml.xiaobinnode.community.entity.Follow;
import com.xml.xiaobinnode.community.mapper.FollowMapper;
import com.xml.xiaobinnode.community.repository.CommentRepository;
import com.xml.xiaobinnode.community.repository.PostLikeRepository;
import com.xml.xiaobinnode.community.repository.PostRepository;
import com.xml.xiaobinnode.community.service.CommunityService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final FollowMapper followMapper;
    private final MinioClient minioClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${minio.bucket:xiaobin}")
    private String bucketName;

    @Value("${minio.endpoint:http://127.0.0.1:9000}")
    private String endpoint;

    @Override
    public Post createPost(Long userId, String content, List<String> images, String location) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(images);
        post.setLocation(location);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus("ACTIVE");
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post = postRepository.save(post);
        log.info("帖子发布成功: postId={}, userId={}", post.getId(), userId);
        return post;
    }

    @Override
    public Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
    }

    @Override
    public Page<Post> getPostList(int page, int size) {
        return postRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", PageRequest.of(page - 1, size));
    }

    @Override
    public void deletePost(String postId, Long userId) {
        Post post = getPostById(postId);
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的帖子");
        }
        post.setStatus("DELETED");
        post.setUpdatedAt(new Date());
        postRepository.save(post);
    }

    @Override
    public void likePost(String postId, Long userId) {
        if (postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new BusinessException("已经点过赞了");
        }

        PostLike like = new PostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        postLikeRepository.save(like);

        // 更新帖子点赞数
        Post post = getPostById(postId);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        // 缓存
        String key = CommonConstants.REDIS_POST_LIKE_KEY + postId;
        redisTemplate.opsForSet().add(key, String.valueOf(userId));
    }

    @Override
    public void unlikePost(String postId, Long userId) {
        PostLike like = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new BusinessException("还未点赞"));

        postLikeRepository.delete(like);

        Post post = getPostById(postId);
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);

        String key = CommonConstants.REDIS_POST_LIKE_KEY + postId;
        redisTemplate.opsForSet().remove(key, String.valueOf(userId));
    }

    @Override
    public boolean isLiked(String postId, Long userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    @Override
    public Comment addComment(String postId, Long userId, String content,
                               Long replyToUserId, String parentCommentId) {
        Post post = getPostById(postId);

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setReplyToUserId(replyToUserId);
        comment.setParentCommentId(parentCommentId);
        comment.setCreatedAt(new Date());
        comment = commentRepository.save(comment);

        // 更新帖子评论数
        post.setCommentCount((int) commentRepository.countByPostId(postId));
        postRepository.save(post);

        log.info("评论成功: commentId={}, postId={}, userId={}", comment.getId(), postId, userId);
        return comment;
    }

    @Override
    public void deleteComment(String commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评论");
        }
        commentRepository.delete(comment);

        // 更新帖子评论数
        Post post = getPostById(comment.getPostId());
        post.setCommentCount((int) commentRepository.countByPostId(comment.getPostId()));
        postRepository.save(post);
    }

    @Override
    public Page<Comment> getComments(String postId, int page, int size) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, PageRequest.of(page - 1, size));
    }

    @Override
    @Transactional
    public void follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new BusinessException("不能关注自己");
        }

        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFolloweeId, followeeId);
        if (followMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已关注该用户");
        }

        // 检查对方是否已关注我 -> 互关
        LambdaQueryWrapper<Follow> reverseWrapper = new LambdaQueryWrapper<>();
        reverseWrapper.eq(Follow::getFollowerId, followeeId)
                .eq(Follow::getFolloweeId, followerId);
        boolean isMutual = followMapper.selectCount(reverseWrapper) > 0;

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        follow.setStatus(isMutual ? "MUTUAL" : "FOLLOWING");
        followMapper.insert(follow);

        if (isMutual) {
            // 更新对方的状态为MUTUAL
            Follow reverse = followMapper.selectOne(reverseWrapper);
            reverse.setStatus("MUTUAL");
            followMapper.updateById(reverse);

            // 缓存互关关系
            redisTemplate.opsForSet().add(CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + followerId, String.valueOf(followeeId));
            redisTemplate.opsForSet().add(CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + followeeId, String.valueOf(followerId));

            log.info("用户互相关注: user1={}, user2={}", followerId, followeeId);
        }
    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFolloweeId, followeeId);
        Follow follow = followMapper.selectOne(wrapper);
        if (follow == null) {
            throw new BusinessException("未关注该用户");
        }

        followMapper.deleteById(follow.getId());

        // 如果之前是互关，更新对方状态
        if ("MUTUAL".equals(follow.getStatus())) {
            LambdaQueryWrapper<Follow> reverseWrapper = new LambdaQueryWrapper<>();
            reverseWrapper.eq(Follow::getFollowerId, followeeId)
                    .eq(Follow::getFolloweeId, followerId);
            Follow reverse = followMapper.selectOne(reverseWrapper);
            if (reverse != null) {
                reverse.setStatus("FOLLOWING");
                followMapper.updateById(reverse);
            }

            // 清除互关缓存
            redisTemplate.opsForSet().remove(CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + followerId, String.valueOf(followeeId));
            redisTemplate.opsForSet().remove(CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + followeeId, String.valueOf(followerId));
        }
    }

    @Override
    public boolean isMutualFollow(Long userId1, Long userId2) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(CommonConstants.REDIS_MUTUAL_FOLLOW_KEY + userId1, String.valueOf(userId2)));
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String objectName = "images/" + datePath + "/" + IdUtil.fastSimpleUUID() + ext;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return endpoint + "/" + bucketName + "/" + objectName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadFile(file));
        }
        return urls;
    }
}

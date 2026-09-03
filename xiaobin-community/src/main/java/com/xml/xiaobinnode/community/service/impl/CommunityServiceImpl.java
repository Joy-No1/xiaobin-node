package com.xml.xiaobinnode.community.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xml.xiaobinnode.common.dto.FollowStatusDTO;
import com.xml.xiaobinnode.api.feign.user.UserFeignClient;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.community.document.Comment;
import com.xml.xiaobinnode.community.document.Notification;
import com.xml.xiaobinnode.community.document.Post;
import com.xml.xiaobinnode.community.document.PostLike;
import com.xml.xiaobinnode.community.dto.CommentVO;
import com.xml.xiaobinnode.community.dto.MyCommentVO;
import com.xml.xiaobinnode.community.dto.MyFollowVO;
import com.xml.xiaobinnode.community.dto.MyLikeVO;
import com.xml.xiaobinnode.community.dto.PostVO;
import com.xml.xiaobinnode.community.entity.Follow;
import com.xml.xiaobinnode.community.mapper.FollowMapper;
import com.xml.xiaobinnode.community.repository.CommentRepository;
import com.xml.xiaobinnode.community.repository.PostLikeRepository;
import com.xml.xiaobinnode.community.repository.PostRepository;
import com.xml.xiaobinnode.community.service.CommunityService;
import com.xml.xiaobinnode.community.service.NotificationService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private final StringRedisTemplate stringRedisTemplate;
    private final NotificationService notificationService;
    private final UserFeignClient userFeignClient;

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
    public Post editPost(String postId, Long userId, String content, List<String> images, String location) {
        Post post = getPostById(postId);
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("只能编辑自己的帖子");
        }
        if (!"ACTIVE".equals(post.getStatus())) {
            throw new BusinessException("帖子状态异常，无法编辑");
        }

        boolean changed = false;
        if (content != null && !content.equals(post.getContent())) {
            post.setContent(content);
            changed = true;
        }
        if (images != null) {
            post.setImages(images);
            changed = true;
        }
        if (location != null && !location.equals(post.getLocation())) {
            post.setLocation(location);
            changed = true;
        }

        if (changed) {
            post.setIsEdited(true);
            post.setUpdatedAt(new Date());
            post = postRepository.save(post);
            log.info("帖子编辑成功: postId={}, userId={}", postId, userId);
        }
        return post;
    }

    @Override
    public PostVO getPostVOById(String postId, Long currentUserId) {
        Post post = getPostById(postId);
        if (!"ACTIVE".equals(post.getStatus())) {
            throw new BusinessException("帖子不存在");
        }
        return buildPostVO(post, currentUserId);
    }

    @Override
    public PageResult<PostVO> getPostVOList(int page, int size, Long currentUserId) {
        Page<Post> postPage = postRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", PageRequest.of(page - 1, size));
        List<Post> posts = postPage.getContent();
        if (posts.isEmpty()) {
            return PageResult.of(page, size, postPage.getTotalElements(), Collections.emptyList());
        }

        List<PostVO> voList = posts.stream()
                .map(post -> buildPostVO(post, currentUserId))
                .collect(Collectors.toList());
        return PageResult.of(page, size, postPage.getTotalElements(), voList);
    }

    /**
     * 构建 PostVO（含点赞人、评论人信息）
     */
    private PostVO buildPostVO(Post post, Long currentUserId) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setContent(post.getContent());
        vo.setImages(post.getImages());
        vo.setLocation(post.getLocation());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setIsEdited(post.getIsEdited() != null ? post.getIsEdited() : false);
        vo.setStatus(post.getStatus());
        vo.setCreatedAt(post.getCreatedAt());
        vo.setUpdatedAt(post.getUpdatedAt());

        // 发帖人信息
        vo.setUser(fetchUserVO(post.getUserId()));

        // 当前用户是否已点赞
        vo.setIsLiked(currentUserId != null && isLiked(post.getId(), currentUserId));

        // 点赞人列表
        List<PostLike> likes = postLikeRepository.findByPostId(post.getId());
        if (!likes.isEmpty()) {
            List<Long> likerIds = likes.stream().map(PostLike::getUserId).collect(Collectors.toList());
            vo.setLikers(fetchUserVOs(likerIds));
        } else {
            vo.setLikers(Collections.emptyList());
        }

        // 最新评论列表（含评论人信息）
        List<Comment> recentComments = commentRepository.findTop5ByPostIdOrderByCreatedAtDesc(post.getId());
        if (!recentComments.isEmpty()) {
            List<CommentVO> commentVOs = new ArrayList<>();
            // 收集所有需要查询的用户ID
            Set<Long> userIds = new HashSet<>();
            for (Comment c : recentComments) {
                userIds.add(c.getUserId());
                if (c.getReplyToUserId() != null) {
                    userIds.add(c.getReplyToUserId());
                }
            }
            Map<Long, UserVO> userMap = fetchUserVOMap(new ArrayList<>(userIds));

            for (Comment c : recentComments) {
                CommentVO cvo = new CommentVO();
                cvo.setId(c.getId());
                cvo.setPostId(c.getPostId());
                cvo.setUserId(c.getUserId());
                cvo.setUser(userMap.get(c.getUserId()));
                cvo.setContent(c.getContent());
                cvo.setReplyToUserId(c.getReplyToUserId());
                cvo.setReplyToUser(c.getReplyToUserId() != null ? userMap.get(c.getReplyToUserId()) : null);
                cvo.setParentCommentId(c.getParentCommentId());
                cvo.setCreatedAt(c.getCreatedAt());
                commentVOs.add(cvo);
            }
            // 按时间正序排列（最新的在前）
            commentVOs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            vo.setRecentComments(commentVOs);
        } else {
            vo.setRecentComments(Collections.emptyList());
        }

        return vo;
    }

    /**
     * 调用用户服务获取单个用户信息
     */
    private UserVO fetchUserVO(Long userId) {
        try {
            return userFeignClient.getUserById(userId);
        } catch (Exception e) {
            log.warn("获取用户信息失败: userId={}", userId, e);
        }
        return null;
    }

    /**
     * 批量获取用户信息
     */
    private List<UserVO> fetchUserVOs(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<UserVO> users = userFeignClient.getUsersByIds(userIds);
            return users != null ? users : Collections.emptyList();
        } catch (Exception e) {
            log.warn("批量获取用户信息失败: userIds={}", userIds, e);
        }
        return Collections.emptyList();
    }

    /**
     * 批量获取用户信息，返回 Map<userId, UserVO>
     */
    private Map<Long, UserVO> fetchUserVOMap(List<Long> userIds) {
        List<UserVO> users = fetchUserVOs(userIds);
        return users.stream().collect(Collectors.toMap(UserVO::getId, u -> u, (a, b) -> a));
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

        // 发送关注通知（尽力而为，失败不影响关注本身）
        try {
            sendFollowNotification(followerId, followeeId);
        } catch (Exception e) {
            log.warn("发送关注通知失败: followerId={}, followeeId={}", followerId, followeeId, e);
        }
    }

    /**
     * 保存关注通知并发布Redis事件，供chat服务WebSocket实时推送
     */
    private void sendFollowNotification(Long followerId, Long followeeId) {
        // 获取关注者信息
        String followerNickname = "用户" + followerId;
        String followerAvatar = "";
        try {
            UserVO followerUser = userFeignClient.getUserById(followerId);
            if (followerUser != null) {
                followerNickname = followerUser.getNickname() != null ? followerUser.getNickname() : followerNickname;
                followerAvatar = followerUser.getAvatarUrl() != null ? followerUser.getAvatarUrl() : "";
            }
        } catch (Exception e) {
            log.warn("获取关注者信息失败: followerId={}", followerId, e);
        }

        String content = followerNickname + " 关注了你";

        // 持久化通知
        Notification notification = notificationService.save(followeeId, "FOLLOW", followerId, content);

        // 发布Redis事件（含用户信息，避免chat服务订阅线程做Feign调用）
        Map<String, Object> event = new HashMap<>();
        event.put("toUserId", followeeId);
        event.put("type", "NEW_FOLLOWER");
        event.put("notificationId", notification.getId());
        event.put("fromUserId", followerId);
        event.put("fromUserName", followerNickname);
        event.put("fromUserAvatar", followerAvatar);
        event.put("content", content);
        event.put("createdAt", notification.getCreatedAt().toString());

        stringRedisTemplate.convertAndSend(CommonConstants.REDIS_CHANNEL_FOLLOW_NOTIFICATION, JSONUtil.toJsonStr(event));
        log.info("关注通知已发布: toUserId={}, fromUserId={}, notificationId={}", followeeId, followerId, notification.getId());
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
    public FollowStatusDTO getFollowStatus(Long currentUserId, Long targetUserId) {
        FollowStatusDTO dto = new FollowStatusDTO();
        if (currentUserId == null || currentUserId.equals(targetUserId)) {
            dto.setIsFollowing(false);
            dto.setIsFollowedBy(false);
            dto.setFollowStatus("NONE");
            return dto;
        }

        // 我是否关注了对方
        LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(Follow::getFollowerId, currentUserId)
                .eq(Follow::getFolloweeId, targetUserId);
        boolean isFollowing = followMapper.selectCount(followWrapper) > 0;

        // 对方是否关注了我
        LambdaQueryWrapper<Follow> reverseWrapper = new LambdaQueryWrapper<>();
        reverseWrapper.eq(Follow::getFollowerId, targetUserId)
                .eq(Follow::getFolloweeId, currentUserId);
        boolean isFollowedBy = followMapper.selectCount(reverseWrapper) > 0;

        dto.setIsFollowing(isFollowing);
        dto.setIsFollowedBy(isFollowedBy);

        if (isFollowing && isFollowedBy) {
            dto.setFollowStatus("MUTUAL");
        } else if (isFollowing) {
            dto.setFollowStatus("FOLLOWING");
        } else if (isFollowedBy) {
            dto.setFollowStatus("FOLLOWER");
        } else {
            dto.setFollowStatus("NONE");
        }

        return dto;
    }

    @Override
    public List<MyFollowVO> getMyFollowing(Long userId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, userId)
                .orderByDesc(Follow::getCreatedAt);
        List<Follow> follows = followMapper.selectList(wrapper);
        if (follows.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> followeeIds = follows.stream().map(Follow::getFolloweeId).collect(Collectors.toList());
        Map<Long, UserVO> userMap = fetchUserVOMap(followeeIds);

        // 批量查反向关系：哪些被关注者也在关注我（互关）
        Set<Long> mutualIds = new HashSet<>();
        LambdaQueryWrapper<Follow> reverseWrapper = new LambdaQueryWrapper<>();
        reverseWrapper.in(Follow::getFollowerId, followeeIds)
                .eq(Follow::getFolloweeId, userId);
        List<Follow> reverseFollows = followMapper.selectList(reverseWrapper);
        reverseFollows.forEach(f -> mutualIds.add(f.getFollowerId()));

        List<MyFollowVO> result = new ArrayList<>();
        for (Follow f : follows) {
            MyFollowVO vo = new MyFollowVO();
            vo.setUser(userMap.get(f.getFolloweeId()));
            vo.setIsMutual(mutualIds.contains(f.getFolloweeId()));
            vo.setFollowedAt(f.getCreatedAt());
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<MyLikeVO> getMyLikes(Long userId) {
        List<PostLike> likes = postLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (likes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> postIds = likes.stream().map(PostLike::getPostId).distinct().collect(Collectors.toList());
        Map<String, Post> postMap = postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        List<Long> postUserIds = postMap.values().stream().map(Post::getUserId).distinct().collect(Collectors.toList());
        Map<Long, UserVO> userMap = fetchUserVOMap(postUserIds);

        List<MyLikeVO> result = new ArrayList<>();
        for (PostLike like : likes) {
            Post post = postMap.get(like.getPostId());
            // 帖子已删除或不存在则跳过
            if (post == null || !"ACTIVE".equals(post.getStatus())) {
                continue;
            }
            MyLikeVO vo = new MyLikeVO();
            vo.setPostId(post.getId());
            vo.setPostUser(userMap.get(post.getUserId()));
            vo.setContent(post.getContent());
            vo.setImages(post.getImages());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setIsEdited(post.getIsEdited() != null ? post.getIsEdited() : false);
            vo.setPostCreatedAt(post.getCreatedAt());
            vo.setLikedAt(like.getCreatedAt());
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<MyCommentVO> getMyComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> postIds = comments.stream().map(Comment::getPostId).distinct().collect(Collectors.toList());
        Map<String, Post> postMap = postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        List<Long> postUserIds = postMap.values().stream().map(Post::getUserId).distinct().collect(Collectors.toList());
        Map<Long, UserVO> userMap = fetchUserVOMap(postUserIds);

        List<MyCommentVO> result = new ArrayList<>();
        for (Comment c : comments) {
            Post post = postMap.get(c.getPostId());
            if (post == null) {
                continue;
            }
            MyCommentVO vo = new MyCommentVO();
            vo.setId(c.getId());
            vo.setPostId(c.getPostId());
            vo.setPostUser(userMap.get(post.getUserId()));
            vo.setPostContent(post.getContent());
            vo.setContent(c.getContent());
            vo.setCreatedAt(c.getCreatedAt());
            result.add(vo);
        }
        return result;
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

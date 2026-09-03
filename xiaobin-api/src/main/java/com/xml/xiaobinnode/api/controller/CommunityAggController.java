package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.community.CommunityFeignClient;
import com.xml.xiaobinnode.common.dto.CommentDTO;
import com.xml.xiaobinnode.common.dto.FollowStatusDTO;
import com.xml.xiaobinnode.common.dto.MyCommentDTO;
import com.xml.xiaobinnode.common.dto.MyFollowDTO;
import com.xml.xiaobinnode.common.dto.MyLikeDTO;
import com.xml.xiaobinnode.common.dto.NotificationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.PostDTO;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聚合-社区（帖子/评论/点赞/关注/通知/我的）
 */
@Tag(name = "聚合-社区")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunityAggController {

    private final CommunityFeignClient communityFeignClient;

    // ==================== 帖子 ====================

    @PostMapping("/posts")
    @Operation(summary = "发帖")
    public Result<PostDTO> createPost(@RequestParam String content,
                                      @RequestParam(required = false) List<String> images,
                                      @RequestParam(required = false) String location) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.createPost(userId, content, images, location));
    }

    @GetMapping("/posts")
    @Operation(summary = "帖子列表")
    public Result<PageResult<PostDTO>> postList(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getPostList(userId, page, size));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "帖子详情")
    public Result<PostDTO> post(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getPost(userId, id));
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "编辑帖子")
    public Result<PostDTO> editPost(@PathVariable String id,
                                    @RequestParam(required = false) String content,
                                    @RequestParam(required = false) List<String> images,
                                    @RequestParam(required = false) String location) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.editPost(userId, id, content, images, location));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "删除帖子")
    public Result<Void> deletePost(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.deletePost(userId, id);
        return Result.success();
    }

    // ==================== 点赞 ====================

    @PostMapping("/posts/{id}/likes")
    @Operation(summary = "点赞")
    public Result<Void> like(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.likePost(userId, id);
        return Result.success();
    }

    @DeleteMapping("/posts/{id}/likes")
    @Operation(summary = "取消点赞")
    public Result<Void> unlike(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.unlikePost(userId, id);
        return Result.success();
    }

    // ==================== 评论 ====================

    @PostMapping("/posts/{id}/comments")
    @Operation(summary = "发表评论")
    public Result<CommentDTO> addComment(@PathVariable String id,
                                         @RequestParam String content,
                                         @RequestParam(required = false) Long replyToUserId,
                                         @RequestParam(required = false) String parentCommentId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.addComment(userId, id, content, replyToUserId, parentCommentId));
    }

    @GetMapping("/posts/{id}/comments")
    @Operation(summary = "评论列表")
    public Result<PageResult<CommentDTO>> comments(@PathVariable String id,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return Result.success(communityFeignClient.getComments(id, page, size));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.deleteComment(userId, id);
        return Result.success();
    }

    // ==================== 关注 ====================

    @PostMapping("/users/{id}/follow")
    @Operation(summary = "关注")
    public Result<Void> follow(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.follow(userId, id);
        return Result.success();
    }

    @DeleteMapping("/users/{id}/follow")
    @Operation(summary = "取关")
    public Result<Void> unfollow(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.unfollow(userId, id);
        return Result.success();
    }

    @GetMapping("/users/{id}/follow-status")
    @Operation(summary = "关注状态")
    public Result<FollowStatusDTO> followStatus(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getFollowStatus(userId, id));
    }

    // ==================== 通知 ====================

    @GetMapping("/notifications")
    @Operation(summary = "通知列表")
    public Result<PageResult<NotificationDTO>> notifications(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getNotifications(userId, page, size));
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "未读通知数")
    public Result<Long> unreadCount() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getUnreadCount(userId));
    }

    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "标记通知已读")
    public Result<Void> markNotificationRead(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityFeignClient.markNotificationRead(userId, id);
        return Result.success();
    }

    // ==================== 我的 ====================

    @GetMapping("/me/following")
    @Operation(summary = "我的关注")
    public Result<List<MyFollowDTO>> myFollowing() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getMyFollowing(userId));
    }

    @GetMapping("/me/likes")
    @Operation(summary = "我赞过的帖子")
    public Result<List<MyLikeDTO>> myLikes() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getMyLikes(userId));
    }

    @GetMapping("/me/comments")
    @Operation(summary = "我的评论")
    public Result<List<MyCommentDTO>> myComments() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityFeignClient.getMyComments(userId));
    }
}

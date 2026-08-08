package com.xml.xiaobinnode.api.community;

import com.xml.xiaobinnode.api.community.dto.CommentDTO;
import com.xml.xiaobinnode.api.community.dto.FollowStatusDTO;
import com.xml.xiaobinnode.api.community.dto.MyCommentDTO;
import com.xml.xiaobinnode.api.community.dto.MyFollowDTO;
import com.xml.xiaobinnode.api.community.dto.MyLikeDTO;
import com.xml.xiaobinnode.api.community.dto.NotificationDTO;
import com.xml.xiaobinnode.api.community.dto.PostDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区服务 Feign 接口（帖子、评论、点赞、关注、通知）
 */
@FeignClient(name = "xiaobin-community", path = "/api/v1")
public interface CommunityFeignClient {

    // ==================== 帖子 ====================

    /** 发帖 */
    @PostMapping("/posts")
    Result<PostDTO> createPost(@RequestHeader("X-User-Id") Long userId,
                                @RequestParam("content") String content,
                                @RequestParam(value = "images", required = false) List<String> images,
                                @RequestParam(value = "location", required = false) String location);

    /** 帖子列表（分页） */
    @GetMapping("/posts")
    Result<PageResult<PostDTO>> getPostList(@RequestHeader("X-User-Id") Long userId,
                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size);

    /** 帖子详情 */
    @GetMapping("/posts/{id}")
    Result<PostDTO> getPost(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable("id") String id);

    /** 编辑帖子 */
    @PutMapping("/posts/{id}")
    Result<PostDTO> editPost(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable("id") String id,
                              @RequestParam(value = "content", required = false) String content,
                              @RequestParam(value = "images", required = false) List<String> images,
                              @RequestParam(value = "location", required = false) String location);

    /** 删除帖子 */
    @DeleteMapping("/posts/{id}")
    Result<Void> deletePost(@RequestHeader("X-User-Id") Long userId,
                             @PathVariable("id") String id);

    // ==================== 点赞 ====================

    /** 点赞 */
    @PostMapping("/posts/{id}/likes")
    Result<Void> likePost(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable("id") String id);

    /** 取消点赞 */
    @DeleteMapping("/posts/{id}/likes")
    Result<Void> unlikePost(@RequestHeader("X-User-Id") Long userId,
                             @PathVariable("id") String id);

    // ==================== 评论 ====================

    /** 发表评论 */
    @PostMapping("/posts/{id}/comments")
    Result<CommentDTO> addComment(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable("id") String postId,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "replyToUserId", required = false) Long replyToUserId,
                                    @RequestParam(value = "parentCommentId", required = false) String parentCommentId);

    /** 评论列表 */
    @GetMapping("/posts/{id}/comments")
    Result<PageResult<CommentDTO>> getComments(@PathVariable("id") String postId,
                                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @RequestParam(value = "size", defaultValue = "20") int size);

    /** 删除评论 */
    @DeleteMapping("/comments/{id}")
    Result<Void> deleteComment(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable("id") String commentId);

    // ==================== 关注 ====================

    /** 关注用户 */
    @PostMapping("/users/{id}/follow")
    Result<Void> follow(@RequestHeader("X-User-Id") Long userId,
                         @PathVariable("id") Long followeeId);

    /** 取消关注 */
    @DeleteMapping("/users/{id}/follow")
    Result<Void> unfollow(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable("id") Long followeeId);

    /** 查询当前用户对指定用户的关注状态 */
    @GetMapping("/users/{id}/follow-status")
    Result<FollowStatusDTO> getFollowStatus(@RequestHeader("X-User-Id") Long currentUserId,
                                             @PathVariable("id") Long targetUserId);

    // ==================== 通知 ====================

    /** 通知列表（分页） */
    @GetMapping("/notifications")
    Result<PageResult<NotificationDTO>> getNotifications(@RequestHeader("X-User-Id") Long userId,
                                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @RequestParam(value = "size", defaultValue = "20") int size);

    /** 未读通知数 */
    @GetMapping("/notifications/unread-count")
    Result<Long> getUnreadCount(@RequestHeader("X-User-Id") Long userId);

    /** 标记通知已读 */
    @PutMapping("/notifications/{id}/read")
    Result<Void> markNotificationRead(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable("id") String notificationId);

    // ==================== 我的查询 ====================

    /** 我的关注列表 */
    @GetMapping("/me/following")
    Result<List<MyFollowDTO>> getMyFollowing(@RequestHeader("X-User-Id") Long userId);

    /** 我的点赞帖子列表 */
    @GetMapping("/me/likes")
    Result<List<MyLikeDTO>> getMyLikes(@RequestHeader("X-User-Id") Long userId);

    /** 我的评论列表 */
    @GetMapping("/me/comments")
    Result<List<MyCommentDTO>> getMyComments(@RequestHeader("X-User-Id") Long userId);
}

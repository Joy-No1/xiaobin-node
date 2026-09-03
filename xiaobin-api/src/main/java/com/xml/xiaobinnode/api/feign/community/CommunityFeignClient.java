package com.xml.xiaobinnode.api.feign.community;

import com.xml.xiaobinnode.api.config.CommunityUploadConfig;
import com.xml.xiaobinnode.common.dto.CommentDTO;
import com.xml.xiaobinnode.common.dto.FollowStatusDTO;
import com.xml.xiaobinnode.common.dto.MyCommentDTO;
import com.xml.xiaobinnode.common.dto.MyFollowDTO;
import com.xml.xiaobinnode.common.dto.MyLikeDTO;
import com.xml.xiaobinnode.common.dto.NotificationDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.PostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 社区服务 Feign 接口（帖子、评论、点赞、关注、通知）
 * <p>服务间调用直接返回裸数据，统一结果包装由网关完成。
 */
@FeignClient(name = "xiaobin-community", path = "/", configuration = CommunityUploadConfig.class)
public interface CommunityFeignClient {

    // ==================== 帖子 ====================

    /** 发帖 */
    @PostMapping("/posts")
    PostDTO createPost(@RequestHeader("X-User-Id") Long userId,
                       @RequestParam("content") String content,
                       @RequestParam(value = "images", required = false) List<String> images,
                       @RequestParam(value = "location", required = false) String location);

    /** 帖子列表（分页） */
    @GetMapping("/posts")
    PageResult<PostDTO> getPostList(@RequestHeader("X-User-Id") Long userId,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size);

    /** 帖子详情 */
    @GetMapping("/posts/{id}")
    PostDTO getPost(@RequestHeader("X-User-Id") Long userId,
                    @PathVariable("id") String id);

    /** 编辑帖子 */
    @PutMapping("/posts/{id}")
    PostDTO editPost(@RequestHeader("X-User-Id") Long userId,
                     @PathVariable("id") String id,
                     @RequestParam(value = "content", required = false) String content,
                     @RequestParam(value = "images", required = false) List<String> images,
                     @RequestParam(value = "location", required = false) String location);

    /** 删除帖子 */
    @DeleteMapping("/posts/{id}")
    void deletePost(@RequestHeader("X-User-Id") Long userId,
                    @PathVariable("id") String id);

    // ==================== 点赞 ====================

    /** 点赞 */
    @PostMapping("/posts/{id}/likes")
    void likePost(@RequestHeader("X-User-Id") Long userId,
                  @PathVariable("id") String id);

    /** 取消点赞 */
    @DeleteMapping("/posts/{id}/likes")
    void unlikePost(@RequestHeader("X-User-Id") Long userId,
                    @PathVariable("id") String id);

    // ==================== 评论 ====================

    /** 发表评论 */
    @PostMapping("/posts/{id}/comments")
    CommentDTO addComment(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable("id") String postId,
                          @RequestParam("content") String content,
                          @RequestParam(value = "replyToUserId", required = false) Long replyToUserId,
                          @RequestParam(value = "parentCommentId", required = false) String parentCommentId);

    /** 评论列表 */
    @GetMapping("/posts/{id}/comments")
    PageResult<CommentDTO> getComments(@PathVariable("id") String postId,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "size", defaultValue = "20") int size);

    /** 删除评论 */
    @DeleteMapping("/comments/{id}")
    void deleteComment(@RequestHeader("X-User-Id") Long userId,
                       @PathVariable("id") String commentId);

    // ==================== 关注 ====================

    /** 关注用户 */
    @PostMapping("/users/{id}/follow")
    void follow(@RequestHeader("X-User-Id") Long userId,
                @PathVariable("id") Long followeeId);

    /** 取消关注 */
    @DeleteMapping("/users/{id}/follow")
    void unfollow(@RequestHeader("X-User-Id") Long userId,
                  @PathVariable("id") Long followeeId);

    /** 查询当前用户对指定用户的关注状态 */
    @GetMapping("/users/{id}/follow-status")
    FollowStatusDTO getFollowStatus(@RequestHeader("X-User-Id") Long currentUserId,
                                    @PathVariable("id") Long targetUserId);

    // ==================== 通知 ====================

    /** 通知列表（分页） */
    @GetMapping("/notifications")
    PageResult<NotificationDTO> getNotifications(@RequestHeader("X-User-Id") Long userId,
                                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                                 @RequestParam(value = "size", defaultValue = "20") int size);

    /** 未读通知数 */
    @GetMapping("/notifications/unread-count")
    Long getUnreadCount(@RequestHeader("X-User-Id") Long userId);

    /** 标记通知已读 */
    @PutMapping("/notifications/{id}/read")
    void markNotificationRead(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable("id") String notificationId);

    // ==================== 我的查询 ====================

    /** 我的关注列表 */
    @GetMapping("/me/following")
    List<MyFollowDTO> getMyFollowing(@RequestHeader("X-User-Id") Long userId);

    /** 我的点赞帖子列表 */
    @GetMapping("/me/likes")
    List<MyLikeDTO> getMyLikes(@RequestHeader("X-User-Id") Long userId);

    /** 我的评论列表 */
    @GetMapping("/me/comments")
    List<MyCommentDTO> getMyComments(@RequestHeader("X-User-Id") Long userId);

    // ==================== 文件上传 ====================

    /** 单文件上传（返回URL） */
    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("file") MultipartFile file);

    /** 批量上传（返回URL列表） */
    @PostMapping(value = "/files/upload-batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<String> uploadFiles(@RequestPart("files") List<MultipartFile> files);
}

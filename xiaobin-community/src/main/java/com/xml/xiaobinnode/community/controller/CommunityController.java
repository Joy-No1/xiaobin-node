package com.xml.xiaobinnode.community.controller;

import com.xml.xiaobinnode.api.community.dto.FollowStatusDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.community.document.Comment;
import com.xml.xiaobinnode.community.document.Post;
import com.xml.xiaobinnode.community.dto.PostVO;
import com.xml.xiaobinnode.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "社区接口", description = "广场发帖、评论、点赞、关注、文件上传")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // ==================== 帖子 ====================

    @PostMapping("/posts")
    @Operation(summary = "发帖", description = "发布广场帖子，支持文字和图片")
    public Result<Post> createPost(@RequestParam String content,
                                    @RequestParam(required = false) List<String> images,
                                    @RequestParam(required = false) String location) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.createPost(userId, content, images, location));
    }

    @GetMapping("/posts")
    @Operation(summary = "帖子列表", description = "分页获取广场帖子列表（含点赞人、评论人）")
    public Result<PageResult<PostVO>> getPostList(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.getPostVOList(page, size, userId));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "帖子详情", description = "获取帖子详情（含点赞人、评论人、是否被编辑）")
    public Result<PostVO> getPost(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.getPostVOById(id, userId));
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "编辑帖子", description = "编辑帖子内容（仅作者可操作），编辑后标记 isEdited=true")
    public Result<Post> editPost(@PathVariable String id,
                                  @RequestParam(required = false) String content,
                                  @RequestParam(required = false) List<String> images,
                                  @RequestParam(required = false) String location) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.editPost(id, userId, content, images, location));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "删除帖子")
    public Result<Void> deletePost(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.deletePost(id, userId);
        return Result.success();
    }

    // ==================== 点赞 ====================

    @PostMapping("/posts/{id}/likes")
    @Operation(summary = "点赞")
    public Result<Void> likePost(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.likePost(id, userId);
        return Result.success();
    }

    @DeleteMapping("/posts/{id}/likes")
    @Operation(summary = "取消点赞")
    public Result<Void> unlikePost(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.unlikePost(id, userId);
        return Result.success();
    }

    // ==================== 评论 ====================

    @PostMapping("/posts/{id}/comments")
    @Operation(summary = "评论", description = "对帖子发表评论，支持回复评论")
    public Result<Comment> addComment(@PathVariable String id,
                                       @RequestParam String content,
                                       @RequestParam(required = false) Long replyToUserId,
                                       @RequestParam(required = false) String parentCommentId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.addComment(id, userId, content, replyToUserId, parentCommentId));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(@PathVariable String id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.deleteComment(id, userId);
        return Result.success();
    }

    @GetMapping("/posts/{id}/comments")
    @Operation(summary = "评论列表")
    public Result<PageResult<Comment>> getComments(@PathVariable String id,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Page<Comment> commentPage = communityService.getComments(id, page, size);
        return Result.success(PageResult.of(page, size, commentPage.getTotalElements(), commentPage.getContent()));
    }

    // ==================== 关注 ====================

    @PostMapping("/users/{id}/follow")
    @Operation(summary = "关注用户")
    public Result<Void> follow(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.follow(userId, id);
        return Result.success();
    }

    @DeleteMapping("/users/{id}/follow")
    @Operation(summary = "取消关注")
    public Result<Void> unfollow(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        communityService.unfollow(userId, id);
        return Result.success();
    }

    @GetMapping("/users/{id}/follow-status")
    @Operation(summary = "查询关注状态", description = "查询当前用户对指定用户的关注状态（是否关注、是否被关注、互关）")
    public Result<FollowStatusDTO> getFollowStatus(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(communityService.getFollowStatus(userId, id));
    }

    // ==================== 文件上传 ====================

    @PostMapping("/files/upload")
    @Operation(summary = "上传文件", description = "上传图片到MinIO对象存储")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.success(communityService.uploadFile(file));
    }

    @PostMapping("/files/upload-batch")
    @Operation(summary = "批量上传", description = "批量上传图片")
    public Result<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return Result.success(communityService.uploadFiles(files));
    }
}

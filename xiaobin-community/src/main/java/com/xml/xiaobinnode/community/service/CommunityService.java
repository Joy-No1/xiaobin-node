package com.xml.xiaobinnode.community.service;

import com.xml.xiaobinnode.common.dto.FollowStatusDTO;
import com.xml.xiaobinnode.common.dto.PageResult;
import com.xml.xiaobinnode.community.document.Comment;
import com.xml.xiaobinnode.community.document.Post;
import com.xml.xiaobinnode.community.dto.MyCommentVO;
import com.xml.xiaobinnode.community.dto.MyFollowVO;
import com.xml.xiaobinnode.community.dto.MyLikeVO;
import com.xml.xiaobinnode.community.dto.PostVO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {

    // 帖子
    Post createPost(Long userId, String content, List<String> images, String location);
    Post getPostById(String postId);
    Page<Post> getPostList(int page, int size);
    void deletePost(String postId, Long userId);

    /** 编辑帖子（仅作者可操作），编辑后标记 isEdited=true */
    Post editPost(String postId, Long userId, String content, List<String> images, String location);

    /** 获取帖子详情VO（含点赞人、评论人） */
    PostVO getPostVOById(String postId, Long currentUserId);

    /** 获取帖子列表VO（含点赞人、评论人、分页信息） */
    PageResult<PostVO> getPostVOList(int page, int size, Long currentUserId);

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

    /** 获取当前用户对目标用户的关注状态 */
    FollowStatusDTO getFollowStatus(Long currentUserId, Long targetUserId);

    /** 我的关注列表 */
    List<MyFollowVO> getMyFollowing(Long userId);

    /** 我的点赞帖子列表 */
    List<MyLikeVO> getMyLikes(Long userId);

    /** 我的评论列表 */
    List<MyCommentVO> getMyComments(Long userId);

    // 文件上传
    String uploadFile(MultipartFile file);
    List<String> uploadFiles(List<MultipartFile> files);
}

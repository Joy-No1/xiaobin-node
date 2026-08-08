package com.xml.xiaobinnode.relationship.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.relationship.entity.*;
import com.xml.xiaobinnode.relationship.service.RelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "关系接口", description = "情侣关系管理与好感度打分")
@RestController
@RequestMapping("/api/v1/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping
    @Operation(summary = "发起关系请求", description = "向对方发起关系请求，可指定关系类型（默认情侣）")
    public Result<Relationship> createRelationship(@RequestParam Long targetUserId,
                                                   @RequestParam(required = false, defaultValue = "COUPLE") String relationType) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.createRelationship(userId, targetUserId, relationType));
    }

    @GetMapping("/received")
    @Operation(summary = "接收到的关系请求", description = "查看自己接收到的关系请求")
    public Result<List<Relationship>> receivedRelationship() {
        Long userId = Long.valueOf(UserContext.getUserId());
        List<Relationship> relationshipList = relationshipService.getReceived(userId);
        return Result.success(relationshipList);
    }

    @GetMapping("/sent")
    @Operation(summary = "发送出去的关系请求", description = "查看自己发送的关系请求")
    public Result<List<Relationship>> sentRelationship() {
        Long userId = Long.valueOf(UserContext.getUserId());
        List<Relationship> relationshipList = relationshipService.getSent(userId);
        return Result.success(relationshipList);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认关系", description = "确认对方发起的情侣关系请求")
    public Result<Relationship> confirmRelationship(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.confirmRelationship(id, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "解除关系", description = "解除已确认的情侣关系")
    public Result<Void> dissolveRelationship(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        relationshipService.dissolveRelationship(id, userId);
        return Result.success();
    }

    @GetMapping("/me")
    @Operation(summary = "获取我的关系", description = "获取当前登录用户的已确认关系")
    public Result<List<Relationship>> getMyRelationship() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.getMyRelationship(userId));
    }

    @GetMapping("/{id}/scores")
    @Operation(summary = "查看好感度", description = "查看关系双方的好感度分数")
    public Result<Score> getScores(@PathVariable Long id) {
        return Result.success(relationshipService.getScore(id));
    }

    @PostMapping("/{id}/scores")
    @Operation(summary = "打分", description = "给对方打分（需使用自定义打分项目）")
    public Result<ScoreRecord> score(@PathVariable Long id,
                                     @RequestParam Long scoreItemId,
                                     @RequestParam(required = false) String reason) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.score(id, userId, scoreItemId, reason));
    }

    @GetMapping("/{id}/score-items")
    @Operation(summary = "获取自定义加减分项", description = "获取当前用户在关系中的自定义加减分项")
    public Result<java.util.List<ScoreItem>> getScoreItems(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.getScoreItems(id, userId));
    }

    @PostMapping("/{id}/score-items")
    @Operation(summary = "创建加减分项", description = "创建自定义加分或扣分项目")
    public Result<ScoreItem> createScoreItem(@PathVariable Long id, @Valid @RequestBody ScoreItem item) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.createScoreItem(userId, id, item));
    }

    @PutMapping("/{id}/score-items/{itemId}")
    @Operation(summary = "修改加减分项")
    public Result<ScoreItem> updateScoreItem(@PathVariable Long id,
                                             @PathVariable Long itemId,
                                             @RequestBody ScoreItem item) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipService.updateScoreItem(itemId, userId, item));
    }

    @DeleteMapping("/{id}/score-items/{itemId}")
    @Operation(summary = "删除加减分项")
    public Result<Void> deleteScoreItem(@PathVariable Long id, @PathVariable Long itemId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        relationshipService.deleteScoreItem(itemId, userId);
        return Result.success();
    }

    @GetMapping("/{id}/score-records")
    @Operation(summary = "打分记录", description = "分页查询关系的打分历史记录")
    public Result<Page<ScoreRecord>> getScoreRecords(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return Result.success(relationshipService.getScoreRecords(id, page, size));
    }
}

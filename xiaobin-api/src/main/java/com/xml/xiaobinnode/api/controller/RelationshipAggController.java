package com.xml.xiaobinnode.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.api.feign.relationship.RelationshipFeignClient;
import com.xml.xiaobinnode.common.dto.RelationshipDTO;
import com.xml.xiaobinnode.common.dto.RelationshipVO;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.ScoreDTO;
import com.xml.xiaobinnode.common.dto.ScoreItemDTO;
import com.xml.xiaobinnode.common.dto.ScoreRecordDTO;
import com.xml.xiaobinnode.common.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聚合-关系（关系 + 好感度 + 加减分项 + 打分记录）
 */
@Tag(name = "聚合-关系")
@RestController
@RequestMapping("/api/v1/relationships")
@RequiredArgsConstructor
public class RelationshipAggController {

    private final RelationshipFeignClient relationshipFeignClient;

    @PostMapping
    @Operation(summary = "发起关系")
    public Result<RelationshipDTO> create(@RequestParam Long targetUserId,
                                          @RequestParam(required = false, defaultValue = "COUPLE") String relationType) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.createRelationship(userId, targetUserId, relationType));
    }

    @GetMapping("/received")
    @Operation(summary = "接收到的关系请求")
    public Result<List<RelationshipDTO>> received() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.getReceived(userId));
    }

    @GetMapping("/sent")
    @Operation(summary = "发送出去的关系请求")
    public Result<List<RelationshipDTO>> sent() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.getSent(userId));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认关系")
    public Result<RelationshipDTO> confirm(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.confirmRelationship(userId, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "解除关系")
    public Result<Void> dissolve(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        relationshipFeignClient.dissolveRelationship(userId, id);
        return Result.success();
    }

    @GetMapping("/me")
    @Operation(summary = "我的关系")
    public Result<List<RelationshipVO>> myRelationship() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.getMyRelationship(userId));
    }

    @GetMapping("/{id}/scores")
    @Operation(summary = "查看好感度")
    public Result<ScoreDTO> scores(@PathVariable Long id) {
        return Result.success(relationshipFeignClient.getScores(id));
    }

    @PostMapping("/{id}/scores")
    @Operation(summary = "打分")
    public Result<ScoreRecordDTO> score(@PathVariable Long id,
                                        @RequestParam Long scoreItemId,
                                        @RequestParam(required = false) String reason) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.score(userId, id, scoreItemId, reason));
    }

    @GetMapping("/{id}/score-items")
    @Operation(summary = "加减分项列表")
    public Result<List<ScoreItemDTO>> scoreItems(@PathVariable Long id) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.getScoreItems(userId, id));
    }

    @PostMapping("/{id}/score-items")
    @Operation(summary = "创建加减分项")
    public Result<ScoreItemDTO> createScoreItem(@PathVariable Long id, @RequestBody ScoreItemDTO item) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.createScoreItem(userId, id, item));
    }

    @PutMapping("/{id}/score-items/{itemId}")
    @Operation(summary = "修改加减分项")
    public Result<ScoreItemDTO> updateScoreItem(@PathVariable Long id,
                                                @PathVariable Long itemId,
                                                @RequestBody ScoreItemDTO item) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(relationshipFeignClient.updateScoreItem(userId, id, itemId, item));
    }

    @DeleteMapping("/{id}/score-items/{itemId}")
    @Operation(summary = "删除加减分项")
    public Result<Void> deleteScoreItem(@PathVariable Long id, @PathVariable Long itemId) {
        Long userId = Long.valueOf(UserContext.getUserId());
        relationshipFeignClient.deleteScoreItem(userId, id, itemId);
        return Result.success();
    }

    @GetMapping("/{id}/score-records")
    @Operation(summary = "打分记录")
    public Result<Page<ScoreRecordDTO>> scoreRecords(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return Result.success(relationshipFeignClient.getScoreRecords(id, page, size));
    }
}

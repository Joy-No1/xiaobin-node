package com.xml.xiaobinnode.api.relationship;

import com.xml.xiaobinnode.api.relationship.dto.RelationshipDTO;
import com.xml.xiaobinnode.api.relationship.dto.ScoreDTO;
import com.xml.xiaobinnode.api.relationship.dto.ScoreItemDTO;
import com.xml.xiaobinnode.api.relationship.dto.ScoreRecordDTO;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关系服务 Feign 接口
 */
@FeignClient(name = "xiaobin-relationship", path = "/api/v1/relationships")
public interface RelationshipFeignClient {

    // ==================== 关系 ====================

    /** 发起关系请求 */
    @PostMapping
    Result<RelationshipDTO> createRelationship(@RequestHeader("X-User-Id") Long userId,
                                                @RequestParam("targetUserId") Long targetUserId);

    /** 接收到的关系请求 */
    @GetMapping("/received")
    Result<List<RelationshipDTO>> getReceived(@RequestHeader("X-User-Id") Long userId);

    /** 发送出去的关系请求 */
    @GetMapping("/sent")
    Result<List<RelationshipDTO>> getSent(@RequestHeader("X-User-Id") Long userId);

    /** 确认关系 */
    @PutMapping("/{id}/confirm")
    Result<RelationshipDTO> confirmRelationship(@RequestHeader("X-User-Id") Long userId,
                                                  @PathVariable("id") Long id);

    /** 解除关系 */
    @DeleteMapping("/{id}")
    Result<Void> dissolveRelationship(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable("id") Long id);

    /** 获取我的关系 */
    @GetMapping("/me")
    Result<RelationshipDTO> getMyRelationship(@RequestHeader("X-User-Id") Long userId);

    /** 获取用户的伴侣信息 */
    @GetMapping("/user/{userId}/partner")
    Result<UserVO> getPartnerInfo(@PathVariable("userId") Long userId);

    // ==================== 好感度 ====================

    /** 查看好感度 */
    @GetMapping("/{id}/scores")
    Result<ScoreDTO> getScores(@PathVariable("id") Long relationshipId);

    /** 打分 */
    @PostMapping("/{id}/scores")
    Result<ScoreRecordDTO> score(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable("id") Long relationshipId,
                                   @RequestParam("scoreItemId") Long scoreItemId,
                                   @RequestParam(value = "reason", required = false) String reason);

    // ==================== 加减分项 ====================

    /** 获取加减分项列表 */
    @GetMapping("/{id}/score-items")
    Result<List<ScoreItemDTO>> getScoreItems(@RequestHeader("X-User-Id") Long userId,
                                               @PathVariable("id") Long relationshipId);

    /** 创建加减分项 */
    @PostMapping("/{id}/score-items")
    Result<ScoreItemDTO> createScoreItem(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable("id") Long relationshipId,
                                           @RequestBody ScoreItemDTO item);

    /** 修改加减分项 */
    @PutMapping("/{id}/score-items/{itemId}")
    Result<ScoreItemDTO> updateScoreItem(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable("id") Long relationshipId,
                                           @PathVariable("itemId") Long itemId,
                                           @RequestBody ScoreItemDTO item);

    /** 删除加减分项 */
    @DeleteMapping("/{id}/score-items/{itemId}")
    Result<Void> deleteScoreItem(@RequestHeader("X-User-Id") Long userId,
                                  @PathVariable("id") Long relationshipId,
                                  @PathVariable("itemId") Long itemId);

    // ==================== 打分记录 ====================

    /** 打分历史 */
    @GetMapping("/{id}/score-records")
    Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<ScoreRecordDTO>> getScoreRecords(
            @PathVariable("id") Long relationshipId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);
}

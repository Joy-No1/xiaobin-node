package com.xml.xiaobinnode.relationship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.relationship.entity.*;

import java.util.List;

/**
 * 关系服务接口
 */
public interface RelationshipService {

    /**
     * 发起关系请求
     * @param userId 发起方用户ID
     * @param targetUserId 接收方用户ID
     * @param relationType 关系类型编码（关联字典 sys_dict_item，type_code=RELATION_TYPE）
     */
    Relationship createRelationship(Long userId, Long targetUserId, String relationType);

    /**
     * 确认情侣关系
     */
    Relationship confirmRelationship(Long relationshipId, Long userId);

    /**
     * 解除关系
     */
    void dissolveRelationship(Long relationshipId, Long userId);

    /**
     * 获取我的当前关系
     */
    List<Relationship> getMyRelationship(Long userId);

    /**
     * 查看双方好感度
     */
    Score getScore(Long relationshipId);

    /**
     * 打分
     */
    ScoreRecord score(Long relationshipId, Long scorerId, Long scoreItemId, String reason);

    /**
     * 创建自定义加减分项
     */
    ScoreItem createScoreItem(Long userId, Long relationshipId, ScoreItem item);

    /**
     * 修改加减分项
     */
    ScoreItem updateScoreItem(Long itemId, Long userId, ScoreItem item);

    /**
     * 删除加减分项
     */
    void deleteScoreItem(Long itemId, Long userId);

    /**
     * 获取自定义加减分项列表
     */
    java.util.List<ScoreItem> getScoreItems(Long relationshipId, Long userId);

    /**
     * 获取打分记录
     */
    Page<ScoreRecord> getScoreRecords(Long relationshipId, int page, int size);

    /**
     * 获取接收到的请求
     * @param userId 用户id
     */
    List<Relationship> getReceived(Long userId);

    /**
     * 获取发送的请求
     * @param userId
     * @return
     */
    List<Relationship> getSent(Long userId);
}

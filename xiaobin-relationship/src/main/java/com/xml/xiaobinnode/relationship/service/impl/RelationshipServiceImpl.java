package com.xml.xiaobinnode.relationship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.relationship.constans.RelationEnum;
import com.xml.xiaobinnode.relationship.entity.*;
import com.xml.xiaobinnode.relationship.mapper.*;
import com.xml.xiaobinnode.relationship.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipMapper relationshipMapper;
    private final ScoreMapper scoreMapper;
    private final ScoreItemMapper scoreItemMapper;
    private final ScoreRecordMapper scoreRecordMapper;

    @Override
    @Transactional
    public Relationship createRelationship(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new BusinessException("不能和自己建立关系");
        }

        // 检查对方是否已有关系
        LambdaQueryWrapper<Relationship> targetWrapper = new LambdaQueryWrapper<>();
        targetWrapper.and(w -> w
                .eq(Relationship::getInitiatorId, targetUserId)
                .or()
                .eq(Relationship::getReceiverId, targetUserId))
                .eq(Relationship::getStatus, "CONFIRMED");
        if (relationshipMapper.selectCount(targetWrapper) > 0) {
            throw new BusinessException("对方已有情侣关系");
        }

        // 检查发起方是否已有关系
        LambdaQueryWrapper<Relationship> myWrapper = new LambdaQueryWrapper<>();
        myWrapper.and(w -> w
                .eq(Relationship::getInitiatorId, userId)
                .or()
                .eq(Relationship::getReceiverId, userId))
                .eq(Relationship::getStatus, "CONFIRMED");
        if (relationshipMapper.selectCount(myWrapper) > 0) {
            throw new BusinessException("你已有情侣关系");
        }

        // 检查是否已存在有效关系
        LambdaQueryWrapper<Relationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(Relationship::getInitiatorId, userId).eq(Relationship::getReceiverId, targetUserId))
                .or(w1 -> w1.eq(Relationship::getInitiatorId, targetUserId).eq(Relationship::getReceiverId, userId))
        ).ne(Relationship::getStatus, "DISSOLVED");
        if (relationshipMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已存在有效关系或待确认的关系请求");
        }

        Relationship relationship = new Relationship();
        relationship.setInitiatorId(userId);
        relationship.setReceiverId(targetUserId);
        relationship.setStatus("PENDING");
        relationshipMapper.insert(relationship);

        log.info("关系请求已发起: relationshipId={}, user1={}, user2={}", relationship.getId(), userId, targetUserId);
        return relationship;
    }

    @Override
    @Transactional
    public Relationship confirmRelationship(Long relationshipId, Long userId) {
        Relationship relationship = relationshipMapper.selectById(relationshipId);
        if (relationship == null) {
            throw new BusinessException("关系不存在");
        }

        if (!"PENDING".equals(relationship.getStatus())) {
            throw new BusinessException("该关系不是待确认状态");
        }

        // 确认方必须是关系中的用户
        if (!relationship.getReceiverId().equals(userId)) {
            throw new BusinessException("无权确认此关系");
        }

        relationship.setStatus("CONFIRMED");
        relationship.setConfirmedAt(LocalDateTime.now());
        relationshipMapper.updateById(relationship);

        // 初始化双方分数（各100分）
        Score score1 = new Score();
        score1.setRelationshipId(relationshipId);
        score1.setScorerId(relationship.getInitiatorId());
        score1.setTargetId(relationship.getReceiverId());
        score1.setCurrentScore(CommonConstants.INITIAL_SCORE);
        scoreMapper.insert(score1);

        Score score2 = new Score();
        score2.setRelationshipId(relationshipId);
        score2.setScorerId(relationship.getReceiverId());
        score2.setTargetId(relationship.getInitiatorId());
        score2.setCurrentScore(CommonConstants.INITIAL_SCORE);
        scoreMapper.insert(score2);

        log.info("关系已确认: relationshipId={}", relationshipId);
        return relationship;
    }

    @Override
    @Transactional
    public void dissolveRelationship(Long relationshipId, Long userId) {
        Relationship relationship = relationshipMapper.selectById(relationshipId);
        if (relationship == null) {
            throw new BusinessException("关系不存在");
        }

        if (!"CONFIRMED".equals(relationship.getStatus())) {
            throw new BusinessException("只能解除已确认的关系");
        }

        if (!relationship.getInitiatorId().equals(userId) && !relationship.getReceiverId().equals(userId)) {
            throw new BusinessException("无权解除此关系");
        }

        relationship.setStatus("DISSOLVED");
        relationship.setDissolvedAt(LocalDateTime.now());
        relationshipMapper.updateById(relationship);

        log.info("关系已解除: relationshipId={}", relationshipId);
    }

    @Override
    public Relationship getMyRelationship(Long userId) {
        LambdaQueryWrapper<Relationship> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Relationship::getInitiatorId, userId).or().eq(Relationship::getReceiverId, userId))
                .eq(Relationship::getStatus, "CONFIRMED");
        return relationshipMapper.selectOne(wrapper);
    }

    @Override
    public Score getScore(Long relationshipId) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getRelationshipId, relationshipId);
        List<Score> scores = scoreMapper.selectList(wrapper);
        if (scores.isEmpty()) {
            throw new BusinessException("分数记录不存在");
        }
        return scores.get(0);
    }

    @Override
    @Transactional
    public ScoreRecord score(Long relationshipId, Long scorerId, Long scoreItemId, String reason) {
        Relationship relationship = relationshipMapper.selectById(relationshipId);
        if (relationship == null || !"CONFIRMED".equals(relationship.getStatus())) {
            throw new BusinessException("关系不存在或未确认");
        }

        // 确定被打分人
        Long targetId;
        if (relationship.getInitiatorId().equals(scorerId)) {
            targetId = relationship.getReceiverId();
        } else if (relationship.getReceiverId().equals(scorerId)) {
            targetId = relationship.getInitiatorId();
        } else {
            throw new BusinessException("不是关系中的用户");
        }

        // 不能给自己打分
        if (scorerId.equals(targetId)) {
            throw new BusinessException("不能给自己打分");
        }

        // 获取当前分数
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getRelationshipId, relationshipId)
                .eq(Score::getScorerId, scorerId)
                .eq(Score::getTargetId, targetId);
        Score score = scoreMapper.selectOne(scoreWrapper);
        if (score == null) {
            throw new BusinessException("分数记录不存在");
        }

        // 计算分数变化
        int scoreChange = 0;
        String scoreReason = reason;

        if (scoreItemId != null) {
            ScoreItem scoreItem = scoreItemMapper.selectById(scoreItemId);
            if (scoreItem == null) {
                throw new BusinessException("打分项目不存在");
            }
            if (!scoreItem.getUserId().equals(scorerId)) {
                throw new BusinessException("只能使用自己的打分项目");
            }
            scoreChange = scoreItem.getScoreValue();
            if ("SUBTRACT".equals(scoreItem.getType())) {
                // 扣分取负值
                scoreChange = -Math.abs(scoreChange);
            }
            if (scoreReason == null || scoreReason.isBlank()) {
                scoreReason = scoreItem.getItemName();
            }
        } else {
            if (scoreReason == null || scoreReason.isBlank()) {
                throw new BusinessException("请填写打分原因");
            }
            // 根据reason的内容判断是加分还是扣分（简单处理）
            throw new BusinessException("请选择一个打分项目");
        }

        // 计算新分数（不能低于0）
        int scoreBefore = score.getCurrentScore();
        int scoreAfter = scoreBefore + scoreChange;
        if (scoreAfter < CommonConstants.MIN_SCORE) {
            scoreAfter = CommonConstants.MIN_SCORE;
            scoreChange = scoreAfter - scoreBefore;
        }

        // 更新分数
        score.setCurrentScore(scoreAfter);
        scoreMapper.updateById(score);

        // 记录
        ScoreRecord record = new ScoreRecord();
        record.setRelationshipId(relationshipId);
        record.setScorerId(scorerId);
        record.setTargetId(targetId);
        record.setScoreItemId(scoreItemId);
        record.setScoreChange(scoreChange);
        record.setReason(scoreReason);
        record.setScoreBefore(scoreBefore);
        record.setScoreAfter(scoreAfter);
        scoreRecordMapper.insert(record);

        log.info("打分完成: scorer={}, target={}, change={}, before={}, after={}",
                scorerId, targetId, scoreChange, scoreBefore, scoreAfter);
        return record;
    }

    @Override
    public ScoreItem createScoreItem(Long userId, Long relationshipId, ScoreItem item) {
        item.setUserId(userId);
        item.setRelationshipId(relationshipId);
        scoreItemMapper.insert(item);
        return item;
    }

    @Override
    public ScoreItem updateScoreItem(Long itemId, Long userId, ScoreItem item) {
        ScoreItem existing = scoreItemMapper.selectById(itemId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此打分项目");
        }
        item.setId(itemId);
        item.setUserId(userId);
        item.setRelationshipId(existing.getRelationshipId());
        scoreItemMapper.updateById(item);
        return item;
    }

    @Override
    public void deleteScoreItem(Long itemId, Long userId) {
        ScoreItem existing = scoreItemMapper.selectById(itemId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此打分项目");
        }
        scoreItemMapper.deleteById(itemId);
    }

    @Override
    public List<ScoreItem> getScoreItems(Long relationshipId, Long userId) {
        LambdaQueryWrapper<ScoreItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScoreItem::getRelationshipId, relationshipId)
                .eq(ScoreItem::getUserId, userId);
        return scoreItemMapper.selectList(wrapper);
    }

    @Override
    public Page<ScoreRecord> getScoreRecords(Long relationshipId, int page, int size) {
        LambdaQueryWrapper<ScoreRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScoreRecord::getRelationshipId, relationshipId)
                .orderByDesc(ScoreRecord::getCreatedAt);
        return scoreRecordMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Relationship> getReceived(Long userId) {
        return new LambdaQueryChainWrapper<>(relationshipMapper)
                .eq(Relationship::getReceiverId,userId)
                .eq(Relationship::getStatus, RelationEnum.PENDING.getCode())
                .list();
    }

    @Override
    public List<Relationship> getSent(Long userId) {
        return new LambdaQueryChainWrapper<>(relationshipMapper)
                .eq(Relationship::getInitiatorId,userId)
                .eq(Relationship::getStatus, RelationEnum.PENDING.getCode())
                .list();
    }
}

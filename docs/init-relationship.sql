-- ========================================
-- 关系服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS xiaobin_relationship DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiaobin_relationship;

CREATE TABLE IF NOT EXISTS `relationship` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `user1_id` BIGINT NOT NULL COMMENT '用户1 ID',
    `user2_id` BIGINT NOT NULL COMMENT '用户2 ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/DISSOLVED',
    `initiated_by` BIGINT NOT NULL COMMENT '发起方用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `confirmed_at` DATETIME DEFAULT NULL COMMENT '确认时间',
    `dissolved_at` DATETIME DEFAULT NULL COMMENT '解除时间',
    PRIMARY KEY (`id`),
    KEY `idx_user1` (`user1_id`),
    KEY `idx_user2` (`user2_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情侣关系表';

CREATE TABLE IF NOT EXISTS `score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分数ID',
    `relationship_id` BIGINT NOT NULL COMMENT '关系ID',
    `scorer_id` BIGINT NOT NULL COMMENT '打分人ID',
    `target_id` BIGINT NOT NULL COMMENT '被打分人ID',
    `current_score` INT NOT NULL DEFAULT 100 COMMENT '当前分数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_relationship` (`relationship_id`),
    KEY `idx_scorer_target` (`scorer_id`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好感度分数表';

CREATE TABLE IF NOT EXISTS `score_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `relationship_id` BIGINT NOT NULL COMMENT '关系ID',
    `item_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `score_value` INT NOT NULL COMMENT '分数值',
    `type` VARCHAR(10) NOT NULL COMMENT '类型: ADD/SUBTRACT',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_relationship` (`user_id`, `relationship_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义加减分项目表';

CREATE TABLE IF NOT EXISTS `score_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `relationship_id` BIGINT NOT NULL COMMENT '关系ID',
    `scorer_id` BIGINT NOT NULL COMMENT '打分人ID',
    `target_id` BIGINT NOT NULL COMMENT '被打分人ID',
    `score_item_id` BIGINT DEFAULT NULL COMMENT '打分项目ID',
    `score_change` INT NOT NULL COMMENT '分数变化值',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '打分原因',
    `score_before` INT NOT NULL COMMENT '打分前分数',
    `score_after` INT NOT NULL COMMENT '打分后分数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_relationship` (`relationship_id`),
    KEY `idx_scorer` (`scorer_id`),
    KEY `idx_target` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打分记录表';

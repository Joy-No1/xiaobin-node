-- ========================================
-- 好感度记账APP - 数据库初始化脚本
-- ========================================

CREATE DATABASE IF NOT EXISTS xiaobin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiaobin;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(500) DEFAULT '' COMMENT '头像URL',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别: MALE/FEMALE/OTHER',
    `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 情侣关系表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `relationship` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `user1_id` BIGINT NOT NULL COMMENT '用户1 ID',
    `user2_id` BIGINT NOT NULL COMMENT '用户2 ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/DISSOLVED',
    `initiated_by` BIGINT NOT NULL COMMENT '发起方用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `confirmed_at` DATETIME DEFAULT NULL COMMENT '确认时间',
    `dissolved_at` DATETIME DEFAULT NULL COMMENT '解除时间',
    PRIMARY KEY (`id`),
    KEY `idx_user1` (`user1_id`),
    KEY `idx_user2` (`user2_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情侣关系表';

-- ----------------------------
-- 好感度分数表
-- ----------------------------
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

-- ----------------------------
-- 自定义加减分项目表
-- ----------------------------
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

-- ----------------------------
-- 打分记录表
-- ----------------------------
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

-- ----------------------------
-- 关注表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关注ID',
    `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
    `followee_id` BIGINT NOT NULL COMMENT '被关注者ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'FOLLOWING' COMMENT 'FOLLOWING/MUTUAL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_followee` (`follower_id`, `followee_id`),
    KEY `idx_followee` (`followee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

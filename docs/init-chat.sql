-- ========================================
-- 聊天服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS xiaobin_chat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiaobin_chat;

-- 会话表：仅存会话本体与最后一条消息摘要，参与方与未读/置顶等状态在 conversation_member
CREATE TABLE IF NOT EXISTS `conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `type` VARCHAR(20) NOT NULL DEFAULT 'PRIVATE' COMMENT '会话类型：PRIVATE-私聊',
    `private_key` VARCHAR(100) DEFAULT NULL COMMENT '私聊幂等键：minUserId_maxUserId（PRIVATE 时使用，保证同一对用户唯一会话）',
    `last_message_id` BIGINT DEFAULT NULL COMMENT '最后一条消息ID',
    `last_message` VARCHAR(500) DEFAULT NULL COMMENT '最后一条消息内容，用于聊天列表展示',
    `last_message_time` DATETIME DEFAULT NULL COMMENT '最后一条消息时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_private_key` (`private_key`),
    KEY `idx_last_message_time` (`last_message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 会话参与者表：每个用户对每个会话独立的已读进度、未读数、置顶、免打扰
CREATE TABLE IF NOT EXISTS `conversation_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `last_read_message_id` BIGINT DEFAULT NULL COMMENT '最后已读消息ID',
    `unread_count` INT NOT NULL DEFAULT 0 COMMENT '未读消息数量',
    `is_pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `is_muted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否免打扰：0-否，1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入会话时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_user` (`conversation_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_conversation` (`user_id`, `conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话参与者表';

-- 消息表：不存 receiver_id（接收方由会话成员推断），不存 is_read（已读进度在 conversation_member.last_read_message_id）
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者用户ID',
    `message_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT-文字，IMAGE-图片，VOICE-语音，EMOJI-表情',
    `content` TEXT NOT NULL COMMENT '消息内容（文本/图片URL/语音URL/表情）',
    `duration` INT DEFAULT NULL COMMENT '语音消息时长（秒），其他类型为NULL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_conversation_time` (`conversation_id`, `created_at`),
    KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

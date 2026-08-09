-- ========================================
-- 聊天服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS xiaobin_chat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiaobin_chat;

CREATE TABLE IF NOT EXISTS `conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `user1_id` BIGINT NOT NULL COMMENT '用户1 ID（较小者）',
    `user2_id` BIGINT NOT NULL COMMENT '用户2 ID（较大者）',
    `last_message` VARCHAR(500) DEFAULT '' COMMENT '最后一条消息',
    `last_message_time` DATETIME DEFAULT NULL COMMENT '最后消息时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_pair` (`user1_id`, `user2_id`),
    KEY `idx_user1` (`user1_id`),
    KEY `idx_user2` (`user2_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

CREATE TABLE IF NOT EXISTS `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
    `content` TEXT NOT NULL COMMENT '消息内容（文本/图片URL/语音URL/表情）',
    `message_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT 'TEXT/IMAGE/VOICE/EMOJI',
    `duration` INT DEFAULT NULL COMMENT '语音消息时长（秒），其他类型为NULL',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation` (`conversation_id`),
    KEY `idx_sender` (`sender_id`),
    KEY `idx_receiver` (`receiver_id`),
    KEY `idx_conversation_time` (`conversation_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- ========================================
-- 字典表 迁移脚本
-- 目标库: xiaobin_user
-- 说明: 通用字典系统，支持按 typeCode 分类查询字典项
-- ========================================
USE xiaobin_user;

-- ----------------------------
-- 字典类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_code`   VARCHAR(64)  NOT NULL COMMENT '字典类型编码（唯一标识）',
    `type_name`   VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序（升序）',
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ----------------------------
-- 字典项表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_item` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_code`   VARCHAR(64)  NOT NULL COMMENT '字典类型编码（关联sys_dict_type.type_code）',
    `item_code`   VARCHAR(64)  NOT NULL COMMENT '字典项编码',
    `item_name`   VARCHAR(100) NOT NULL COMMENT '字典项名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序（升序）',
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_item_code` (`type_code`, `item_code`),
    KEY `idx_type_code` (`type_code`),
    KEY `idx_item_code` (`item_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

-- ----------------------------
-- 种子数据：社交关系类型
-- ----------------------------
INSERT INTO `sys_dict_type` (`type_code`, `type_name`, `description`, `sort_order`, `status`) VALUES
('RELATION_TYPE', '社交关系类型', '好友/情侣等社交关系分类', 1, 'ACTIVE'),
('MESSAGE_TYPE',  '聊天消息类型', '聊天消息内容类型分类',   2, 'ACTIVE');

INSERT INTO `sys_dict_item` (`type_code`, `item_code`, `item_name`, `description`, `sort_order`, `status`) VALUES
('RELATION_TYPE', 'COUPLE',    '情侣', '恋爱关系', 1, 'ACTIVE'),
('RELATION_TYPE', 'BESTIE',    '闺蜜', '女性密友', 2, 'ACTIVE'),
('RELATION_TYPE', 'BUDDY',     '死党', '铁杆兄弟', 3, 'ACTIVE'),
('RELATION_TYPE', 'BRO',       '基友', '男性密友', 4, 'ACTIVE'),
('RELATION_TYPE', 'SOULMATE',  '知己', '灵魂伴侣', 5, 'ACTIVE'),
('RELATION_TYPE', 'FAMILY',    '家人', '亲情关系', 6, 'ACTIVE'),
('RELATION_TYPE', 'COLLEAGUE', '同事', '职场关系', 7, 'ACTIVE'),
('MESSAGE_TYPE',  'TEXT',      '文字', '文字消息',       1, 'ACTIVE'),
('MESSAGE_TYPE',  'IMAGE',     '图片', '图片消息（缩略图+点击放大）', 2, 'ACTIVE'),
('MESSAGE_TYPE',  'VOICE',     '语音', '语音消息（播放按钮+时长）',   3, 'ACTIVE'),
('MESSAGE_TYPE',  'EMOJI',     '表情', '表情消息（大字号显示）',     4, 'ACTIVE');

-- ----------------------------
-- 关系表增加类型字段（对已有数据库的迁移）
-- 新建库可直接使用 init-relationship.sql（已包含该列）
-- ----------------------------
USE xiaobin_relationship;
ALTER TABLE `relationship`
    ADD COLUMN IF NOT EXISTS `relation_type` VARCHAR(50) DEFAULT NULL COMMENT '关系类型编码（关联sys_dict_item.item_code, type_code=RELATION_TYPE）' AFTER `status`;

-- ----------------------------
-- 聊天消息表增加语音时长字段（对已有数据库的迁移）
-- 新建库可直接使用 init-chat.sql（已包含该列）
-- ----------------------------
USE xiaobin_chat;
ALTER TABLE `chat_message`
    ADD COLUMN `duration` INT DEFAULT NULL COMMENT '语音消息时长（秒），其他类型为NULL' AFTER `message_type`;

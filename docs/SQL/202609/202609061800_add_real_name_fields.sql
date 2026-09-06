-- ========================================
-- 添加实名认证相关字段
-- 创建时间: 2026-09-06
-- ========================================
USE xiaobin_user;

ALTER TABLE `user`
ADD COLUMN `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号' AFTER `email`,
ADD COLUMN `real_name_verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否实名认证：0否 1是' AFTER `status`;

-- 添加身份证号唯一索引
ALTER TABLE `user`
ADD UNIQUE KEY `uk_id_card` (`id_card`);

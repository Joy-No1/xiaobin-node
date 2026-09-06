-- ========================================
-- 添加用户主页背景图字段
-- ========================================
USE xiaobin_user;

ALTER TABLE `user`
ADD COLUMN `profile_background_url` VARCHAR(500) DEFAULT '' COMMENT '主页背景图URL'
AFTER `avatar_url`;

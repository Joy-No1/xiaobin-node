-- ========================================
-- 用户服务数据库 xiaobin_user
-- ========================================
CREATE DATABASE IF NOT EXISTS xiaobin_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiaobin_user;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  DEFAULT NULL COMMENT '用户名（真实姓名，实名认证后填充）',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `id_card`     VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `nickname`    VARCHAR(50)  NOT NULL COMMENT '昵称',
    `avatar_url`  VARCHAR(500) DEFAULT '' COMMENT '头像URL',
    `profile_background_url` VARCHAR(500) DEFAULT '' COMMENT '主页背景图URL',
    `gender`      VARCHAR(10)  DEFAULT NULL COMMENT '性别: MALE/FEMALE/OTHER',
    `birthday`    TIMESTAMP    NULL DEFAULT NULL COMMENT '生日',
    `company`     VARCHAR(255) DEFAULT NULL COMMENT '公司',
    `school`      VARCHAR(255) DEFAULT NULL COMMENT '学校',
    `height`      DOUBLE       DEFAULT NULL COMMENT '身高',
    `weight`      DOUBLE       DEFAULT NULL COMMENT '体重',
    `education`   VARCHAR(32)  DEFAULT NULL COMMENT '学历',
    `location_id` VARCHAR(255) DEFAULT NULL COMMENT '地址id',
    `bio`         VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    `real_name_verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否实名认证：0否 1是',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 地址信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `location` (
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `province`   VARCHAR(50)     NOT NULL COMMENT '省级行政区名称',
    `city`       VARCHAR(50)     DEFAULT NULL COMMENT '市级行政区名称',
    `district`   VARCHAR(50)     DEFAULT NULL COMMENT '区县级行政区名称',
    `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_province` (`province`),
    KEY `idx_city` (`city`),
    KEY `idx_district` (`district`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户个人所在地信息表';

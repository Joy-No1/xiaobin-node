-- ===================================================
-- XXL-Job Admin 调度中心数据库初始化
-- 数据库名称: xiaobin-job
-- 适用于 XXL-Job 2.4.x
-- ===================================================

CREATE DATABASE IF NOT EXISTS `xiaobin-job`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `xiaobin-job`;

-- ---------------------------------------------------
-- 1. 执行器组（AppName 与执行器地址的映射）
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_group`
(
    `id`           INT(11)     NOT NULL AUTO_INCREMENT,
    `app_name`     VARCHAR(64) NOT NULL COMMENT '执行器AppName',
    `title`        VARCHAR(12) NOT NULL COMMENT '执行器名称',
    `address_type` TINYINT(4)  NOT NULL DEFAULT 0 COMMENT '注册方式: 0=自动注册, 1=手动录入',
    `address_list` TEXT                  DEFAULT NULL COMMENT '手动录入的执行器地址列表, 逗号分隔',
    `update_time`  DATETIME         DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 2. 任务信息
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_info`
(
    `id`                        INT(11)      NOT NULL AUTO_INCREMENT,
    `job_group`                 INT(11)      NOT NULL COMMENT '执行器主键ID',
    `job_desc`                  VARCHAR(255) NOT NULL COMMENT '任务描述',
    `add_time`                  DATETIME              DEFAULT NULL,
    `update_time`               DATETIME              DEFAULT NULL,
    `author`                    VARCHAR(64)           DEFAULT NULL COMMENT '负责人',
    `alarm_email`               VARCHAR(255)          DEFAULT NULL COMMENT '报警邮件',
    `schedule_type`             VARCHAR(50)  NOT NULL DEFAULT 'NONE' COMMENT '调度类型: NONE, CRON, FIX_RATE, FIX_DELAY',
    `schedule_conf`             VARCHAR(128)          DEFAULT NULL COMMENT '调度配置: CRON表达式等',
    `misfire_strategy`          VARCHAR(50)  NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略: DO_NOTHING, FIRE_ONCE_NOW',
    `executor_route_strategy`   VARCHAR(50)           DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler`          VARCHAR(255)          DEFAULT NULL COMMENT '执行器任务Handler名称 (@XxlJob注解值)',
    `executor_param`            VARCHAR(512)          DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy`   VARCHAR(50)           DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout`          INT(11)      NOT NULL DEFAULT 0 COMMENT '任务执行超时时间(秒)',
    `executor_fail_retry_count` INT(11)      NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `glue_type`                 VARCHAR(50)  NOT NULL COMMENT 'GLUE类型: BEAN, GLUE_GROOVY, GLUE_SHELL...',
    `glue_source`               MEDIUMTEXT COMMENT 'GLUE源代码',
    `glue_remark`               VARCHAR(128)          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           DATETIME              DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               VARCHAR(255)          DEFAULT NULL COMMENT '子任务ID, 逗号分隔',
    `trigger_status`            TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '调度状态: 0=停止, 1=运行',
    `trigger_last_time`         BIGINT(13)   NOT NULL DEFAULT 0 COMMENT '上次调度时间',
    `trigger_next_time`         BIGINT(13)   NOT NULL DEFAULT 0 COMMENT '下次调度时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 3. 任务调度日志
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_log`
(
    `id`                        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `job_group`                 INT(11)    NOT NULL COMMENT '执行器主键ID',
    `job_id`                    INT(11)    NOT NULL COMMENT '任务主键ID',
    `executor_address`          VARCHAR(255)        DEFAULT NULL COMMENT '执行器地址',
    `executor_handler`          VARCHAR(255)        DEFAULT NULL COMMENT '执行器任务Handler名称',
    `executor_param`            VARCHAR(512)        DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   VARCHAR(20)         DEFAULT NULL COMMENT '分片参数',
    `executor_fail_retry_count` INT(11)    NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `trigger_time`              DATETIME            DEFAULT NULL COMMENT '调度时间',
    `trigger_code`              INT(11)    NOT NULL DEFAULT 0 COMMENT '调度结果: 200=成功, 500=失败',
    `trigger_msg`               TEXT COMMENT '调度日志',
    `handle_time`               DATETIME            DEFAULT NULL COMMENT '执行时间',
    `handle_code`               INT(11)    NOT NULL DEFAULT 0 COMMENT '执行结果: 200=成功, 500=失败',
    `handle_msg`                TEXT COMMENT '执行日志',
    `alarm_status`              TINYINT(4) NOT NULL DEFAULT 0 COMMENT '告警状态: 0=默认, 1=无需告警, 2=告警成功, 3=告警失败',
    PRIMARY KEY (`id`),
    KEY `I_trigger_time` (`trigger_time`),
    KEY `I_handle_code` (`handle_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 4. 任务调度日志报表 (按天汇总)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_log_report`
(
    `id`            INT(11) NOT NULL AUTO_INCREMENT,
    `trigger_day`   DATE             DEFAULT NULL COMMENT '调度日期',
    `running_count` INT(11) NOT NULL DEFAULT 0 COMMENT '运行中的日志数量',
    `suc_count`     INT(11) NOT NULL DEFAULT 0 COMMENT '执行成功数量',
    `fail_count`    INT(11) NOT NULL DEFAULT 0 COMMENT '执行失败数量',
    `update_time`   DATETIME         DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_trigger_day` (`trigger_day`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 5. GLUE脚本日志
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_logglue`
(
    `id`          INT(11)      NOT NULL AUTO_INCREMENT,
    `job_id`      INT(11)      NOT NULL COMMENT '任务主键ID',
    `glue_type`   VARCHAR(50)           DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` MEDIUMTEXT COMMENT 'GLUE源代码',
    `glue_remark` VARCHAR(128) NOT NULL COMMENT 'GLUE备注',
    `add_time`    DATETIME              DEFAULT NULL,
    `update_time` DATETIME              DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 6. 执行器注册信息
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_registry`
(
    `id`             INT(11)      NOT NULL AUTO_INCREMENT,
    `registry_group` VARCHAR(50)  NOT NULL COMMENT '注册组: EXECUTOR 或 ADMIN',
    `registry_key`   VARCHAR(255) NOT NULL COMMENT '注册键: AppName 或 Admin地址',
    `registry_value` VARCHAR(255) NOT NULL COMMENT '注册值: 执行器地址',
    `update_time`    DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `i_g_k_v` (`registry_group`, `registry_key`, `registry_value`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 7. 用户表 (调度中心登录)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_user`
(
    `id`         INT(11)     NOT NULL AUTO_INCREMENT,
    `username`   VARCHAR(50) NOT NULL COMMENT '用户名',
    `password`   VARCHAR(50) NOT NULL COMMENT '密码',
    `role`       TINYINT(4)  NOT NULL DEFAULT 0 COMMENT '角色: 0=普通用户, 1=管理员',
    `permission` VARCHAR(255)         DEFAULT NULL COMMENT '权限: 执行器ID列表, 逗号分隔',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 8. 分布式锁 (避免多个Admin同时调度)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `xxl_job_lock`
(
    `lock_name` VARCHAR(50) NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------
-- 初始化数据: 默认管理员 admin / 123456
-- ---------------------------------------------------
INSERT IGNORE INTO `xxl_job_user` (`username`, `password`, `role`, `permission`)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);

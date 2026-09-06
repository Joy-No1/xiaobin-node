-- ========================================
-- 用户设备信息表
-- 创建时间: 2026-09-06 17:37
-- ========================================
USE xiaobin_user;

CREATE TABLE IF NOT EXISTS user_device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',

    device_id VARCHAR(128) NOT NULL COMMENT '设备唯一标识',
    device_type VARCHAR(20) NOT NULL COMMENT '设备类型：IOS/ANDROID/MAC/WINDOWS/LINUX/WEB',
    device_name VARCHAR(100) DEFAULT NULL COMMENT '设备名称，如 iPhone 17 Pro、MacBook Pro',

    os_name VARCHAR(50) DEFAULT NULL COMMENT '操作系统名称',
    os_version VARCHAR(50) DEFAULT NULL COMMENT '操作系统版本',

    app_version VARCHAR(50) DEFAULT NULL COMMENT '客户端/App版本',
    browser VARCHAR(100) DEFAULT NULL COMMENT '浏览器名称及版本',

    last_ip VARCHAR(64) DEFAULT NULL COMMENT '最近一次访问IP',
    last_active_at DATETIME DEFAULT NULL COMMENT '最近活跃时间',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '设备状态：0禁用 1正常',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次使用时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',

    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',

    PRIMARY KEY (id),
    UNIQUE KEY uk_user_device (user_id, device_id),
    KEY idx_user_id (user_id),
    KEY idx_device_id (device_id),
    KEY idx_last_active_at (last_active_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='用户设备表';

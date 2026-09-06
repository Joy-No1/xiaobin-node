package com.xml.xiaobinnode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户设备信息实体
 */
@Data
@TableName("user_device")
public class UserDevice implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 设备唯一标识 */
    private String deviceId;

    /** 设备类型：IOS/ANDROID/MAC/WINDOWS/LINUX/WEB */
    private String deviceType;

    /** 设备名称，如 iPhone 17 Pro、MacBook Pro */
    private String deviceName;

    /** 操作系统名称 */
    private String osName;

    /** 操作系统版本 */
    private String osVersion;

    /** 客户端/App版本 */
    private String appVersion;

    /** 浏览器名称及版本 */
    private String browser;

    /** 最近一次访问IP */
    private String lastIp;

    /** 最近活跃时间 */
    private Date lastActiveAt;

    /** 设备状态：0禁用 1正常 */
    private Integer status;

    /** 首次使用时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /** 逻辑删除：0否 1是 */
    @TableLogic
    private Integer deleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

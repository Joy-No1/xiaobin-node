package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 用户设备VO
 */
@Data
public class UserDeviceVO {

    private Long id;

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
    private Date createdAt;

    /** 是否当前设备 */
    private Boolean isCurrent;
}

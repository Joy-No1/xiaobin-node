package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 设备信息DTO（用于登录时传递）
 */
@Data
public class DeviceInfoDTO {

    /** 设备唯一标识（如UUID、设备指纹） */
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
}

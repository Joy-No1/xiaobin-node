package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.common.dto.DeviceInfoDTO;
import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.entity.UserDevice;

import java.util.List;

/**
 * 用户设备服务
 */
public interface UserDeviceService {

    /**
     * 记录或更新设备信息（登录时调用）
     * @param userId 用户ID
     * @param deviceInfo 设备信息
     * @param ip 访问IP
     * @return 设备记录
     */
    UserDevice recordDevice(Long userId, DeviceInfoDTO deviceInfo, String ip);

    /**
     * 获取用户的所有设备列表
     * @param userId 用户ID
     * @return 设备列表
     */
    List<UserDeviceVO> getUserDevices(Long userId);

    /**
     * 获取用户的所有设备列表（带当前设备标识）
     * @param userId 用户ID
     * @param currentDeviceId 当前设备ID
     * @return 设备列表
     */
    List<UserDeviceVO> getUserDevices(Long userId, String currentDeviceId);

    /**
     * 删除设备（退出登录）
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void removeDevice(Long userId, Long deviceId);

    /**
     * 禁用/启用设备
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param status 状态：0禁用 1正常
     */
    void updateDeviceStatus(Long userId, Long deviceId, Integer status);
}

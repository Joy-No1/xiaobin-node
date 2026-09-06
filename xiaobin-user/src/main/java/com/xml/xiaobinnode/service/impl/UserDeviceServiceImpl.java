package com.xml.xiaobinnode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xml.xiaobinnode.common.dto.DeviceInfoDTO;
import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.entity.UserDevice;
import com.xml.xiaobinnode.mapper.UserDeviceMapper;
import com.xml.xiaobinnode.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户设备服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceMapper userDeviceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDevice recordDevice(Long userId, DeviceInfoDTO deviceInfo, String ip) {
        if (deviceInfo == null || deviceInfo.getDeviceId() == null) {
            log.warn("设备信息为空，userId={}", userId);
            return null;
        }

        // 查找是否已存在
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
               .eq(UserDevice::getDeviceId, deviceInfo.getDeviceId());
        UserDevice existingDevice = userDeviceMapper.selectOne(wrapper);

        if (existingDevice != null) {
            // 更新设备信息
            existingDevice.setLastIp(ip);
            existingDevice.setLastActiveAt(new Date());

            // 更新设备详细信息（可能变化）
            if (deviceInfo.getDeviceName() != null) {
                existingDevice.setDeviceName(deviceInfo.getDeviceName());
            }
            if (deviceInfo.getOsVersion() != null) {
                existingDevice.setOsVersion(deviceInfo.getOsVersion());
            }
            if (deviceInfo.getAppVersion() != null) {
                existingDevice.setAppVersion(deviceInfo.getAppVersion());
            }
            if (deviceInfo.getBrowser() != null) {
                existingDevice.setBrowser(deviceInfo.getBrowser());
            }

            userDeviceMapper.updateById(existingDevice);
            log.info("更新设备信息: userId={}, deviceId={}", userId, deviceInfo.getDeviceId());
            return existingDevice;
        } else {
            // 新增设备
            UserDevice newDevice = new UserDevice();
            newDevice.setUserId(userId);
            newDevice.setDeviceId(deviceInfo.getDeviceId());
            newDevice.setDeviceType(deviceInfo.getDeviceType());
            newDevice.setDeviceName(deviceInfo.getDeviceName());
            newDevice.setOsName(deviceInfo.getOsName());
            newDevice.setOsVersion(deviceInfo.getOsVersion());
            newDevice.setAppVersion(deviceInfo.getAppVersion());
            newDevice.setBrowser(deviceInfo.getBrowser());
            newDevice.setLastIp(ip);
            newDevice.setLastActiveAt(new Date());
            newDevice.setStatus(1);

            userDeviceMapper.insert(newDevice);
            log.info("新增设备信息: userId={}, deviceId={}", userId, deviceInfo.getDeviceId());
            return newDevice;
        }
    }

    @Override
    public List<UserDeviceVO> getUserDevices(Long userId) {
        return getUserDevices(userId, null);
    }

    @Override
    public List<UserDeviceVO> getUserDevices(Long userId, String currentDeviceId) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
               .orderByDesc(UserDevice::getLastActiveAt);

        List<UserDevice> devices = userDeviceMapper.selectList(wrapper);

        return devices.stream().map(device -> {
            UserDeviceVO vo = new UserDeviceVO();
            BeanUtils.copyProperties(device, vo);
            // 标记当前设备
            vo.setIsCurrent(currentDeviceId != null && currentDeviceId.equals(device.getDeviceId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDevice(Long userId, Long deviceId) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
               .eq(UserDevice::getId, deviceId);

        UserDevice device = userDeviceMapper.selectOne(wrapper);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        // 逻辑删除
        userDeviceMapper.deleteById(deviceId);
        log.info("删除设备: userId={}, deviceId={}", userId, deviceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceStatus(Long userId, Long deviceId, Integer status) {
        LambdaQueryWrapper<UserDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDevice::getUserId, userId)
               .eq(UserDevice::getId, deviceId);

        UserDevice device = userDeviceMapper.selectOne(wrapper);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        device.setStatus(status);
        userDeviceMapper.updateById(device);
        log.info("更新设备状态: userId={}, deviceId={}, status={}", userId, deviceId, status);
    }
}

package com.xml.xiaobinnode.api.feign.user;

import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.common.dto.UserProfileVO;
import com.xml.xiaobinnode.common.dto.UserUpdateDTO;
import com.xml.xiaobinnode.common.dto.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户服务 Feign 接口
 * <p>服务间调用直接返回裸数据，统一结果包装由聚合服务完成。
 */
@FeignClient(name = "xiaobin-user", contextId = "userFeignClient", path = "/users")
public interface UserFeignClient {

    /**
     * 获取当前用户信息（需传X-User-Id头）
     */
    @GetMapping("/me")
    UserVO getCurrentUser(@RequestHeader("X-User-Id") Long userId);

    /**
     * 更新当前用户信息
     */
    @PutMapping("/me")
    UserVO updateCurrentUser(@RequestHeader("X-User-Id") Long userId,
                             @RequestBody UserUpdateDTO userDTO);

    /**
     * 根据ID获取用户（user 模块返回 UserProfileVO：含关注状态）
     */
    @GetMapping("/{id}")
    UserProfileVO getUserById(@PathVariable("id") Long userId);

    /**
     * 根据手机号查找用户
     */
    @GetMapping("/search")
    UserVO searchByPhone(@RequestParam("phone") String phone);

    /**
     * 批量获取用户信息
     */
    @PostMapping("/batch")
    List<UserVO> getUsersByIds(@RequestBody List<Long> userIds);

    /**
     * 获取用户设备列表
     */
    @GetMapping("/me/devices")
    List<UserDeviceVO> getUserDevices(@RequestHeader("X-User-Id") Long userId,
                                      @RequestParam(required = false) String currentDeviceId);

    /**
     * 删除设备
     */
    @DeleteMapping("/me/devices/{deviceId}")
    void removeDevice(@RequestHeader("X-User-Id") Long userId,
                      @PathVariable("deviceId") Long deviceId);

    /**
     * 更新设备状态
     */
    @PutMapping("/me/devices/{deviceId}/status")
    void updateDeviceStatus(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable("deviceId") Long deviceId,
                           @RequestParam("status") Integer status);

    /**
     * 修改密码
     */
    @PutMapping("/me/password")
    void changePassword(@RequestHeader("X-User-Id") Long userId,
                       @RequestBody Map<String, String> request);

    /**
     * 更换手机号
     */
    @PutMapping("/me/phone")
    void changePhone(@RequestHeader("X-User-Id") Long userId,
                    @RequestBody Map<String, String> request);

    /**
     * 更换邮箱
     */
    @PutMapping("/me/email")
    void changeEmail(@RequestHeader("X-User-Id") Long userId,
                    @RequestBody Map<String, String> request);

    /**
     * 获取实名认证状态
     */
    @GetMapping("/me/real-name")
    Map<String, Object> getRealNameStatus(@RequestHeader("X-User-Id") Long userId);

    /**
     * 提交实名认证
     */
    @PostMapping("/me/real-name")
    void submitRealName(@RequestHeader("X-User-Id") Long userId,
                       @RequestBody Map<String, String> request);

    /**
     * 注销账号
     */
    @DeleteMapping("/me")
    void deleteAccount(@RequestHeader("X-User-Id") Long userId,
                      @RequestBody Map<String, String> body);
}

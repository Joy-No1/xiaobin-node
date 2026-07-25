package com.xml.xiaobinnode.user.service;

import com.xml.xiaobinnode.user.dto.LoginRequest;
import com.xml.xiaobinnode.user.dto.RegisterRequest;
import com.xml.xiaobinnode.user.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 注册
     */
    User register(RegisterRequest request);

    /**
     * 登录
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 获取当前用户信息
     */
    User getCurrentUser(Long userId);

    /**
     * 更新用户信息
     */
    User updateUser(Long userId, User user);

    /**
     * 获取用户信息
     */
    User getUserById(Long userId);

    /**
     * 根据手机号查找用户
     */
    User getUserByPhone(String phone);
}

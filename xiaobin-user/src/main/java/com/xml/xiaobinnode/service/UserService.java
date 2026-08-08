package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.dto.LoginRequest;
import com.xml.xiaobinnode.dto.LoginVO;
import com.xml.xiaobinnode.dto.RegisterRequest;
import com.xml.xiaobinnode.dto.UserDTO;
import com.xml.xiaobinnode.dto.UserProfileVO;
import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.entity.User;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    UserDTO getCurrentUserDTO(Long userId);

    User updateUser(Long userId, User user, Location location);

    User getUserById(Long userId);

    UserDTO getUserDTOById(Long userId);

    User getUserByPhone(String phone);

    /** 根据ID获取用户VO（扁平化，含地址） */
    UserVO getUserVOById(Long userId);

    /** 批量获取用户VO */
    List<UserVO> getUserVOsByIds(List<Long> userIds);

    /** 获取用户主页VO（含当前用户对该用户的关注状态） */
    UserProfileVO getUserProfileVO(Long currentUserId, Long targetUserId);
}

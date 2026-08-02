package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.dto.LoginRequest;
import com.xml.xiaobinnode.dto.RegisterRequest;
import com.xml.xiaobinnode.dto.UserDTO;
import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.entity.User;

import java.util.Map;

public interface UserService {

    User register(RegisterRequest request);

    Map<String, Object> login(LoginRequest request);

    UserDTO getCurrentUserDTO(Long userId);

    User updateUser(Long userId, User user, Location location);

    User getUserById(Long userId);

    UserDTO getUserDTOById(Long userId);

    User getUserByPhone(String phone);
}

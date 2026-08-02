package com.xml.xiaobinnode.dto;

import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.entity.User;
import lombok.Data;

/**
 * 用户信息 + 地址信息
 */
@Data
public class UserDTO {

    private User user;
    private Location location;
}

package com.xml.xiaobinnode.dto;

import com.xml.xiaobinnode.common.dto.UserDeviceVO;
import com.xml.xiaobinnode.common.dto.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** JWT Token */
    private String token;

    /** 用户信息 */
    private UserVO user;

    /** 用户设备列表 */
    private List<UserDeviceVO> devices;
}

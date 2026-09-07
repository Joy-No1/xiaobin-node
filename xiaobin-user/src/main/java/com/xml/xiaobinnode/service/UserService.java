package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.dto.*;
import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.entity.User;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    User registerByEmail(EmailRegisterRequest request);

    LoginVO login(LoginRequest request, String ip);

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

    /** 修改密码 */
    void changePassword(Long userId, ChangePasswordRequest request);

    /** 更换手机号 */
    void changePhone(Long userId, ChangePhoneRequest request);

    /** 更换邮箱 */
    void changeEmail(Long userId, ChangeEmailRequest request);

    /** 获取实名认证状态 */
    RealNameStatusVO getRealNameStatus(Long userId);

    /** 提交实名认证 */
    void submitRealName(Long userId, RealNameRequest request);

    /** 注销账号 */
    void deleteAccount(Long userId, String password);
}

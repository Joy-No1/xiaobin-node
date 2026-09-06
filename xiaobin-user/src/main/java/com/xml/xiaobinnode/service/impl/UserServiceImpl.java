package com.xml.xiaobinnode.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xml.xiaobinnode.api.feign.community.CommunityFeignClient;
import com.xml.xiaobinnode.common.dto.FollowStatusDTO;
import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.common.util.JwtUtils;
import com.xml.xiaobinnode.dto.*;
import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.entity.User;
import com.xml.xiaobinnode.mapper.LocationMapper;
import com.xml.xiaobinnode.mapper.UserMapper;
import com.xml.xiaobinnode.service.UserDeviceService;
import com.xml.xiaobinnode.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserDeviceService userDeviceService;

    @Autowired
    @Lazy
    private CommunityFeignClient communityFeignClient;

    @Override
    public User register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该手机号已注册");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setUsername("user_" + request.getPhone());
        user.setStatus("ACTIVE");
        user.setAvatarUrl("");
        userMapper.insert(user);

        log.info("用户注册成功: userId={}, phone={}", user.getId(), user.getPhone());
        return user;
    }

    @Override
    public LoginVO login(LoginRequest request, String ip) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getAccount())
                .or()
                .eq(User::getEmail, request.getAccount());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("账号不存在");
        }
        if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("phone", user.getPhone());
        String token = JwtUtils.generateToken(String.valueOf(user.getId()), claims);

        redisTemplate.opsForValue().set(
                CommonConstants.REDIS_TOKEN_KEY + user.getId(),
                token,
                CommonConstants.TOKEN_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );

        // 记录设备信息
        if (request.getDeviceInfo() != null) {
            userDeviceService.recordDevice(user.getId(), request.getDeviceInfo(), ip);
        }

        user.setPassword(null);
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(toUserVO(user));

        // 获取用户设备列表
        String currentDeviceId = request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceId() : null;
        loginVO.setDevices(userDeviceService.getUserDevices(user.getId(), currentDeviceId));

        log.info("用户登录成功: userId={}, ip={}, deviceId={}", user.getId(), ip, currentDeviceId);
        return loginVO;
    }

    @Override
    public UserDTO getCurrentUserDTO(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return buildUserDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(Long userId, User updateUser, Location location) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String locationId = user.getLocationId();
        if (location != null && StringUtils.isBlank(locationId) && hasLocationData(location)) {
            locationMapper.insert(location);
            user.setLocationId(String.valueOf(location.getId()));
        }

        // 更新已存在的地址信息
        if (ObjectUtils.allNotNull(locationId, location) && hasLocationData(location)) {
            Location dbLocation = locationMapper.selectById(locationId);
            if (Objects.nonNull(dbLocation)) {
                // 只更新地址字段，不复制id
                if (location.getProvince() != null) dbLocation.setProvince(location.getProvince());
                if (location.getCity() != null) dbLocation.setCity(location.getCity());
                if (location.getDistrict() != null) dbLocation.setDistrict(location.getDistrict());
                locationMapper.updateById(dbLocation);
            }
        }

        if (updateUser.getNickname() != null) user.setNickname(updateUser.getNickname());
        if (updateUser.getBio() != null) user.setBio(updateUser.getBio());
        if (updateUser.getGender() != null) user.setGender(updateUser.getGender());
        if (updateUser.getAvatarUrl() != null) user.setAvatarUrl(updateUser.getAvatarUrl());
        if (updateUser.getProfileBackgroundUrl() != null) user.setProfileBackgroundUrl(updateUser.getProfileBackgroundUrl());
        if (updateUser.getEmail() != null) user.setEmail(updateUser.getEmail());
        if (updateUser.getBirthday() != null) user.setBirthday(updateUser.getBirthday());
        if (updateUser.getCompany() != null) user.setCompany(updateUser.getCompany());
        if (updateUser.getSchool() != null) user.setSchool(updateUser.getSchool());
        if (updateUser.getHeight() != null) user.setHeight(updateUser.getHeight());
        if (updateUser.getWeight() != null) user.setWeight(updateUser.getWeight());
        if (updateUser.getEducation() != null) user.setEducation(updateUser.getEducation());

        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public UserDTO getUserDTOById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return null;
        user.setPassword(null);
        return buildUserDTO(user);
    }

    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    private UserDTO buildUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUser(user);
        dto.setLocation(getLocationByUser(user));
        return dto;
    }

    private Location getLocationByUser(User user) {
        if (user.getLocationId() == null || user.getLocationId().isEmpty()) {
            return null;
        }
        try {
            return locationMapper.selectById(Long.valueOf(user.getLocationId()));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasLocationData(Location location) {
        return location.getProvince() != null
                || location.getCity() != null
                || location.getDistrict() != null;
    }

    @Override
    public UserVO getUserVOById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return toUserVO(user);
    }

    @Override
    public List<UserVO> getUserVOsByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        users.forEach(user -> {
            user.setPassword(null);
            user.setPhone(null);
            user.setCreatedAt(null);
            user.setUpdatedAt(null);
            user.setCompany(null);
            user.setSchool(null);
            user.setHeight(null);
            user.setWeight(null);
            user.setEducation(null);
            user.setBirthday(null);
            user.setEmail(null);
        });
        return users.stream().map(this::toUserVO).collect(Collectors.toList());
    }

    /**
     * 将 User 实体转换为 UserVO（扁平化，含地址信息）
     */
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setProfileBackgroundUrl(user.getProfileBackgroundUrl());
        vo.setGender(user.getGender());
        vo.setBio(user.getBio());
        vo.setStatus(user.getStatus());
        vo.setRealNameVerified(user.getRealNameVerified());
        vo.setBirthday(user.getBirthday());
        vo.setCompany(user.getCompany());
        vo.setSchool(user.getSchool());
        vo.setHeight(user.getHeight());
        vo.setWeight(user.getWeight());
        vo.setEducation(user.getEducation());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());

        // 填充地址信息
        Location location = getLocationByUser(user);
        if (location != null) {
            vo.setProvince(location.getProvince());
            vo.setCity(location.getCity());
            vo.setDistrict(location.getDistrict());
        }

        return vo;
    }

    @Override
    public UserProfileVO getUserProfileVO(Long currentUserId, Long targetUserId) {
        User user = userMapper.selectById(targetUserId);
        if (user == null) {
            return null;
        }

        UserProfileVO profileVO = new UserProfileVO();
        BeanUtils.copyProperties(toUserVO(user), profileVO);

        // 获取关注状态（尽力而为，失败默认NONE，不影响用户信息返回）
        FollowStatusDTO followStatus = fetchFollowStatus(currentUserId, targetUserId);
        if (followStatus != null) {
            profileVO.setIsFollowing(followStatus.getIsFollowing());
            profileVO.setIsFollowedBy(followStatus.getIsFollowedBy());
            profileVO.setFollowStatus(followStatus.getFollowStatus());
        } else {
            profileVO.setIsFollowing(false);
            profileVO.setIsFollowedBy(false);
            profileVO.setFollowStatus("NONE");
        }

        return profileVO;
    }

    /**
     * 调用社区服务获取关注状态
     */
    private FollowStatusDTO fetchFollowStatus(Long currentUserId, Long targetUserId) {
        if (currentUserId == null) {
            return null;
        }
        try {
            return communityFeignClient.getFollowStatus(currentUserId, targetUserId);
        } catch (Exception e) {
            log.warn("获取关注状态失败: currentUserId={}, targetUserId={}", currentUserId, targetUserId, e);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新新密码
        user.setPassword(BCrypt.hashpw(request.getNewPassword()));
        userMapper.updateById(user);

        // 清除Redis中的token，强制重新登录
        redisTemplate.delete(CommonConstants.REDIS_TOKEN_KEY + userId);

        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePhone(Long userId, ChangePhoneRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // TODO: 验证短信验证码（需要接入短信服务）
        // if (!smsService.verifyCode(request.getNewPhone(), request.getVerifyCode())) {
        //     throw new BusinessException("验证码错误或已过期");
        // }

        // 检查新手机号是否已被使用
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getNewPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该手机号已被使用");
        }

        // 更新手机号
        user.setPhone(request.getNewPhone());
        userMapper.updateById(user);

        log.info("用户更换手机号成功: userId={}, oldPhone={}, newPhone={}",
                 userId, user.getPhone(), request.getNewPhone());
    }

    @Override
    public RealNameStatusVO getRealNameStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        RealNameStatusVO vo = new RealNameStatusVO();
        vo.setVerified(user.getRealNameVerified() != null && user.getRealNameVerified() == 1);

        if (vo.getVerified()) {
            vo.setRealName(user.getUsername());
            // 身份证号脱敏：保留前6位和后4位
            if (user.getIdCard() != null && user.getIdCard().length() == 18) {
                String idCard = user.getIdCard();
                vo.setIdCardMasked(idCard.substring(0, 6) + "********" + idCard.substring(14));
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRealName(Long userId, RealNameRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否已实名
        if (user.getRealNameVerified() != null && user.getRealNameVerified() == 1) {
            throw new BusinessException("您已完成实名认证，无法重复认证");
        }

        // 检查身份证号是否已被使用
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIdCard, request.getIdCard());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该身份证号已被使用");
        }

        // TODO: 调用实名认证服务验证身份信息
        // if (!realNameVerifyService.verify(request.getRealName(), request.getIdCard())) {
        //     throw new BusinessException("实名认证失败，请检查姓名和身份证号是否正确");
        // }

        // 更新实名信息
        user.setUsername(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setRealNameVerified(1);
        userMapper.updateById(user);

        log.info("用户提交实名认证成功: userId={}, realName={}", userId, request.getRealName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 设置账号为禁用状态（软删除）
        user.setStatus("DISABLED");
        userMapper.updateById(user);

        // 清除Redis中的token
        redisTemplate.delete(CommonConstants.REDIS_TOKEN_KEY + userId);

        // TODO: 清理用户相关数据（帖子、评论、关注关系等）
        // 可以使用消息队列异步处理

        log.info("用户注销账号: userId={}", userId);
    }
}

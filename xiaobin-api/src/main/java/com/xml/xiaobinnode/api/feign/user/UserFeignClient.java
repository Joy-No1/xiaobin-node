package com.xml.xiaobinnode.api.feign.user;

import com.xml.xiaobinnode.common.dto.UserProfileVO;
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
     * 更新当前用户信息（body 为 {user, location}，原样透传）
     */
    @PutMapping("/me")
    Object updateCurrentUser(@RequestHeader("X-User-Id") Long userId,
                             @RequestBody Map<String, Object> userDTO);

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
}

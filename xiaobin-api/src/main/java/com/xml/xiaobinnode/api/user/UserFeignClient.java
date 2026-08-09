package com.xml.xiaobinnode.api.user;

import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 Feign 接口
 */
@FeignClient(name = "xiaobin-user", path = "/api/v1/users")
public interface UserFeignClient {

    /** 获取当前用户信息（需传X-User-Id头） */
    @GetMapping("/me")
    Result<UserVO> getCurrentUser(@RequestHeader("X-User-Id") Long userId);

    /** 根据ID获取用户 */
    @GetMapping("/{id}")
    Result<UserVO> getUserById(@PathVariable("id") Long userId);

    /** 根据手机号查找用户 */
    @GetMapping("/search")
    Result<UserVO> searchByPhone(@RequestParam("phone") String phone);

    /** 批量获取用户信息，ids用逗号分隔 */
    @PostMapping("/batch")
    Result<List<UserVO>> getUsersByIds(@RequestBody List<Long> userIds);
}

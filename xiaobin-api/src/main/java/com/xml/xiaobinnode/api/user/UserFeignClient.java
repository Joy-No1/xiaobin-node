package com.xml.xiaobinnode.api.user;

import com.xml.xiaobinnode.common.dto.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 Feign 接口
 * <p>服务间调用直接返回裸数据，统一结果包装由网关完成。
 */
@FeignClient(name = "xiaobin-user", path = "/api/v1/users")
public interface UserFeignClient {

    /** 获取当前用户信息（需传X-User-Id头） */
    @GetMapping("/me")
    UserVO getCurrentUser(@RequestHeader("X-User-Id") Long userId);

    /** 根据ID获取用户 */
    @GetMapping("/{id}")
    UserVO getUserById(@PathVariable("id") Long userId);

    /** 根据手机号查找用户 */
    @GetMapping("/search")
    UserVO searchByPhone(@RequestParam("phone") String phone);

    /** 批量获取用户信息，ids用逗号分隔 */
    @PostMapping("/batch")
    List<UserVO> getUsersByIds(@RequestBody List<Long> userIds);
}

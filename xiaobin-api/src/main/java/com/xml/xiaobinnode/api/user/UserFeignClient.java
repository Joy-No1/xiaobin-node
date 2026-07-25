package com.xml.xiaobinnode.api.user;

import com.xml.xiaobinnode.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 用户服务 Feign 接口
 */
@FeignClient(name = "xiaobin-user", path = "/api/v1/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<Map<String, Object>> getUserById(@PathVariable("id") Long userId);

    @GetMapping("/batch")
    Result<Map<Long, Map<String, Object>>> getUsersByIds(@RequestParam("ids") String ids);
}

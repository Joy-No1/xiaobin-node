package com.xml.xiaobinnode.api.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 邮件服务 Feign 接口
 */
@FeignClient(name = "xiaobin-user", contextId = "emailFeignClient", path = "/email")
public interface EmailFeignClient {

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/verify-code")
    void sendVerifyCode(@RequestBody Map<String, String> request);
}

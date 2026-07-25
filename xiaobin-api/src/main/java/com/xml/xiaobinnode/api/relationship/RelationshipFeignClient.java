package com.xml.xiaobinnode.api.relationship;

import com.xml.xiaobinnode.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 关系服务 Feign 接口
 */
@FeignClient(name = "xiaobin-relationship", path = "/api/v1/relationships")
public interface RelationshipFeignClient {

    @GetMapping("/user/{userId}/partner")
    Result<Map<String, Object>> getPartnerInfo(@PathVariable("userId") Long userId);
}

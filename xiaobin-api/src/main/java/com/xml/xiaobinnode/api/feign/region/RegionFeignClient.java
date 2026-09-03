package com.xml.xiaobinnode.api.feign.region;

import com.xml.xiaobinnode.common.dto.SysRegionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 行政区划 Feign（转发 user 模块 /region）
 */
@FeignClient(name = "xiaobin-user", contextId = "userRegionFeignClient", path = "/region")
public interface RegionFeignClient {

    @GetMapping
    List<SysRegionDTO> getProvinces();

    @GetMapping("/children")
    List<SysRegionDTO> getChildren(@RequestParam(value = "parentCode", required = false) String parentCode);
}

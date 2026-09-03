package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.region.RegionFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.SysRegionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 聚合-行政区划（公开）
 */
@Tag(name = "聚合-行政区划")
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionAggController {

    private final RegionFeignClient regionFeignClient;

    @GetMapping
    @Operation(summary = "所有省份")
    public Result<List<SysRegionDTO>> getProvinces() {
        return Result.success(regionFeignClient.getProvinces());
    }

    @GetMapping("/children")
    @Operation(summary = "子级行政区")
    public Result<List<SysRegionDTO>> getChildren(@RequestParam(required = false) String parentCode) {
        return Result.success(regionFeignClient.getChildren(parentCode));
    }
}

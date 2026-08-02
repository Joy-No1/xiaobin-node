package com.xml.xiaobinnode.controller;


import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.entity.SysRegion;
import com.xml.xiaobinnode.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private SysRegionService sysRegionService;

    @GetMapping
    public Result<List<SysRegion>> findAllProvince() {
        return  Result.success(sysRegionService.getAllProvince());
    }

    @GetMapping("/children")
    public Result<List<SysRegion>> getChildByParentCode(@RequestParam(required = false) String parentCode) {
        return Result.success(sysRegionService.getChildren(parentCode));
    }
}

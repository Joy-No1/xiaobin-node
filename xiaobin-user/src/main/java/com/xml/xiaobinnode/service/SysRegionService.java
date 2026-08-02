package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.entity.SysRegion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author joy
* @description 针对表【sys_region(中国行政区表)】的数据库操作Service
* @createDate 2026-08-02 20:35:46
*/
public interface SysRegionService extends IService<SysRegion> {

    /**
     * 获取所有的省份
     * @return  省份集合
     */
    List<SysRegion> getAllProvince();

    /**
     * 获取下级城市
     * @param parentCode 上级城市编码
     * @return 城市集合
     */
    List<SysRegion> getChildren(String parentCode);
}

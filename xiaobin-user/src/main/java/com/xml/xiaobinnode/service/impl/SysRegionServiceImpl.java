package com.xml.xiaobinnode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xml.xiaobinnode.dto.CityLevelEnum;
import com.xml.xiaobinnode.entity.SysRegion;
import com.xml.xiaobinnode.service.SysRegionService;
import com.xml.xiaobinnode.mapper.SysRegionMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author joy
* @description 针对表【sys_region(中国行政区表)】的数据库操作Service实现
* @createDate 2026-08-02 20:35:45
*/
@Service
@RequiredArgsConstructor
public class SysRegionServiceImpl extends ServiceImpl<SysRegionMapper, SysRegion>
    implements SysRegionService{

    private final SysRegionMapper sysRegionMapper;

    @Override
    public List<SysRegion> getAllProvince() {
        return new LambdaQueryChainWrapper<>(sysRegionMapper)
                .eq(SysRegion::getLevel, CityLevelEnum.PROVINCE.getCode())
                .orderByAsc(SysRegion::getCode)
                .list();
    }

    @Override
    public List<SysRegion> getChildren(String parentCode) {
        LambdaQueryWrapper<SysRegion> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isBlank(parentCode)) {
            wrapper.isNull(SysRegion::getParentCode);
        } else {
            wrapper.eq(SysRegion::getParentCode, parentCode);
        }
        wrapper.orderByAsc(SysRegion::getCode);
        return sysRegionMapper.selectList(wrapper);
    }


}





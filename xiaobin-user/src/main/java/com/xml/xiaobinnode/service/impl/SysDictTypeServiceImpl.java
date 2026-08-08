package com.xml.xiaobinnode.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xml.xiaobinnode.entity.SysDictType;
import com.xml.xiaobinnode.mapper.SysDictTypeMapper;
import com.xml.xiaobinnode.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author joy
* @description 针对表【sys_dict_type(字典类型表)】的数据库操作Service实现
* @createDate 2026-08-08
*/
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;

    @Override
    public SysDictType getByTypeCode(String typeCode) {
        return new LambdaQueryChainWrapper<>(sysDictTypeMapper)
                .eq(SysDictType::getTypeCode, typeCode)
                .one();
    }
}

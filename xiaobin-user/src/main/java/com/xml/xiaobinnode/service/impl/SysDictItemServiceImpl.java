package com.xml.xiaobinnode.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xml.xiaobinnode.entity.SysDictItem;
import com.xml.xiaobinnode.mapper.SysDictItemMapper;
import com.xml.xiaobinnode.service.SysDictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author joy
* @description 针对表【sys_dict_item(字典项表)】的数据库操作Service实现
* @createDate 2026-08-08
*/
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> getActiveItemsByTypeCode(String typeCode) {
        return new LambdaQueryChainWrapper<>(sysDictItemMapper)
                .eq(SysDictItem::getTypeCode, typeCode)
                .eq(SysDictItem::getStatus, "ACTIVE")
                .orderByAsc(SysDictItem::getSortOrder)
                .list();
    }

    @Override
    public List<SysDictItem> getAllItemsByTypeCode(String typeCode) {
        return new LambdaQueryChainWrapper<>(sysDictItemMapper)
                .eq(SysDictItem::getTypeCode, typeCode)
                .orderByAsc(SysDictItem::getSortOrder)
                .list();
    }
}

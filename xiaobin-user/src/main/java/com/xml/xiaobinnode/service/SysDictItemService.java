package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author joy
* @description 针对表【sys_dict_item(字典项表)】的数据库操作Service
* @createDate 2026-08-08
*/
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 根据类型编码获取启用的字典项列表（按sort_order升序）
     * @param typeCode 类型编码
     * @return 字典项列表
     */
    List<SysDictItem> getActiveItemsByTypeCode(String typeCode);

    /**
     * 根据类型编码获取所有字典项列表（包含禁用项，按sort_order升序）
     * @param typeCode 类型编码
     * @return 字典项列表
     */
    List<SysDictItem> getAllItemsByTypeCode(String typeCode);
}

package com.xml.xiaobinnode.service;

import com.xml.xiaobinnode.entity.SysDictType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author joy
* @description 针对表【sys_dict_type(字典类型表)】的数据库操作Service
* @createDate 2026-08-08
*/
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 根据类型编码查询字典类型
     * @param typeCode 类型编码
     * @return 字典类型或null
     */
    SysDictType getByTypeCode(String typeCode);
}

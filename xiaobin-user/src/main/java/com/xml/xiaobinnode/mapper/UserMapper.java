package com.xml.xiaobinnode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xml.xiaobinnode.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.xml.xiaobinnode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xml.xiaobinnode.entity.Location;
import com.xml.xiaobinnode.service.LocationService;
import com.xml.xiaobinnode.mapper.LocationMapper;
import org.springframework.stereotype.Service;

/**
* @author joy
* @description 针对表【location(用户个人所在地信息表)】的数据库操作Service实现
* @createDate 2026-08-02 15:38:25
*/
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location>
    implements LocationService {

}





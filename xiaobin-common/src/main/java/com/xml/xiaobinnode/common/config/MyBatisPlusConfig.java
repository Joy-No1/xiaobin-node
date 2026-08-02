package com.xml.xiaobinnode.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "com.baomidou.mybatisplus.core.handlers.MetaObjectHandler")
public class MyBatisPlusConfig {

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", Date.class, new Date());
                this.strictInsertFill(metaObject, "updatedAt", Date.class, new Date());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", Date.class, new Date());
            }
        };
    }
}

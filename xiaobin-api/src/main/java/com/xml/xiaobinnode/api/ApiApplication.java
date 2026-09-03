package com.xml.xiaobinnode.api;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.xml.xiaobinnode.common.config.FeignUserContextInterceptor;
import com.xml.xiaobinnode.common.config.UserContextInterceptor;
import com.xml.xiaobinnode.common.config.WebMvcConfig;
import com.xml.xiaobinnode.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * 聚合 API 服务（对外统一封装返回值，Feign 转发各业务模块）
 * <p>聚合 Controller 放在 com.xml.xiaobinnode.api.controller。
 * <p>本服务不连库，显式排除 DataSource / MyBatis-Plus 自动配置；只引入用户上下文与统一异常处理。
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xml.xiaobinnode.api.feign"})
@Import({GlobalExceptionHandler.class, WebMvcConfig.class, UserContextInterceptor.class, FeignUserContextInterceptor.class})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}

package com.xml.xiaobinnode.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 所有外部 API 与行政区划先到聚合服务 xiaobin-api，由它统一封装并 Feign 转发到各业务服务
                .route("xiaobin-api", r -> r
                        .path("/api/**", "/region/**")
                        .uri("lb://xiaobin-api"))
                .build();
    }
}

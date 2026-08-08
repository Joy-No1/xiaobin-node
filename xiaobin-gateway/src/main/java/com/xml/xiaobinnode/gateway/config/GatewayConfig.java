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
                // 关注/取关/关注状态接口定义在 community 服务中，但路径以 /users/ 开头
                // 必须放在 user 路由之前，否则会被 /api/v1/users/** 抢占转发到 user 服务
                .route("xiaobin-community-follow", r -> r
                        .path("/api/v1/users/*/follow", "/api/v1/users/*/follow-status")
                        .uri("lb://xiaobin-community"))
                .route("xiaobin-user", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users/**")
                        .uri("lb://xiaobin-user"))
                .route("xiaobin-dict", r -> r
                        .path("/api/v1/dict/**")
                        .uri("lb://xiaobin-user"))
                .route("xiaobin-region", r -> r
                        .path("/region/**")
                        .uri("lb://xiaobin-user"))
                .route("xiaobin-relationship", r -> r
                        .path("/api/v1/relationships/**")
                        .uri("lb://xiaobin-relationship"))
                .route("xiaobin-community", r -> r
                        .path("/api/v1/posts/**", "/api/v1/comments/**", "/api/v1/files/**", "/api/v1/notifications/**", "/api/v1/me/**")
                        .uri("lb://xiaobin-community"))
                .route("xiaobin-chat", r -> r
                        .path("/api/v1/chat/**")
                        .uri("lb://xiaobin-chat"))
                .build();
    }
}

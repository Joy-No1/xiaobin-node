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
                .route("xiaobin-user", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users/**")
                        .uri("lb://xiaobin-user"))
                .route("xiaobin-relationship", r -> r
                        .path("/api/v1/relationships/**")
                        .uri("lb://xiaobin-relationship"))
                .route("xiaobin-community", r -> r
                        .path("/api/v1/posts/**", "/api/v1/comments/**", "/api/v1/files/**")
                        .uri("lb://xiaobin-community"))
                .route("xiaobin-chat", r -> r
                        .path("/api/v1/chat/**")
                        .uri("lb://xiaobin-chat"))
                .build();
    }
}

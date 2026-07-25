package com.xml.xiaobinnode.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务
                .route("xiaobin-user", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users/**")
                        .uri("lb://xiaobin-user"))
                // 关系服务
                .route("xiaobin-relationship", r -> r
                        .path("/api/v1/relationships/**")
                        .uri("lb://xiaobin-relationship"))
                // 社区服务
                .route("xiaobin-community", r -> r
                        .path("/api/v1/posts/**", "/api/v1/comments/**", "/api/v1/files/**")
                        .uri("lb://xiaobin-community"))
                // 聊天服务
                .route("xiaobin-chat", r -> r
                        .path("/api/v1/chat/**")
                        .uri("lb://xiaobin-chat"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}

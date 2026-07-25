package com.xml.xiaobinnode.gateway.filter;

import com.xml.xiaobinnode.common.constant.CommonConstants;
import com.xml.xiaobinnode.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway 全局认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final RedisTemplate<String, String> redisTemplate;

    /** 无需认证的路径 */
    private static final List<String> NO_AUTH_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars",
            "/doc.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 无需认证的路径直接放行
        for (String noAuthPath : NO_AUTH_PATHS) {
            if (path.startsWith(noAuthPath)) {
                return chain.filter(exchange);
            }
        }

        // 获取Token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(CommonConstants.TOKEN_PREFIX)) {
            log.warn("未携带Token: path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(CommonConstants.TOKEN_PREFIX.length());

        // 验证Token
        if (!JwtUtils.validateToken(token)) {
            log.warn("Token无效: path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 检查Redis中Token是否存在（支持Token失效）
        String userId = JwtUtils.getUserId(token);
        String cachedToken = redisTemplate.opsForValue().get(CommonConstants.REDIS_TOKEN_KEY + userId);
        if (cachedToken == null || !cachedToken.equals(token)) {
            log.warn("Token已失效: userId={}", userId);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 将userId添加到请求头传递给下游服务
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r.header("X-User-Id", userId))
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

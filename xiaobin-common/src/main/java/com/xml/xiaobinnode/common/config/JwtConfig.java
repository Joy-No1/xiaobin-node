package com.xml.xiaobinnode.common.config;

import com.xml.xiaobinnode.common.util.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /** JWT密钥 */
    private String secret = "xiaobin-node-secret-key-for-jwt-token-generation-2026";

    /** Token过期时间（秒），默认7天 */
    private long expireSeconds = 7 * 24 * 60 * 60;

    @PostConstruct
    public void init() {
        JwtUtils.setSecret(secret);
    }
}

package com.xml.xiaobinnode.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
public class JwtUtils {

    /**
     * 默认密钥（生产环境应通过配置注入）
     */
    private static final String DEFAULT_SECRET = "xiaobin-node-secret-key-for-jwt-token-generation-2026";
    private static final long DEFAULT_EXPIRE_SECONDS = 7 * 24 * 60 * 60; // 7天

    private static SecretKey secretKey;

    static {
        secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 设置密钥（应由配置类调用）
     */
    public static void setSecret(String secret) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     */
    public static String generateToken(String userId, Map<String, Object> claims) {
        return generateToken(userId, claims, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 生成JWT Token (指定过期时间)
     */
    public static String generateToken(String userId, Map<String, Object> claims, long expireSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取用户ID
     */
    public static String getUserId(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取Token过期时间
     */
    public static Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 判断Token是否过期
     */
    public static boolean isExpired(String token) {
        try {
            return getExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

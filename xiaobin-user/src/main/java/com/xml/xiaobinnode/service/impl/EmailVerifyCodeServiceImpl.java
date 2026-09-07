package com.xml.xiaobinnode.service.impl;

import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.service.EmailVerifyCodeService;
import com.xml.xiaobinnode.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮件验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerifyCodeServiceImpl implements EmailVerifyCodeService {

    private final EmailUtil emailUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CODE_PREFIX = "email:verify:code:";
    private static final String SEND_LIMIT_PREFIX = "email:send:limit:";
    private static final long CODE_EXPIRE_MINUTES = 5; // 验证码5分钟过期
    private static final long SEND_INTERVAL_SECONDS = 60; // 发送间隔60秒

    @Override
    public void sendVerifyCode(String email, String purpose) {
        // 检查发送频率限制
        String limitKey = SEND_LIMIT_PREFIX + email + ":" + purpose;
        Boolean hasLimit = redisTemplate.hasKey(limitKey);
        if (Boolean.TRUE.equals(hasLimit)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }

        // 生成验证码
        String code = EmailUtil.generateCode();

        // 保存到Redis（5分钟过期）
        String codeKey = CODE_PREFIX + email + ":" + purpose;
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 设置发送频率限制（60秒内不能重复发送）
        redisTemplate.opsForValue().set(limitKey, "1", SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // 发送邮件
        String purposeText = getPurposeText(purpose);
        emailUtil.sendVerifyCode(email, code, purposeText);

        log.info("验证码已发送: email={}, purpose={}, code={}", email, purpose, code);
    }

    @Override
    public boolean verifyCode(String email, String code, String purpose) {
        String codeKey = CODE_PREFIX + email + ":" + purpose;
        String savedCode = redisTemplate.opsForValue().get(codeKey);

        if (savedCode == null) {
            log.warn("验证码不存在或已过期: email={}, purpose={}", email, purpose);
            return false;
        }

        if (!savedCode.equals(code)) {
            log.warn("验证码错误: email={}, purpose={}, inputCode={}, savedCode={}",
                    email, purpose, code, savedCode);
            return false;
        }

        // 验证成功后删除验证码（一次性使用）
        redisTemplate.delete(codeKey);
        log.info("验证码验证成功: email={}, purpose={}", email, purpose);
        return true;
    }

    /**
     * 获取用途文本
     */
    private String getPurposeText(String purpose) {
        return switch (purpose) {
            case "REGISTER" -> "注册账号";
            case "LOGIN" -> "登录验证";
            case "CHANGE_PASSWORD" -> "修改密码";
            case "CHANGE_EMAIL" -> "更换邮箱";
            case "CHANGE_PHONE" -> "更换手机号";
            case "RESET_PASSWORD" -> "重置密码";
            default -> "身份验证";
        };
    }
}

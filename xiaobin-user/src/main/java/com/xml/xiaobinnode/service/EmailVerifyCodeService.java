package com.xml.xiaobinnode.service;

/**
 * 邮件验证码服务
 */
public interface EmailVerifyCodeService {

    /**
     * 发送验证码
     * @param email 邮箱地址
     * @param purpose 用途：REGISTER/LOGIN/CHANGE_PASSWORD/CHANGE_EMAIL/CHANGE_PHONE
     */
    void sendVerifyCode(String email, String purpose);

    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param purpose 用途
     * @return 是否验证通过
     */
    boolean verifyCode(String email, String code, String purpose);
}

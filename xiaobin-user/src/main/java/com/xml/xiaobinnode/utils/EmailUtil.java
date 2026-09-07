package com.xml.xiaobinnode.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 邮件服务工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @param purpose 验证码用途（注册/登录/修改密码/更换邮箱等）
     */
    public void sendVerifyCode(String to, String code, String purpose) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("【筱玢社交】" + purpose + "验证码");

            String content = buildVerifyCodeHtml(code, purpose);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("验证码邮件发送成功: to={}, purpose={}", to, purpose);
        } catch (MessagingException e) {
            log.error("验证码邮件发送失败: to={}, purpose={}", to, purpose, e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    /**
     * 发送通知邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendNotification(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("【筱玢社交】" + subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("通知邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("通知邮件发送失败: to={}, subject={}", to, subject, e);
            throw new RuntimeException("邮件发送失败");
        }
    }

    /**
     * 生成6位数字验证码
     */
    public static String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 构建验证码邮件HTML内容
     */
    private String buildVerifyCodeHtml(String code, String purpose) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; color: #333; margin-bottom: 20px; }
                    .code-box { background-color: #f0f8ff; border: 2px dashed #1890ff; border-radius: 5px; padding: 20px; text-align: center; margin: 20px 0; }
                    .code { font-size: 32px; font-weight: bold; color: #1890ff; letter-spacing: 5px; }
                    .tips { color: #666; font-size: 14px; line-height: 1.6; margin-top: 20px; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>筱玢社交 - """ + purpose + """
            验证码</h2>
                    </div>
                    <p>您好，</p>
                    <p>您正在进行<strong>""" + purpose + """
            </strong>操作，验证码为：</p>
                    <div class="code-box">
                        <div class="code">""" + code + """
            </div>
                    </div>
                    <div class="tips">
                        <p>• 验证码有效期为 <strong>5分钟</strong>，请尽快完成验证</p>
                        <p>• 请勿将验证码透露给他人</p>
                        <p>• 如非本人操作，请忽略此邮件</p>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿直接回复</p>
                        <p>&copy; 2026 筱玢社交. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}


package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.annotation.NoAuth;
import com.xml.xiaobinnode.service.EmailVerifyCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮件验证码接口
 */
@Tag(name = "邮件验证码", description = "邮箱验证码发送")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerifyCodeService emailVerifyCodeService;

    @NoAuth
    @PostMapping("/verify-code")
    @Operation(summary = "发送邮箱验证码")
    public void sendVerifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String purpose = request.get("purpose"); // REGISTER/LOGIN/CHANGE_PASSWORD/CHANGE_EMAIL/CHANGE_PHONE/RESET_PASSWORD
        emailVerifyCodeService.sendVerifyCode(email, purpose);
    }
}

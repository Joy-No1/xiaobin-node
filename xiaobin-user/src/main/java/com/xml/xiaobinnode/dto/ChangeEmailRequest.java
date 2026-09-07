package com.xml.xiaobinnode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更换邮箱请求
 */
@Data
@Schema(description = "更换邮箱请求")
public class ChangeEmailRequest {

    @NotBlank(message = "新邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "新邮箱地址")
    private String newEmail;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "邮箱验证码")
    private String verifyCode;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "当前账号密码")
    private String password;
}

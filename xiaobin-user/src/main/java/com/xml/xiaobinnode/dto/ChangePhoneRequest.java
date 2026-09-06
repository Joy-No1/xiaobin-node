package com.xml.xiaobinnode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更换手机号请求
 */
@Data
@Schema(description = "更换手机号请求")
public class ChangePhoneRequest {

    @NotBlank(message = "新手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "新手机号")
    private String newPhone;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "短信验证码")
    private String verifyCode;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "当前账号密码")
    private String password;
}

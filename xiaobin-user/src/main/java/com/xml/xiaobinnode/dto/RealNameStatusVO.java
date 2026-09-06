package com.xml.xiaobinnode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实名认证状态VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实名认证状态")
public class RealNameStatusVO {

    @Schema(description = "是否已实名认证")
    private Boolean verified;

    @Schema(description = "真实姓名（已认证才返回）")
    private String realName;

    @Schema(description = "身份证号（脱敏）")
    private String idCardMasked;
}

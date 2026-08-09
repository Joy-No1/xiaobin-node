package com.xml.xiaobinnode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 密码（加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatarUrl;

    /** 性别: MALE/FEMALE/OTHER */
    private String gender;

    /** 个人简介 */
    private String bio;

    /** 状态: ACTIVE/DISABLED */
    private String status;

    /** 生日 */
    private Date birthday;

    /** 公司 */
    private String company;

    /** 学校 */
    private String school;

    /** 身高 */
    private Double height;

    /** 体重 */
    private Double weight;

    /** 学历 */
    private String education;

    /** 地址ID */
    private String locationId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

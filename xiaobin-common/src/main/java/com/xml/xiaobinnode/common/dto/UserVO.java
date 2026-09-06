package com.xml.xiaobinnode.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 用户信息VO（扁平化，不含密码，供跨服务调用和前端展示）
 */
@Data
public class UserVO {

    private Long id;

    /** 用户名 */
    private String username;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatarUrl;

    /** 主页背景图URL */
    private String profileBackgroundUrl;

    /** 性别: MALE/FEMALE/OTHER */
    private String gender;

    /** 个人简介 */
    private String bio;

    /** 状态: ACTIVE/DISABLED */
    private String status;

    /** 是否实名认证：0否 1是 */
    private Integer realNameVerified;

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

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区/县 */
    private String district;

    private Date createdAt;

    private Date updatedAt;
}

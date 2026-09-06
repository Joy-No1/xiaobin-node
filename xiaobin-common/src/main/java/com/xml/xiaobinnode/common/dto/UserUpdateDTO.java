package com.xml.xiaobinnode.common.dto;

import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateDTO {

    /** 用户信息 */
    private UserUpdateInfo user;

    /** 地址信息 */
    private LocationInfo location;

    @Data
    public static class UserUpdateInfo {
        private String nickname;
        private String email;
        private String avatarUrl;
        private String profileBackgroundUrl;
        private String gender;
        private String bio;
        private String birthday;
        private String company;
        private String school;
        private Double height;
        private Double weight;
        private String education;
    }

    @Data
    public static class LocationInfo {
        private String province;
        private String city;
        private String district;
    }
}

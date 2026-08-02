package com.xml.xiaobinnode.dto;

import lombok.Getter;

@Getter
public enum CityLevelEnum {

    /**
     * 国家
     */
    COUNTRY("country", "国家"),

    /**
     * 省级
     */
    PROVINCE("province", "省"),

    /**
     * 市级
     */
    CITY("city", "市"),

    /**
     * 区县级
     */
    DISTRICT("district", "区/县"),

    /**
     * 街道级
     */
    STREET("street", "街道");

    private final String code;
    private final String description;

    CityLevelEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

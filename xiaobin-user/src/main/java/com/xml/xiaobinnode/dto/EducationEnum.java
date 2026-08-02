package com.xml.xiaobinnode.dto;

import lombok.Getter;

@Getter
public enum EducationEnum {

    HIGH_SCHOOL("HIGH_SCHOOL", "高中"),

    ASSOCIATE("ASSOCIATE", "大专"),

    BACHELOR("BACHELOR", "本科"),

    MASTER("MASTER", "硕士研究生"),

    DOCTOR("MASTER", "博士研究生"),

    OTHER("MASTER", "其他");

    private final String code;
    private final String description;

    EducationEnum(String code, String description) {
        this.code = code;
        this.description = description;

    }

}
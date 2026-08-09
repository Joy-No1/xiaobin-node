package com.xxl.job.admin.business.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */

@Data
public class XxlJobRegistry {

    private long id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;
}

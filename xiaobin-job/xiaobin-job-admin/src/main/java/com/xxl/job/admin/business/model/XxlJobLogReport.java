package com.xxl.job.admin.business.model;

import lombok.Data;

import java.util.Date;

@Data
public class XxlJobLogReport {

    private int id;
    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;

    private Date updateTime;


}

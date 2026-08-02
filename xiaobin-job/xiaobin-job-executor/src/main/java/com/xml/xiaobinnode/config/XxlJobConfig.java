package com.xml.xiaobinnode.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 执行器配置
 */
@Slf4j
@Configuration
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses:http://127.0.0.1:8088/xxl-job-admin}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appname:xiaobin-job}")
    private String appName;

    @Value("${xxl.job.executor.port:9999}")
    private int port;

    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info("XXL-Job 执行器启动: appName={}, adminAddress={}", appName, adminAddresses);
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        return executor;
    }
}

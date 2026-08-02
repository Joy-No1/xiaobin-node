package com.xml.xiaobinnode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * XXL-Job Admin 调度中心
 * <p>
 * 启动后访问: http://127.0.0.1:8088/xxl-job-admin
 * 默认账号: admin / 123456
 * <p>
 * 核心功能:
 * - 任务管理 (新增/编辑/暂停/启动)
 * - 调度日志查看
 * - 执行器管理 (自动注册 xiaobin-job 执行器)
 */
@SpringBootApplication(scanBasePackages = {"com.xml.xiaobinnode.admin"})
public class JobAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAdminApplication.class, args);
    }

}

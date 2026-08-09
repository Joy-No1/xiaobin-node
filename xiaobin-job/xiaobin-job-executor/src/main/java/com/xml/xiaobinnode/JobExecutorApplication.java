package com.xml.xiaobinnode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.xml.xiaobinnode")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xml.xiaobinnode.api")
public class JobExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobExecutorApplication.class, args);
    }

}

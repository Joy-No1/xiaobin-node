package com.xml.xiaobinnode.relationship;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.xml.xiaobinnode")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xml.xiaobinnode.api")
@MapperScan("com.xml.xiaobinnode.relationship.mapper")
public class RelationshipApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipApplication.class, args);
    }

}

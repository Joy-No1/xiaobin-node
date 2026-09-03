package com.xml.xiaobinnode.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
        basePackages = "com.xml.xiaobinnode",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.xml\\.xiaobinnode\\.api\\..*"))
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xml.xiaobinnode.api")
@MapperScan("com.xml.xiaobinnode.chat.mapper")
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}

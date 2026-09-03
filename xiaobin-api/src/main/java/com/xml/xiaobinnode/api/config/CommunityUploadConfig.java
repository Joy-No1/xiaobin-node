package com.xml.xiaobinnode.api.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

/**
 * 社区上传 Feign 专用配置：为 multipart 提供 SpringFormEncoder。
 * <p>注意：不加 @Configuration，避免被业务服务整包扫描成全局 Encoder。
 */
public class CommunityUploadConfig {

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}

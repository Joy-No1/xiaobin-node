package com.xml.xiaobinnode.common.annotation;

import java.lang.annotation.*;

/**
 * 无需认证注解 - 标记在控制器方法上跳过JWT校验
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuth {
}

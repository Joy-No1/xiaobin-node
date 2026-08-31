package com.xml.xiaobinnode.common.config;

import com.xml.xiaobinnode.common.util.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Feign 请求拦截器：把当前登录用户的 X-User-Id 透传给下游服务。
 * <p>服务间 Feign 调用不再经过网关，下游服务需要靠 X-User-Id 头才能通过
 * {@link UserContextInterceptor} 识别调用者身份；本拦截器在出站请求上补上该头，
 * 从而让「当前用户信息」在多个服务间共享。
 * <p>网关（无 feign-core 依赖）不满足 {@link ConditionalOnClass}，不会加载本组件。
 */
@Component
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignUserContextInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Feign 方法已通过 @RequestHeader("X-User-Id") 显式传值时，不覆盖
        if (template.headers().containsKey("X-User-Id")) {
            return;
        }
        String userId = UserContext.getUserId();
        if (userId != null) {
            template.header("X-User-Id", userId);
        }
    }
}

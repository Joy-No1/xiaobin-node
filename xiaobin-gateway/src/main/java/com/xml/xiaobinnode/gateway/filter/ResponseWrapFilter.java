package com.xml.xiaobinnode.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xml.xiaobinnode.common.dto.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 统一响应包装：对外部暴露的接口，将下游服务返回的裸数据包装成 {@link Result}。
 * <p>
 * 业务服务的 Controller 现在直接返回裸数据（供服务间 Feign 调用），
 * 只有经过网关的请求才在这里统一包装，保证外部调用方拿到的仍是
 * {@code {code, message, data, timestamp}} 结构。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseWrapFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                MediaType contentType = getDelegate().getHeaders().getContentType();
                HttpStatusCode statusCode = getDelegate().getStatusCode();
                boolean isJson = contentType != null && contentType.includes(MediaType.APPLICATION_JSON);
                boolean is2xx = statusCode != null && statusCode.value() >= 200 && statusCode.value() < 300;
                // JSON 响应或 2xx 空 body（void 接口）需要统一包装；其余（如文件流、非 JSON 错误）原样透传
                if (!isJson && !is2xx) {
                    return super.writeWith(body);
                }
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        byte[] bytes = readAll(dataBuffers);
                        byte[] wrappedBytes;
                        if (!isJson && bytes.length == 0) {
                            // void 接口：空 body 包为 Result.success()
                            getDelegate().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            wrappedBytes = writeJson(Result.success()).getBytes(StandardCharsets.UTF_8);
                        } else if (!isJson) {
                            // 非 JSON 内容（如下载文件）→ 原样透传
                            wrappedBytes = bytes;
                        } else {
                            String wrapped = wrapBody(new String(bytes, StandardCharsets.UTF_8), statusCode);
                            wrappedBytes = wrapped.getBytes(StandardCharsets.UTF_8);
                        }
                        getDelegate().getHeaders().setContentLength(wrappedBytes.length);
                        return bufferFactory().wrap(wrappedBytes);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 读取并释放所有 DataBuffer，拼成完整字节数组
     */
    private byte[] readAll(List<? extends DataBuffer> dataBuffers) {
        int total = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        byte[] bytes = new byte[total];
        int offset = 0;
        for (DataBuffer buffer : dataBuffers) {
            int len = buffer.readableByteCount();
            buffer.read(bytes, offset, len);
            offset += len;
            DataBufferUtils.release(buffer);
        }
        return bytes;
    }

    /**
     * 包装响应体：
     * <ul>
     *   <li>已是 {@link Result} 结构（异常处理器的错误响应）→ 原样透传，不改状态码；</li>
     *   <li>2xx 裸数据 → 包为 {@code Result.success(data)}；</li>
     *   <li>非 2xx 裸数据 → 包为 {@code Result.error(status, body)}；</li>
     *   <li>空 body → {@code Result.success()}。</li>
     * </ul>
     */
    private String wrapBody(String body, HttpStatusCode statusCode) {
        boolean is2xx = statusCode.value() >= 200 && statusCode.value() < 300;
        if (body == null || body.isBlank()) {
            return is2xx ? writeJson(Result.success()) : body;
        }
        try {
            JsonNode node = objectMapper.readTree(body);
            if (isResult(node)) {
                return body;
            }
            if (is2xx) {
                return writeJson(Result.success(node));
            }
            return writeJson(Result.error(statusCode.value(), body));
        } catch (Exception e) {
            log.warn("响应包装失败: status={}", statusCode.value(), e);
            return body;
        }
    }

    /**
     * 判断是否为统一响应体：含 code(int)/message(str)/data/timestamp(number) 四字段
     */
    private boolean isResult(JsonNode node) {
        if (node == null || !node.isObject()) {
            return false;
        }
        JsonNode code = node.get("code");
        JsonNode message = node.get("message");
        JsonNode timestamp = node.get("timestamp");
        return code != null && code.isInt()
                && message != null && message.isTextual()
                && node.has("data")
                && timestamp != null && timestamp.isNumber();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("响应序列化失败", e);
            return "{}";
        }
    }

    @Override
    public int getOrder() {
        // 在 AuthFilter(-100) 之后执行；认证短路（setComplete）时本过滤器不生效
        return -90;
    }
}

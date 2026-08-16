package com.xml.xiaobinnode.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xml.xiaobinnode.common.dto.Result;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        int status = resolveStatus(e.getCode());
        return ResponseEntity.status(status).body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Result<Void>> handleFeignException(FeignException e) {
        int status = e.status() > 0 ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = "服务调用失败";
        try {
            java.util.Optional<ByteBuffer> body = e.responseBody();
            if (body.isPresent()) {
                ByteBuffer buffer = body.get();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                JsonNode node = OBJECT_MAPPER.readTree(new String(bytes, StandardCharsets.UTF_8));
                if (node != null && node.has("code") && node.has("message")) {
                    status = resolveStatus(node.get("code").asInt());
                    message = node.get("message").asText();
                }
            }
        } catch (Exception ex) {
            log.debug("解析Feign异常响应体失败", ex);
        }
        log.warn("Feign调用失败: status={}, message={}", status, message, e);
        return ResponseEntity.status(status).body(Result.error(status, message));
    }

    /**
     * 将业务错误码映射为 HTTP 状态码：400/401/403/404 原样返回，其余统一为 500
     */
    private int resolveStatus(int code) {
        if (code == 400 || code == 401 || code == 403 || code == 404) {
            return code;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验异常: {}", message);
        return Result.badRequest(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数绑定失败");
        log.warn("参数绑定异常: {}", message);
        return Result.badRequest(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("约束校验异常: {}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.badRequest("请求体格式错误，请检查 JSON 格式与字段类型");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return Result.badRequest("参数 " + e.getName() + " 类型不正确");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("服务器内部错误，请稍后重试");
    }
}

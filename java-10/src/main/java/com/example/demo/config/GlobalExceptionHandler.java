package com.example.demo.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 全局异常处理器
 * <p>
 * 为什么需要全局异常处理：
 * 1. 将参数校验异常统一转为 422 状态码（API 契约约定）
 * 2. 避免将异常堆栈直接暴露给前端，提升安全性和用户体验
 * </p>
 * 为什么用 @RestControllerAdvice：
 * 组合了 @ControllerAdvice + @ResponseBody，异常处理结果自动序列化为 JSON。
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * <p>
     * 为什么捕获 BindException 而非 MethodArgumentNotValidException：
     * MethodArgumentNotValidException 继承自 BindException，
     * 捕获父类可同时覆盖表单绑定和 JSON 请求体两种场景的校验失败，
     * 且在 Spring Framework 7 中包路径更稳定。
     * </p>
     *
     * @param ex 绑定异常
     * @return 422 状态码 + 错误信息
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(BindException ex) {
        // 取第一个字段错误信息返回，避免响应体过于复杂
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数校验失败");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", message));
    }
}

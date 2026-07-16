package com.example.java12.config;  // 配置层包，存放拦截器与 Web 配置

import com.example.java12.dto.ApiResponse;  // 统一响应格式
import org.springframework.http.HttpStatus;  // HTTP 状态码
import org.springframework.validation.FieldError;  // 字段校验错误
import org.springframework.web.bind.MethodArgumentNotValidException;  // 参数校验异常
import org.springframework.web.bind.annotation.ExceptionHandler;  // 异常处理注解
import org.springframework.web.bind.annotation.ResponseStatus;  // 响应状态码注解
import org.springframework.web.bind.annotation.RestControllerAdvice;  // 全局异常处理注解

/**
 * 全局异常处理器
 * <p>
 * 统一捕获 Controller 层抛出的异常，转换为标准 ApiResponse 格式返回。
 * 避免将异常堆栈直接暴露给前端。
 * </p>
 */
@RestControllerAdvice  // 全局异常处理，返回 JSON
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（RuntimeException）
     * <p>
     * Service 层抛出的 RuntimeException 通常是业务校验失败（如"账号已存在"），
     * 返回 400 状态码和错误信息。
     * </p>
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)  // 捕获 RuntimeException
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 返回 400
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 校验失败）
     * <p>
     * 提取第一个字段校验错误信息返回给前端。
     * </p>
     *
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)  // 捕获校验异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 返回 400
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 获取第一个字段校验错误
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return ApiResponse.error(400, message);
    }

    /**
     * 处理其他未捕获异常
     * <p>
     * 兜底处理，返回 500 状态码和通用错误信息，不暴露异常详情。
     * </p>
     *
     * @param e 未捕获异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)  // 捕获所有异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 返回 500
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.error(500, "服务器内部错误");
    }
}

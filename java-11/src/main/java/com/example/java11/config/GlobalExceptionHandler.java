package com.example.java11.config;  // 配置层包，存放全局配置与异常处理

import com.example.java11.dto.ApiResponse;  // 统一响应包装 DTO
import jakarta.servlet.http.HttpServletRequest;  // Servlet 请求对象
import org.slf4j.Logger;  // SLF4J 日志接口
import org.slf4j.LoggerFactory;  // SLF4J 日志工厂
import org.springframework.http.HttpStatus;  // HTTP 状态码常量
import org.springframework.validation.FieldError;  // 校验错误字段信息
import org.springframework.web.bind.MethodArgumentNotValidException;  // @Valid 校验失败异常
import org.springframework.web.bind.annotation.ExceptionHandler;  // 异常处理方法注解
import org.springframework.web.bind.annotation.ResponseStatus;  // 响应状态码注解
import org.springframework.web.bind.annotation.RestControllerAdvice;  // 全局异常处理注解

import java.util.stream.Collectors;  // 流式收集器

/**
 * 全局异常处理器
 * <p>
 * 统一捕获 Controller 层抛出的异常，转换为标准 ApiResponse 格式返回前端。
 * 处理的异常类型：
 * <ol>
 *   <li>RuntimeException：业务异常，返回 400 + 错误消息；</li>
 *   <li>IllegalArgumentException：参数校验异常，返回 400；</li>
 *   <li>MethodArgumentNotValidException：@Valid 注解校验失败，返回 422 + 字段错误详情；</li>
 *   <li>Exception：未知系统异常，返回 500 + 通用错误消息。</li>
 * </ol>
 * </p>
 */
@RestControllerAdvice  // 全局异常处理，拦截所有 Controller 抛出的异常
public class GlobalExceptionHandler {  // 全局异常处理器

    /** 日志记录器，用于输出异常堆栈便于排查 */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务运行时异常
     * <p>
     * Service 层抛出的 RuntimeException 通常携带业务错误消息（如"用户名已存在"），
     * 直接将消息返回给前端，HTTP 状态码设为 400。
     * </p>
     *
     * @param e 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)  // 捕获 RuntimeException
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 返回 400 状态码
    public ApiResponse handleRuntimeException(RuntimeException e) {  // 业务异常处理
        log.warn("业务异常: {}", e.getMessage());  // 记录警告级别日志
        return ApiResponse.error(e.getMessage());  // 返回错误消息
    }

    /**
     * 处理参数非法异常
     *
     * @param e 参数异常
     * @return 统一错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)  // 捕获非法参数异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 返回 400
    public ApiResponse handleIllegalArgumentException(IllegalArgumentException e) {  // 参数异常处理
        log.warn("参数异常: {}", e.getMessage());  // 记录日志
        return ApiResponse.error(e.getMessage());  // 返回错误消息
    }

    /**
     * 处理 @Valid 校验失败异常
     * <p>
     * 提取所有字段校验错误，拼接为可读的错误消息返回前端。
     * HTTP 状态码设为 422（Unprocessable Entity），表示请求格式正确但语义错误。
     * </p>
     *
     * @param e 校验异常
     * @return 统一错误响应，含字段级错误详情
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)  // 捕获校验异常
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 返回 422
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {  // 校验异常处理
        // 提取所有字段错误的消息，拼接为逗号分隔的字符串
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)  // 取出每个字段的错误消息
                .collect(Collectors.joining("; "));  // 用分号拼接
        log.warn("参数校验失败: {}", errorMessage);  // 记录日志
        return ApiResponse.error(errorMessage);  // 返回拼接后的错误消息
    }

    /**
     * 处理未知系统异常
     * <p>
     * 兜底异常处理，防止堆栈信息泄露给前端。返回通用错误消息，
     * 完整异常堆栈输出到日志便于开发排查。
     * </p>
     *
     * @param e       未知异常
     * @param request HTTP 请求对象，用于记录请求路径
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)  // 捕获所有未处理的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 返回 500
    public ApiResponse handleException(Exception e, HttpServletRequest request) {  // 兜底异常处理
        log.error("系统异常, 请求路径: {}", request.getRequestURI(), e);  // 记录完整堆栈
        return ApiResponse.error("系统内部错误，请稍后重试");  // 返回通用错误消息，不泄露堆栈
    }
}

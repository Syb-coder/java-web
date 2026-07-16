package com.example.java12.dto;  // DTO 层包，存放请求与响应数据传输对象

/**
 * 统一 API 响应格式
 * <p>
 * 所有接口返回此格式，前端通过 code 判断请求是否成功，
 * message 展示提示信息， data 携带业务数据。
 * </p>
 *
 * @param <T> 业务数据类型
 */
public class ApiResponse<T> {

    /** 状态码：200 成功，400 参数错误，401 未登录，403 无权限，500 服务器错误 */
    private int code;

    /** 提示信息（成功/失败描述） */
    private String message;

    /** 业务数据 */
    private T data;

    /**
     * 无参构造器（JSON 反序列化用）
     */
    public ApiResponse() {
    }

    /**
     * 全参构造器
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    业务数据
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param <T>  数据类型
     * @param data 业务数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带提示信息和数据）
     *
     * @param <T>     数据类型
     * @param message 提示信息
     * @param data    业务数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败响应（带状态码和提示信息）
     *
     * @param <T>     数据类型
     * @param code    状态码
     * @param message 提示信息
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // ===== getter / setter =====

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

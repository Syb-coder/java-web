package com.example.java11.dto;

/**
 * 统一响应包装类
 * <p>
 * 用于将 Controller 返回结果统一包装为标准格式，包含成功标志、提示消息与数据载荷。
 * 通过静态工厂方法快速构造成功或失败的响应结果。
 * </p>
 */
public class ApiResponse {

    /** 是否成功 */
    private boolean success;

    /** 提示消息 */
    private String message;

    /** 数据载荷 */
    private Object data;

    /**
     * 默认无参构造器
     */
    public ApiResponse() {
    }

    /**
     * 全参构造器
     *
     * @param success 是否成功
     * @param message 提示消息
     * @param data    数据载荷
     */
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造成功响应（默认提示消息）
     *
     * @param data 数据载荷
     * @return 成功响应包装对象
     */
    public static ApiResponse success(Object data) {
        return new ApiResponse(true, "操作成功", data);
    }

    /**
     * 构造成功响应（自定义提示消息）
     *
     * @param message 提示消息
     * @param data    数据载荷
     * @return 成功响应包装对象
     */
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    /**
     * 构造失败响应
     *
     * @param message 错误提示消息
     * @return 失败响应包装对象
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

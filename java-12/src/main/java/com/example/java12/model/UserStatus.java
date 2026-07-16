package com.example.java12.model;  // 模型层包，存放实体与枚举

/**
 * 用户状态枚举
 * <p>
 * 标记用户账号的当前状态。封禁用户无法登录，其帖子自动隐藏。
 * </p>
 */
public enum UserStatus {

    /** 正常状态：可登录、可操作 */
    NORMAL,

    /** 封禁状态：无法登录，帖子自动隐藏 */
    BANNED
}

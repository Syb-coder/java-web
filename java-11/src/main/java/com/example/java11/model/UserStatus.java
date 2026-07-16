package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 用户状态枚举
 * <p>
 * 用于标识用户账户的当前状态，控制登录与发帖权限。
 * NORMAL: 正常状态，可登录、发帖、评论；
 * BANNED: 封禁状态，禁止登录与发言。
 * </p>
 */
public enum UserStatus {  // 用户状态枚举
    NORMAL,  // 正常
    BANNED   // 封禁
}

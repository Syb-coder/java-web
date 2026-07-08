// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

/**
 * 用户状态枚举
 * <p>
 * 用于违规账号封禁机制：
 * <ul>
 *   <li>ACTIVE：正常状态，可登录交易</li>
 *   <li>BANNED：已封禁，禁止登录与发布</li>
 * </ul>
 * </p>
 */
public enum UserStatus {
    // 正常：账号可用
    ACTIVE,
    // 封禁：违规账号，禁止登录
    BANNED
}

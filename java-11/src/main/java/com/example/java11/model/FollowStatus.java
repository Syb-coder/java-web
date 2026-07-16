package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 关注关系状态枚举
 * <p>
 * 标识用户之间关注关系的类型，用于区分单向关注与双向互关。
 * FOLLOWING: 关注中，单向关注关系；
 * MUTUAL: 互相关注，双方已建立双向关注。
 * </p>
 */
public enum FollowStatus {  // 关注关系状态枚举
    FOLLOWING,  // 关注中
    MUTUAL      // 互相关注
}

package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 管理员角色枚举
 * <p>
 * 区分超级管理员与版主，用于权限控制。
 * ADMIN: 超级管理员，拥有后台全部权限；
 * MODERATOR: 版主，仅有所属板块的帖子审核与举报处理权限。
 * </p>
 */
public enum AdminRole {  // 管理员角色枚举
    ADMIN,  // 超级管理员
    MODERATOR  // 版主
}

package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 评论状态枚举
 * <p>
 * 标识评论的可见性状态，支持评论的隐藏与逻辑删除。
 * NORMAL: 正常状态，前台可见；
 * HIDDEN: 隐藏状态，因违规被管理员隐藏，前台不可见但数据保留；
 * DELETED: 删除状态，用户或管理员删除，逻辑删除不物理移除。
 * </p>
 */
public enum CommentStatus {  // 评论状态枚举
    NORMAL,  // 正常
    HIDDEN,  // 隐藏
    DELETED  // 删除
}

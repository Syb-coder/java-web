package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 目标对象类型枚举
 * <p>
 * 用于多态关联场景，标识点赞、举报、通知等业务对象的目标实体类型。
 * POST: 帖子；
 * COMMENT: 评论；
 * WORK: 作品；
 * NEWS: 新闻；
 * USER: 用户。
 * </p>
 */
public enum TargetType {  // 目标对象类型枚举
    POST,     // 帖子
    COMMENT,  // 评论
    WORK,     // 作品
    NEWS,     // 新闻
    USER      // 用户
}

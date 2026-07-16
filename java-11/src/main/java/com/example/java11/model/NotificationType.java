package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 通知类型枚举
 * <p>
 * 区分通知消息的业务来源，便于前端按类型展示与筛选。
 * REPLY: 回复提醒，他人回复了用户的帖子或评论；
 * LIKE: 点赞提醒，他人点赞了用户的内容；
 * MENTION: @提及，他人 @ 了该用户；
 * SYSTEM: 系统公告，由系统广播的通知；
 * REPORT: 举报处理结果，用户提交的举报处理完毕后通知。
 * </p>
 */
public enum NotificationType {  // 通知类型枚举
    REPLY,    // 回复提醒
    LIKE,     // 点赞提醒
    MENTION,  // @提及
    SYSTEM,   // 系统公告
    REPORT    // 举报处理结果
}

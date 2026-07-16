package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 新闻资讯分类枚举
 * <p>
 * 用于对站内新闻资讯进行分类管理，便于用户按类别检索。
 * INDUSTRY: 行业资讯，二次元产业动态；
 * NEW_ANIME: 新番速递，新番动画播出信息；
 * WORK_INFO: 作品资讯，漫画/游戏/轻小说等作品动态；
 * EVENT: 活动事件，漫展、线下活动、企划等。
 * </p>
 */
public enum NewsCategory {  // 新闻资讯分类枚举
    INDUSTRY,    // 行业资讯
    NEW_ANIME,   // 新番速递
    WORK_INFO,   // 作品资讯
    EVENT        // 活动事件
}

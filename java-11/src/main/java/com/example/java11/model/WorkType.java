package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 作品类型枚举
 * <p>
 * 标识二次元作品的载体类型，用于作品库的分类与筛选。
 * ANIME: 动画，包括 TV/OVA/剧场版等；
 * MANGA: 漫画，包括连载/单行本/条漫等；
 * GAME: 游戏，包括 Galgame/RPG 等；
 * NOVEL: 轻小说，包括文库本/网络小说等；
 * OTHER: 其他类型，如广播剧、音乐等。
 * </p>
 */
public enum WorkType {  // 作品类型枚举
    ANIME,   // 动画
    MANGA,   // 漫画
    GAME,    // 游戏
    NOVEL,   // 轻小说
    OTHER    // 其他
}

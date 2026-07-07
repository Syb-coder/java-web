// 声明包路径
package com.example.java2.dto;

/**
 * 仪表板统计响应 DTO
 * <p>
 * 后台首页展示平台核心数据概览。
 * </p>
 *
 * @param articleCount   已发布文章数
 * @param categoryCount  分类数
 * @param userCount      注册用户数
 * @param questionCount  题库题目数
 * @param commentCount   评论总数
 * @param totalViewCount 全平台阅读量总和
 * @param testCount      答题次数总和
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；后台首页仪表板展示平台核心数据概览
public record StatsResponse(
        long articleCount,   // 已发布文章数（不含草稿）
        long categoryCount,  // 分类总数
        long userCount,      // 注册用户总数
        long questionCount,  // 题库题目总数
        long commentCount,   // 评论总数
        long totalViewCount, // 全平台文章阅读量总和
        long testCount       // 全平台答题次数总和
) {
}

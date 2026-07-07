package com.example.java8.dto;

/**
 * 仪表盘统计响应 DTO
 *
 * @param fabricCount 面料总数
 * @param styleCount  款式总数
 * @param orderCount  订单总数
 * @param userCount   用户总数
 */
public record StatsResponse(long fabricCount, long styleCount, long orderCount, long userCount) {
}

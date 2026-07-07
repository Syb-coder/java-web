package com.example.java8.dto;

/**
 * 款式响应 DTO
 *
 * @param id          主键 ID
 * @param name        名称
 * @param category    类别
 * @param craftFee    工费
 * @param description 描述
 * @param createTime  创建时间
 */
public record StyleResponse(Long id, String name, String category, Double craftFee,
                            String description, java.time.LocalDateTime createTime) {
}

package com.example.java8.dto;

/**
 * 面料响应 DTO
 *
 * @param id          主键 ID
 * @param name        名称
 * @param material    材质
 * @param color       颜色
 * @param unitPrice   单价
 * @param stock       库存
 * @param description 描述
 * @param createTime  创建时间
 */
public record FabricResponse(Long id, String name, String material, String color,
                             Double unitPrice, Double stock, String description,
                             java.time.LocalDateTime createTime, java.time.LocalDateTime updateTime) {
}

package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 面料请求 DTO（新增/编辑共用）
 *
 * @param name        面料名称
 * @param material    材质
 * @param color       颜色
 * @param unitPrice   单价（元/米）
 * @param stock       库存（米）
 * @param description 详细描述
 */
public record FabricRequest(
        @NotBlank(message = "名称不能为空") String name,
        @NotBlank(message = "材质不能为空") String material,
        @NotBlank(message = "颜色不能为空") String color,
        @NotNull(message = "单价不能为空") Double unitPrice,
        Double stock,
        String description
) {
}

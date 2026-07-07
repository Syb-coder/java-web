package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 款式请求 DTO（新增/编辑共用）
 *
 * @param name        款式名称
 * @param category    类别
 * @param craftFee    工费（元）
 * @param description 详细描述
 */
public record StyleRequest(
        @NotBlank(message = "名称不能为空") String name,
        @NotBlank(message = "类别不能为空") String category,
        @NotNull(message = "工费不能为空") Double craftFee,
        String description
) {
}

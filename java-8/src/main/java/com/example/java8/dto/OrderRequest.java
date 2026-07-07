package com.example.java8.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 创建订单请求 DTO
 *
 * @param styleId       款式 ID
 * @param fabricId      面料 ID
 * @param measurementId 量体数据 ID
 * @param remark        备注
 */
public record OrderRequest(
        @NotNull(message = "款式不能为空") Long styleId,
        @NotNull(message = "面料不能为空") Long fabricId,
        @NotNull(message = "量体数据不能为空") Long measurementId,
        String remark
) {
}

package com.example.java8.dto;

import com.example.java8.model.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单响应 DTO
 * <p>包含关联实体的名称信息，避免前端再发多次请求查询。</p>
 *
 * @param id              订单 ID
 * @param userId          用户 ID
 * @param userName        用户名
 * @param styleId         款式 ID
 * @param styleName       款式名称
 * @param fabricId        面料 ID
 * @param fabricName      面料名称
 * @param measurementId   量体 ID
 * @param measurementName 量体名称
 * @param totalPrice      总价
 * @param status          状态
 * @param remark          备注
 * @param createTime      下单时间
 */
public record OrderResponse(Long id, Long userId, String userName,
                            Long styleId, String styleName,
                            Long fabricId, String fabricName,
                            Long measurementId, String measurementName,
                            Double totalPrice, OrderStatus status,
                            String remark, LocalDateTime createTime) {
}

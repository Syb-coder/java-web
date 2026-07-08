package com.example.java3.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 交易评价请求 DTO
 */
public class ReviewRequest {

    /** 订单 ID */
    @NotNull(message = "订单不能为空")
    private Long orderId;

    /** 评分 1-5 */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低 1 分")
    @Max(value = 5, message = "评分最高 5 分")
    private Integer rating;

    /** 评价内容 */
    @Size(max = 500, message = "评价内容不能超过 500 字")
    private String content;

    // ===== Getter / Setter =====

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

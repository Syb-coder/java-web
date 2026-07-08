package com.example.java3.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 下单请求 DTO
 */
public class OrderRequest {

    /** 商品 ID */
    @NotNull(message = "商品不能为空")
    private Long productId;

    /** 买家留言 */
    @Size(max = 500, message = "留言不能超过 500 字")
    private String buyerRemark;

    // ===== Getter / Setter =====

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }
}

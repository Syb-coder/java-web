package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 私信发送请求 DTO
 */
public class MessageRequest {

    /** 接收者用户 ID */
    @NotNull(message = "接收者不能为空")
    private Long receiverId;

    /** 关联商品 ID（可选） */
    private Long productId;

    /** 消息内容 */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容不能超过 1000 字")
    private String content;

    // ===== Getter / Setter =====

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

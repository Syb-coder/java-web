package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 反馈回复请求 DTO（管理员处理反馈时使用）
 */
public class FeedbackReplyRequest {

    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容不能超过 500 字")
    private String reply;

    // ===== Getter / Setter =====

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}

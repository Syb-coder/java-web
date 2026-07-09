package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解

/**
 * 工单回复请求 DTO
 * <p>
 * 运营人员或客服对工单进行回复，reply 为回复内容。
 * </p>
 */
public class TicketReplyRequest {  // 工单回复请求 DTO 类定义

    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")  // 非空字符串校验:回复内容必填,防止空回复
    private String reply;  // 回复内容(客服/运营填写的答复)

    // —— reply 字段的 getter/setter ——
    public String getReply() {  // 获取回复内容
        return reply;  // 返回回复内容
    }

    public void setReply(String reply) {  // 设置回复内容
        this.reply = reply;  // 赋值回复内容
    }
}

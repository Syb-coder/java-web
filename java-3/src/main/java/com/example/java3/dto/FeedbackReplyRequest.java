// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 反馈回复请求 DTO（管理员处理反馈时使用）
 * <p>
 * 管理员在后台处理用户反馈时填写回复内容，反馈状态随之变更为已处理。
 * </p>
 */
public class FeedbackReplyRequest {

    // 回复内容字段，必填，限制最长 500 字
    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    // Size：限定回复内容最长 500 字符
    @Size(max = 500, message = "回复内容不能超过 500 字")
    private String reply;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取回复内容
    public String getReply() {
        return reply;
    }

    // 设置回复内容
    public void setReply(String reply) {
        this.reply = reply;
    }
}

// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 NotNull 校验注解，确保对象非 null
import jakarta.validation.constraints.NotNull;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 私信发送请求 DTO
 * <p>
 * 用户之间发送私信，可关联具体商品便于交易沟通。
 * </p>
 */
public class MessageRequest {

    // 接收者用户 ID 字段，必填，指定私信送达对象
    /** 接收者用户 ID */
    @NotNull(message = "接收者不能为空")
    private Long receiverId;

    // 关联商品 ID 字段，可选；咨询商品时填写，便于消息列表关联展示
    /** 关联商品 ID（可选） */
    private Long productId;

    // 消息内容字段，必填，限制最长 1000 字
    /** 消息内容 */
    @NotBlank(message = "消息内容不能为空")
    // Size：限定消息内容最长 1000 字符
    @Size(max = 1000, message = "消息内容不能超过 1000 字")
    private String content;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取接收者用户 ID
    public Long getReceiverId() {
        return receiverId;
    }

    // 设置接收者用户 ID
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // 获取关联商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置关联商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取消息内容
    public String getContent() {
        return content;
    }

    // 设置消息内容
    public void setContent(String content) {
        this.content = content;
    }
}

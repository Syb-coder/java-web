// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Max 校验注解，限定数值最大值
import jakarta.validation.constraints.Max;
// 导入 Min 校验注解，限定数值最小值
import jakarta.validation.constraints.Min;
// 导入 NotNull 校验注解，确保对象非 null
import jakarta.validation.constraints.NotNull;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 交易评价请求 DTO
 * <p>
 * 买家在订单完成后对卖家进行评分与评价，用于信誉积累。
 * </p>
 */
public class ReviewRequest {

    // 订单 ID 字段，必填，评价必须绑定具体订单
    /** 订单 ID */
    @NotNull(message = "订单不能为空")
    private Long orderId;

    // 评分字段，必填，取值范围 1-5 分
    /** 评分 1-5 */
    @NotNull(message = "评分不能为空")
    // Min：限定评分最小值为 1
    @Min(value = 1, message = "评分最低 1 分")
    // Max：限定评分最大值为 5
    @Max(value = 5, message = "评分最高 5 分")
    private Integer rating;

    // 评价内容字段，可选，限制最长 500 字
    /** 评价内容 */
    // Size：限定评价内容最长 500 字符
    @Size(max = 500, message = "评价内容不能超过 500 字")
    private String content;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取订单 ID
    public Long getOrderId() {
        return orderId;
    }

    // 设置订单 ID
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 获取评分
    public Integer getRating() {
        return rating;
    }

    // 设置评分
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    // 获取评价内容
    public String getContent() {
        return content;
    }

    // 设置评价内容
    public void setContent(String content) {
        this.content = content;
    }
}

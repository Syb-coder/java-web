// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotNull 校验注解，确保对象非 null
import jakarta.validation.constraints.NotNull;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 下单请求 DTO
 * <p>
 * 买家提交购买请求，由 Service 层校验商品状态后生成订单。
 * </p>
 */
public class OrderRequest {

    // 商品 ID 字段，必填，指定要购买的商品
    /** 商品 ID */
    @NotNull(message = "商品不能为空")
    private Long productId;

    // 买家留言字段，可选，限制最长 500 字
    /** 买家留言 */
    // Size：限定留言最长 500 字符
    @Size(max = 500, message = "留言不能超过 500 字")
    private String buyerRemark;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取买家留言
    public String getBuyerRemark() {
        return buyerRemark;
    }

    // 设置买家留言
    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }
}

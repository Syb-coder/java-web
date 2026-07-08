// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 NotNull 校验注解，确保对象非 null（适用于包装类型、数值等）
import jakarta.validation.constraints.NotNull;
// 导入 Positive 校验注解，确保数值为正数（> 0）
import jakarta.validation.constraints.Positive;

/**
 * 商品发布/编辑请求 DTO
 * <p>
 * 学生发布二手商品或编辑商品时提交的数据，需经过管理员审核后才能上架。
 * </p>
 */
public class ProductRequest {

    // 商品标题字段，必填且为搜索关键字
    /** 商品标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    // 商品描述字段，可选，详细介绍商品成色、使用情况等
    /** 商品描述 */
    private String description;

    // 期望价格字段，单位元，必须为正数
    /** 期望价格 */
    @NotNull(message = "价格不能为空")
    // Positive：确保价格严格大于 0，防止 0 元或负数价格
    @Positive(message = "价格必须大于 0")
    private Double price;

    // 原价字段，可选，用于展示折扣力度
    /** 原价（可选） */
    private Double originalPrice;

    // 图片 URL 字段，多张图片用分号分隔，前端轮播展示
    /** 图片 URL（多张分号分隔） */
    private String images;

    // 新旧程度字段，例如全新、九成新、八成新等
    /** 新旧程度 */
    private String conditionLevel;

    // 所属分类 ID，必须指向有效的商品分类
    /** 分类 ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取商品标题
    public String getTitle() {
        return title;
    }

    // 设置商品标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取商品描述
    public String getDescription() {
        return description;
    }

    // 设置商品描述
    public void setDescription(String description) {
        this.description = description;
    }

    // 获取期望价格
    public Double getPrice() {
        return price;
    }

    // 设置期望价格
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取原价
    public Double getOriginalPrice() {
        return originalPrice;
    }

    // 设置原价
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    // 获取图片 URL 字符串
    public String getImages() {
        return images;
    }

    // 设置图片 URL 字符串
    public void setImages(String images) {
        this.images = images;
    }

    // 获取新旧程度
    public String getConditionLevel() {
        return conditionLevel;
    }

    // 设置新旧程度
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    // 获取分类 ID
    public Long getCategoryId() {
        return categoryId;
    }

    // 设置分类 ID
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

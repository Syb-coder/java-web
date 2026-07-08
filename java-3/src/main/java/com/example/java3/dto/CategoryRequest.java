// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 NotNull 校验注解，确保对象非 null
import jakarta.validation.constraints.NotNull;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 分类请求 DTO
 * <p>
 * 管理员创建或编辑商品分类时提交的数据。
 * </p>
 */
public class CategoryRequest {

    // 分类名称字段，必填，前端展示与搜索依据
    /** 分类名称 */
    @NotBlank(message = "分类名称不能为空")
    // Size：限定分类名称最长 50 字符
    @Size(max = 50, message = "分类名称长度不能超过 50")
    private String name;

    // 图标字段，可选，存储图标 URL 或图标标识
    /** 图标 */
    private String icon;

    // 排序序号字段，控制前端列表展示顺序，数值越小越靠前
    /** 排序序号 */
    @NotNull(message = "排序序号不能为空")
    private Integer sortOrder;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取分类名称
    public String getName() {
        return name;
    }

    // 设置分类名称
    public void setName(String name) {
        this.name = name;
    }

    // 获取图标
    public String getIcon() {
        return icon;
    }

    // 设置图标
    public void setIcon(String icon) {
        this.icon = icon;
    }

    // 获取排序序号
    public Integer getSortOrder() {
        return sortOrder;
    }

    // 设置排序序号
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

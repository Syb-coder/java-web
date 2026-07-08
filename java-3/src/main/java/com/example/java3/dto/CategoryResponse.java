// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 ProductCategory 实体类，用于构造响应 DTO
import com.example.java3.model.ProductCategory;

/**
 * 分类响应 DTO
 * <p>
 * 用于商品分类列表查询，前端按 sortOrder 排序后展示。
 * </p>
 */
public class CategoryResponse {

    // 分类主键 ID
    private Long id;
    // 分类名称
    private String name;
    // 图标
    private String icon;
    // 排序序号
    private Integer sortOrder;

    /**
     * 由 ProductCategory 实体构造响应
     * <p>
     * 直接拷贝实体字段到 DTO，避免暴露实体 ORM 注解等内部细节。
     * </p>
     *
     * @param c 商品分类实体
     */
    public CategoryResponse(ProductCategory c) {
        this.id = c.getId();                // 赋值分类 ID
        this.name = c.getName();            // 赋值分类名称
        this.icon = c.getIcon();            // 赋值图标
        this.sortOrder = c.getSortOrder();  // 赋值排序序号
    }

    // 获取分类 ID
    public Long getId() {
        return id;
    }

    // 设置分类 ID
    public void setId(Long id) {
        this.id = id;
    }

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

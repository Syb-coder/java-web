// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Category;

/**
 * 分类响应 DTO
 * <p>
 * 前台分类导览与后台分类列表共用，附带该分类下文章数（由 Service 层填充）。
 * </p>
 *
 * @param id            分类 ID
 * @param name          分类名称
 * @param description   分类描述
 * @param sortOrder     排序序号
 * @param articleCount  该分类下已发布文章数（前台展示用，后台列表可为 null）
 * @param createTime    创建时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString，前台导览与后台列表共用
public record CategoryResponse(
        Long id,            // 分类主键 ID
        String name,        // 分类名称
        String description, // 分类描述，可为 null
        int sortOrder,      // 排序序号，前台按该字段升序展示分类
        Long articleCount,  // 该分类下已发布文章数：前台导览需展示，后台列表场景可为 null
        String createTime   // 创建时间字符串
) {
    /**
     * 由分类实体构造响应（不含文章数）
     *
     * @param category 分类实体
     * @return 分类响应
     */
    // 使用静态工厂方法 from() 而非构造方法：命名语义清晰，且集中处理 articleCount 缺省为 null 的逻辑
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSortOrder(),
                // 后台列表场景无需文章数，传 null
                null,
                category.getCreateTime() != null ? category.getCreateTime().toString() : null
        );
    }

    /**
     * 由分类实体构造响应（含文章数，用于前台导览）
     *
     * @param category     分类实体
     * @param articleCount 文章数
     * @return 分类响应
     */
    // 重载 from()：通过额外参数 articleCount 区分调用场景，前台导览需要展示分类下文章数量
    public static CategoryResponse from(Category category, long articleCount) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSortOrder(),
                articleCount,
                category.getCreateTime() != null ? category.getCreateTime().toString() : null
        );
    }
}

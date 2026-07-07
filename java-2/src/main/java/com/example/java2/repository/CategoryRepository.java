// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入集合与排序类
import java.util.List;

/**
 * 文章分类仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 通过 findAllByOrderBySortOrderAsc 派生查询实现分类按 sortOrder 升序展示。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<Category> 表示管理的实体类型；<Long> 表示主键类型（与 Category.id 字段一致）。
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 查询全部分类，按 sortOrder 升序排列
     *
     * @return 分类列表
     */
    // 方法名约定解析：findAll + By + OrderBy + 字段(SortOrder) + 关键字(Asc)
    //   → Spring Data 解析为 SELECT * FROM category ORDER BY sort_order ASC
    // 命名原因：分类列表展示需保持人工配置的展示顺序（如"前端"在前"运维"在后），不能按字母或 ID。
    // 返回 List 而非 Page：分类数据量小且需一次性渲染到导航栏，无需分页。
    List<Category> findAllByOrderBySortOrderAsc();

    /**
     * 判断分类名称是否已存在
     *
     * @param name 分类名称
     * @return true 已存在
     */
    // 方法名约定解析：existsBy + 字段名(Name) → Spring Data 解析为 SELECT COUNT(*) > 0 FROM category WHERE name = ?
    // 业务场景：新增分类前校验名称唯一，避免前端重复提交导致同名列。
    boolean existsByName(String name);
}

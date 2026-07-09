// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 图书分类数据访问层
 * <p>
 * 提供分类的 CRUD 与按名称查询、排序查询等能力。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    /**
     * 根据分类名称查找分类
     *
     * @param name 分类名称
     * @return 分类实体（可能为空）
     */
    Optional<BookCategory> findByName(String name);

    /**
     * 检查分类名称是否已存在
     *
     * @param name 分类名称
     * @return true 表示已存在
     */
    boolean existsByName(String name);

    /**
     * 按排序序号升序查询全部分类
     *
     * @return 排序后的分类列表
     */
    List<BookCategory> findAllByOrderBySortOrderAsc();
}

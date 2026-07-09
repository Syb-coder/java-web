// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图书数据访问层
 * <p>
 * 提供图书的 CRUD、关键字搜索、按分类分页查询、热门借阅统计等能力。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 按书名或作者关键字分页搜索图书（用户端检索使用）
     *
     * @param title    书名关键字
     * @param author   作者关键字
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Book> findByTitleContainingOrAuthorContaining(String title, String author, Pageable pageable);

    /**
     * 按分类 ID 分页查询图书
     *
     * @param categoryId 分类 ID
     * @param pageable   分页参数
     * @return 分页结果
     */
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 按分类 ID 和关键字组合搜索图书
     *
     * @param categoryId 分类 ID
     * @param keyword    书名或作者关键字
     * @param pageable   分页参数
     * @return 分页结果
     */
    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND (b.title LIKE %:keyword% OR b.author LIKE %:keyword%)")
    Page<Book> findByCategoryIdAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询最新入库的 N 本图书（主页展示使用）
     *
     * @param pageable 分页参数（取前 N 条）
     * @return 图书列表
     */
    Page<Book> findAllByOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 统计馆藏图书总数
     *
     * @return 图书总数
     */
    long count();

    /**
     * 统计可借图书数量（availableCopies > 0）
     *
     * @return 可借图书种类数
     */
    long countByAvailableCopiesGreaterThan(Integer min);
}

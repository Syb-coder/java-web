package com.example.java7.repository; // 声明包路径，归属 repository 仓储层

import com.example.java7.model.Book; // 引入 Book 实体类，作为 JpaRepository 的泛型参数
import com.example.java7.model.BookCategory; // 引入图书分类枚举，用于按分类查询
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JpaRepository 接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.Query; // 引入 @Query 注解，用于声明自定义 JPQL 查询
import org.springframework.data.repository.query.Param; // 引入 @Param 注解，用于绑定命名参数到查询语句
import org.springframework.stereotype.Repository; // 引入 @Repository 注解，声明为 Spring 仓储组件

import java.util.List; // 引入 List 集合，作为查询方法的返回类型

/**
 * 图书仓储接口
 * <p>
 * 职责：提供 Book 实体的 CRUD 与自定义查询能力。
 * 继承 JpaRepository 自动获得基础 CRUD 实现，Spring Data JPA 在运行时生成代理类。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>基于接口编程，Spring Data JPA 通过 JDK 动态代理生成实现类，无需手写 SQL</li>
 *   <li>方法名派生查询与 @Query 自定义查询结合，覆盖简单到复杂的查询场景</li>
 *   <li>聚合查询使用 COALESCE 保证空表场景返回 0，避免 NPE</li>
 * </ul>
 * </p>
 *
 * @author example
 * @see JpaRepository
 */
@Repository // 声明为 Spring 仓储组件，启用持久层异常转换（将原生 JPA 异常转换为 DataAccessException 体系）
public interface BookRepository extends JpaRepository<Book, Long> { // 继承 JpaRepository，指定实体类型为 Book、主键类型为 Long

    /**
     * 按书名或作者模糊查询（忽略大小写）
     * <p>
     * 使用 @Query 自定义 JPQL，通过 OR 连接书名与作者模糊匹配，
     * LOWER 函数保证大小写不敏感，便于用户检索。
     * </p>
     * <p>
     * JPQL 解析：
     * <ul>
     *   <li>SELECT b FROM Book b：查询 Book 实体，别名 b</li>
     *   <li>WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))：书名包含关键字（忽略大小写）</li>
     *   <li>OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))：或作者包含关键字（忽略大小写）</li>
     *   <li>CONCAT 拼接 % 实现前缀后缀通配，LOWER 保证大小写不敏感</li>
     * </ul>
     * </p>
     *
     * @param keyword 小写后的关键字
     * @return 匹配的图书列表，无匹配时返回空 List
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " + // JPQL 查询语句：SELECT 实体 b，WHERE 条件为书名模糊匹配
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))") // OR 连接作者模糊匹配，与书名条件取并集
    List<Book> searchByKeyword(@Param("keyword") String keyword); // 方法签名：@Param 将入参绑定到 JPQL 中的 :keyword 命名参数

    /**
     * 按分类查询图书
     * <p>
     * Spring Data JPA 方法名派生查询：根据方法名 findByCategory 自动生成
     * WHERE b.category = ?1 查询条件，无需手写 JPQL。
     * </p>
     *
     * @param category 图书分类
     * @return 该分类下所有图书
     */
    List<Book> findByCategory(BookCategory category); // 派生查询方法：按 category 字段等值匹配，返回匹配的图书列表

    /**
     * 统计图书总数
     * <p>
     * 覆盖 JpaRepository 默认 count 方法，返回图书种类数量（非副本数）。
     * 实际为继承方法的 Javadoc 补充说明。
     * </p>
     *
     * @return 图书种类数量
     */
    long count(); // 统计 Book 表记录总数，返回 long 类型（继承自 JpaRepository，此处仅补充文档）

    /**
     * 聚合查询：所有图书总副本数
     * <p>
     * 使用 SUM 聚合函数累加 totalCopies 字段，COALESCE 处理空结果集
     * 返回 0，保证无图书时不会返回 null。
     * </p>
     *
     * @return 总副本数，无图书时返回 0
     */
    @Query("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b") // JPQL：SUM 聚合总副本数，COALESCE 保证空表返回 0
    long sumTotalCopies(); // 自定义聚合查询方法，返回所有图书的总副本数

    /**
     * 聚合查询：所有图书可借副本数
     * <p>
     * 使用 SUM 聚合函数累加 availableCopies 字段，COALESCE 处理空结果集
     * 返回 0，保证无图书时不会返回 null。
     * </p>
     *
     * @return 可借副本数，无图书时返回 0
     */
    @Query("SELECT COALESCE(SUM(b.availableCopies), 0) FROM Book b") // JPQL：SUM 聚合可借副本数，COALESCE 保证空表返回 0
    long sumAvailableCopies(); // 自定义聚合查询方法，返回所有图书的可借副本总数
}

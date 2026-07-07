// 声明包路径，归类为 repository 仓储层，存放 JPA 仓储接口
package com.example.java1.repository;

// 导入图书实体类，对应数据库 books 表
import com.example.java1.model.Book;
// 导入 Spring Data JPA 仓储接口，继承后自动获得标准 CRUD 实现
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Query 注解，用于声明自定义 JPQL 查询，覆盖方法名约定无法表达的场景
import org.springframework.data.jpa.repository.Query;
// 导入 @Repository 注解，标记为持久层组件
import org.springframework.stereotype.Repository;

// 导入 List 集合接口，用于返回多结果查询
import java.util.List;

/**
 * 图书仓储
 * <p>
 * 提供图书实体的持久化访问能力，包含按书名/作者/分类检索及库存聚合查询。
 * </p>
 */
@Repository // 声明为 Spring 仓储组件，由 IoC 容器管理为单例 Bean
public interface BookRepository extends JpaRepository<Book, Long> { // 泛型参数：实体类型为 Book，主键类型为 Long

    /**
     * 按书名或作者模糊检索（忽略大小写）
     * <p>使用 @Query 自定义 JPQL，通过 OR 条件同时匹配书名与作者。</p>
     *
     * @param keyword 关键字
     * @return 匹配的图书列表
     */
    // 方法名难以表达 OR + 忽略大小写 + LIKE 的复合条件，故用 @Query 显式声明 JPQL
    // LOWER + CONCAT('%', :keyword, '%') 实现 SQL 层忽略大小写的模糊匹配，避免 Java 层遍历过滤
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByKeyword(String keyword); // :keyword 参数自动绑定方法入参，返回匹配的图书列表

    /**
     * 按分类查询图书
     *
     * @param category 分类
     * @return 图书列表
     */
    List<Book> findByCategory(String category); // 方法名约定：findBy + Category，Spring Data 生成 WHERE category = ? 查询，用于分类筛选页面

    /**
     * 聚合查询：所有图书的总副本数之和
     * <p>避免加载全部实体再求和，直接由数据库聚合计算。</p>
     *
     * @return 副本总数
     */
    // COALESCE 避免 SUM 在空表上返回 null 导致 NPE，空表时降级返回 0
    @Query("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b")
    long sumTotalCopies(); // 聚合查询直接在数据库层求和，避免加载全部实体到内存再遍历求和的性能开销

    /**
     * 聚合查询：所有图书的可借副本数之和
     *
     * @return 可借副本总数
     */
    @Query("SELECT COALESCE(SUM(b.availableCopies), 0) FROM Book b")
    long sumAvailableCopies(); // 与 sumTotalCopies 同理，首页统计看板展示馆藏可借总量
}

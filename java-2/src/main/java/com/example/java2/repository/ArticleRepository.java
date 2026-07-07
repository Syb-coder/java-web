// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 导入集合类
import java.util.List;

/**
 * 文章仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按分类查询、关键词搜索、阅读量统计等派生与自定义查询。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<Article> 表示管理的实体类型；<Long> 表示主键类型（与 Article.id 字段一致）。
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 按分类查询已发布文章，分页返回
     *
     * @param categoryId 分类 ID
     * @param pageable   分页参数
     * @return 文章分页
     */
    // 方法名约定解析：findBy + 字段(CategoryId) + And + 字段(Published) + 关键字(True)
    //   → Spring Data 解析为 SELECT * FROM article WHERE category_id = ? AND published = true
    // 业务场景：前台分类列表页展示该分类下已发布文章，未发布(draft)文章不暴露给访客。
    // 返回 Page：文章量大必须分页，Page 同时携带当前页数据与总条数/总页数元数据。
    Page<Article> findByCategoryIdAndPublishedTrue(Long categoryId, Pageable pageable);

    /**
     * 查询全部已发布文章，分页返回
     *
     * @param pageable 分页参数
     * @return 文章分页
     */
    // 方法名约定解析：findBy + 字段(Published) + 关键字(True)
    //   → Spring Data 解析为 SELECT * FROM article WHERE published = true
    // 业务场景：前台首页文章流、最新文章 RSS 等场景。
    Page<Article> findByPublishedTrue(Pageable pageable);

    /**
     * 关键词搜索（标题或摘要包含关键词），仅返回已发布文章
     * <p>使用 LIKE 模糊匹配，关键词需前后加 %。</p>
     *
     * @param keyword  关键词
     * @param pageable 分页参数
     * @return 文章分页
     */
    // @Query 注解：当方法名约定无法表达 OR 嵌套条件（如 title OR summary 同时匹配）时，手写 JPQL。
    // JPQL vs SQL：JPQL 面向实体(Article a) 而非表名，由 Hibernate 翻译为对应方言 SQL，
    //   优点是可跨数据库方言、字段名受实体类 refactor 保护。
    // %:keyword% 中的 % 直接写在 JPQL 字符串中，Spring Data 会将 :keyword 绑定到 % 中间，
    //   等价于 SQL: title LIKE '%keyword%'，避免在 Java 代码里手动拼 %。
    // @Param("keyword")：将 Java 参数 keyword 绑定到 JPQL 中的 :keyword 占位符，
    //   使用命名参数而非位置参数(?1)便于维护、可读性强、参数顺序可调。
    @Query("SELECT a FROM Article a WHERE a.published = true " +
            "AND (a.title LIKE %:keyword% OR a.summary LIKE %:keyword%)")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询指定分类下的文章数量（含未发布，用于后台删除分类前的占用检查）
     *
     * @param categoryId 分类 ID
     * @return 文章数量
     */
    // 方法名约定解析：countBy + 字段名(CategoryId) → Spring Data 解析为 SELECT COUNT(*) FROM article WHERE category_id = ?
    // 业务场景：管理端删除分类前判断是否还有文章引用，避免孤儿文章。
    long countByCategoryId(Long categoryId);

    /**
     * 查询已发布文章总数（用于仪表板统计）
     *
     * @return 已发布文章数量
     */
    // 方法名约定解析：countBy + 字段(Published) + 关键字(True)
    //   → Spring Data 解析为 SELECT COUNT(*) FROM article WHERE published = true
    // 业务场景：后台仪表板展示"已发布文章数"指标卡。
    long countByPublishedTrue();

    /**
     * 查询全平台阅读量总和（用于仪表板统计）
     */
    // @Query：SUM 聚合函数无法用方法名约定表达，需手写 JPQL。
    // COALESCE(SUM(x), 0)：当表为空或 SUM 结果为 NULL 时返回 0，避免返回 null 强转 long 时 NPE。
    //   COALESCE 是 SQL 标准函数，所有数据库方言均支持。
    // 返回 long 而非 Long：单值聚合且 COALESCE 保证非 null，用基础类型避免拆箱开销。
    @Query("SELECT COALESCE(SUM(a.viewCount), 0) FROM Article a")
    long sumViewCount();

    /**
     * 查询最近发布的 N 篇文章（用于前台首页推荐）
     *
     * @param pageable 分页参数，限制条数
     * @return 文章列表
     */
    // 方法名约定解析：findTopN + By + 字段(Published) + 关键字(True) + OrderBy + 字段(PublishTime) + 关键字(Desc)
    //   → Spring Data 解析为 SELECT * FROM article WHERE published = true ORDER BY publish_time DESC LIMIT N
    // 命名 TopN 是约定关键字，但实际条数由 Pageable 的 size 参数决定（如 PageRequest.of(0, 5)）。
    // 返回 List 而非 Page：首页推荐场景已知条数小，无需分页元数据，List 更轻量。
    List<Article> findTopNByPublishedTrueOrderByPublishTimeDesc(Pageable pageable);
}

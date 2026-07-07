// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章评论仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按文章查询评论、按用户查询评论等派生查询。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<Comment> 表示管理的实体类型；<Long> 表示主键类型（与 Comment.id 字段一致）。
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按文章查询评论，按时间倒序分页返回
     *
     * @param articleId 文章 ID
     * @param pageable  分页参数
     * @return 评论分页
     */
    // 方法名约定解析：findBy + 字段(ArticleId) + OrderBy + 字段(CreateTime) + 关键字(Desc)
    //   → Spring Data 解析为 SELECT * FROM comment WHERE article_id = ? ORDER BY create_time DESC
    // 业务场景：文章详情页加载评论列表，最新评论优先展示。
    // 返回 Page：热门文章评论可能上百条，必须分页避免一次加载过多数据。
    Page<Comment> findByArticleIdOrderByCreateTimeDesc(Long articleId, Pageable pageable);

    /**
     * 查询某文章的评论总数
     *
     * @param articleId 文章 ID
     * @return 评论数
     */
    // 方法名约定解析：countBy + 字段名(ArticleId)
    //   → Spring Data 解析为 SELECT COUNT(*) FROM comment WHERE article_id = ?
    // 业务场景：文章列表展示"评论 N 条"徽标，无需加载评论明细。
    long countByArticleId(Long articleId);

    /**
     * 查询全部评论，按时间倒序分页返回（用于后台评论审核）
     *
     * @param pageable 分页参数
     * @return 评论分页
     */
    // 方法名约定解析：findAll + By + OrderBy + 字段(CreateTime) + 关键字(Desc)
    //   → Spring Data 解析为 SELECT * FROM comment ORDER BY create_time DESC
    // 命名原因：findAllByOrderByXxx 是约定的"查全部 + 排序"写法，By 后无字段表示无条件。
    // 业务场景：后台审核评论列表，管理员可看到所有文章的评论（含未审核）。
    Page<Comment> findAllByOrderByCreateTimeDesc(Pageable pageable);
}

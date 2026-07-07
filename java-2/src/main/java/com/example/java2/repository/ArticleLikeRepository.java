// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional 类
import java.util.Optional;

/**
 * 文章点赞仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按用户+文章查询点赞是否存在、删除点赞等派生查询。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<ArticleLike> 表示管理的实体类型；<Long> 表示主键类型（与 ArticleLike.id 字段一致）。
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    /**
     * 查询某用户是否已点赞某文章
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return 点赞记录（可选）
     */
    // 方法名约定解析：findBy + 字段(UserId) + And + 字段(ArticleId)
    //   → Spring Data 解析为 SELECT * FROM article_like WHERE user_id = ? AND article_id = ?
    // 命名原因：(user_id, article_id) 是点赞表的唯一键（一人一篇只能赞一次），组合条件命中唯一索引。
    // 业务场景：用户点赞前先校验是否已赞过；文章详情页展示"已点赞"状态。
    // 返回 Optional：未点赞时返回 empty，调用方据此决定是新增还是提示已赞。
    Optional<ArticleLike> findByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 删除某用户对某文章的点赞（取消点赞）
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     */
    // 方法名约定解析：deleteBy + 字段(UserId) + And + 字段(ArticleId)
    //   → Spring Data 解析为 DELETE FROM article_like WHERE user_id = ? AND article_id = ?
    // 业务场景：用户点击取消点赞按钮。
    // 注意：派生 deleteBy 默认先 SELECT 再 DELETE，取消点赞为单条操作，性能可接受。
    void deleteByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 查询某文章的点赞总数（用于校验冗余计数一致性）
     *
     * @param articleId 文章 ID
     * @return 点赞数
     */
    // 方法名约定解析：countBy + 字段名(ArticleId)
    //   → Spring Data 解析为 SELECT COUNT(*) FROM article_like WHERE article_id = ?
    // 业务场景：文章表通常冗余存储 like_count 字段以避免每次查询都 COUNT，
    //   该方法用于定时校验冗余计数与明细表是否一致（数据修复任务）。
    long countByArticleId(Long articleId);
}

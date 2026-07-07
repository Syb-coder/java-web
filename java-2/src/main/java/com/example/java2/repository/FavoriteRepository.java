// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入集合与 Optional 类
import java.util.List;
import java.util.Optional;

/**
 * 用户收藏仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按用户查询收藏、按用户+文章查询收藏是否存在等派生查询。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<Favorite> 表示管理的实体类型；<Long> 表示主键类型（与 Favorite.id 字段一致）。
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 查询某用户是否已收藏某文章
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return 收藏记录（可选）
     */
    // 方法名约定解析：findBy + 字段(UserId) + And + 字段(ArticleId)
    //   → Spring Data 解析为 SELECT * FROM favorite WHERE user_id = ? AND article_id = ?
    // 命名原因：(user_id, article_id) 是收藏表的唯一键，组合条件命中索引且最多返回一行。
    // 业务场景：用户点收藏前判断是否已收藏过，避免重复插入；详情页显示"已收藏"高亮。
    // 返回 Optional：用户未收藏时返回 empty，调用方判空即可决定是否插入新记录。
    Optional<Favorite> findByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 查询某用户收藏的全部文章 ID 列表（按收藏时间倒序）
     *
     * @param userId 用户 ID
     * @return 收藏记录列表
     */
    // 方法名约定解析：findBy + 字段(UserId) + OrderBy + 字段(CreateTime) + 关键字(Desc)
    //   → Spring Data 解析为 SELECT * FROM favorite WHERE user_id = ? ORDER BY create_time DESC
    // 业务场景：个人中心"我的收藏"列表，最新收藏排在最前。
    // 返回 List：当前实现未分页（数据量可控），如后续数据量增长可改为 Page + Pageable。
    List<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 查询某用户收藏总数（用于个人中心统计）
     *
     * @param userId 用户 ID
     * @return 收藏数
     */
    // 方法名约定解析：countBy + 字段名(UserId) → Spring Data 解析为 SELECT COUNT(*) FROM favorite WHERE user_id = ?
    // 业务场景：个人中心首页展示"收藏 N 篇"统计指标。
    long countByUserId(Long userId);

    /**
     * 删除某用户对某文章的收藏（取消收藏）
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     */
    // 方法名约定解析：deleteBy + 字段(UserId) + And + 字段(ArticleId)
    //   → Spring Data 解析为 DELETE FROM favorite WHERE user_id = ? AND article_id = ?
    // 返回 void：删除操作无需返回值，删除条数若需校验可改用 long 返回删除行数。
    // 注意：派生 deleteBy 默认会先 SELECT 再逐条 DELETE，大批量删除应改用 @Modifying @Query 批量删除。
    void deleteByUserIdAndArticleId(Long userId, Long articleId);
}

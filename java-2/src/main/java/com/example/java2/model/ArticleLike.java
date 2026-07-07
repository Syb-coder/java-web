// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 文章点赞实体
 * <p>
 * 记录用户对文章的点赞行为。一个用户对一篇文章只能点赞一次，
 * 故使用 (userId, articleId) 联合唯一约束防止重复点赞。
 * </p>
 * <p>
 * 实体关系：作为 User 与 Article 之间的多对多中间表，承载点赞关系。
 * 与 Favorite 表结构对称，区别仅在表名与业务含义（点赞 vs 收藏）。
 * 删除时需同步扣减 Article.likeCount 冗余计数。
 * </p>
 */
@Entity  // 标识为 JPA 实体
// uniqueConstraints 在 (userId, articleId) 上建立联合唯一索引：
// 防止同一用户对同一文章重复点赞（业务侧防刷），同时加速按文章维度统计点赞列表
@Table(name = "article_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "articleId"}))
public class ArticleLike {

    /** 主键 ID，自增 */
    // 主键仍保留：便于单条点赞记录的取消操作，避免依赖联合键进行删除
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 点赞用户 ID */
    // nullable=false：点赞记录必须有归属用户，否则无法统计用户点赞历史
    @Column(nullable = false)
    private Long userId;

    /** 被点赞文章 ID */
    // nullable=false：点赞记录必须指向具体文章，配合 userId 形成联合唯一约束防重复点赞
    @Column(nullable = false)
    private Long articleId;

    /** 点赞时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public ArticleLike() {
    }

    /**
     * 全参构造方法
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     */
    public ArticleLike(Long userId, Long articleId) {
        this.userId = userId;
        this.articleId = articleId;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

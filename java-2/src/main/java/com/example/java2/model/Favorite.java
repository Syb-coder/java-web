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
 * 用户收藏实体
 * <p>
 * 记录用户对文章的收藏行为。一个用户对一篇文章只能收藏一次，
 * 故使用 (userId, articleId) 联合唯一约束防止重复收藏。
 * </p>
 * <p>
 * 实体关系：作为 User 与 Article 之间的多对多中间表，承载收藏关系。
 * 删除时需同步扣减 Article.favoriteCount 冗余计数，避免列表显示与实际脱节。
 * </p>
 */
@Entity  // 标识为 JPA 实体
// uniqueConstraints 在 (userId, articleId) 上建立联合唯一索引：
// 既防止同一用户对同一文章重复收藏，又加速按用户维度查询收藏列表的查询性能
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "articleId"}))
public class Favorite {

    /** 主键 ID，自增 */
    // 主键仍保留：便于后台按记录维度管理与单条删除，避免仅靠联合键操作带来的复杂查询
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 收藏用户 ID */
    // nullable=false：收藏记录必须有归属用户，否则数据无意义且无法关联用户中心
    @Column(nullable = false)
    private Long userId;

    /** 被收藏文章 ID */
    // nullable=false：收藏记录必须指向具体文章，配合 userId 形成联合唯一约束防重复收藏
    @Column(nullable = false)
    private Long articleId;

    /** 收藏时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public Favorite() {
    }

    /**
     * 全参构造方法
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     */
    public Favorite(Long userId, Long articleId) {
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

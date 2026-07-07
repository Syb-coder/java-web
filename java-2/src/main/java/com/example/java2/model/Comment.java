// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 文章评论实体
 * <p>
 * 用户在文章详情页发表的评论。管理员可在后台删除违规评论。
 * 评论内容较短，限制 500 字符。
 * </p>
 * <p>
 * 实体关系：与 Article 为多对一（通过 articleId 关联）；与 User 为多对一（通过 userId 关联）。
 * 评论与点赞/收藏不同，不存在唯一约束——同一用户可对同一文章多次评论。
 * </p>
 */
@Entity  // 标识为 JPA 实体
@Table(name = "comment")
public class Comment {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，便于后台单条删除与分页查询
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 评论所属文章 ID */
    // nullable=false：评论必须挂在文章下，避免孤儿评论无法定位展示位置
    @Column(nullable = false)
    private Long articleId;

    /** 评论发表用户 ID */
    // nullable=false：评论必须有发表者，便于追责与管理员审核违规
    @Column(nullable = false)
    private Long userId;

    /** 评论内容 */
    // length=500：评论为短文本，500 字符限制防刷屏并控制存储，与 Article.summary 对齐
    @Column(nullable = false, length = 500)
    private String content;

    /** 评论发表时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public Comment() {
    }

    /**
     * 全参构造方法
     *
     * @param articleId 文章 ID
     * @param userId    用户 ID
     * @param content   评论内容
     */
    public Comment(Long articleId, Long userId, String content) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    /**
     * 获取评论内容。
     * 业务约束：返回值为用户原文，前端展示前需做 XSS 过滤，避免存储型 XSS 攻击。
     */
    public String getContent() { return content; }

    /**
     * 设置评论内容。
     * 业务约束：调用方应在写入前完成长度校验（≤500）与敏感词过滤；
     * 不在此处做 HTML 转义以保留原始文本便于后续编辑。
     */
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

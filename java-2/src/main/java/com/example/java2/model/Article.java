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
 * 安全学习文章实体
 * <p>
 * 平台核心学习资源。每篇文章归属于一个分类，包含标题、摘要、正文、阅读/点赞/收藏计数。
 * 正文可能较长，故使用 length=20000 容纳大段科普内容。
 * </p>
 * <p>
 * 实体关系：与 Category 为多对一（仅持有 categoryId 而非对象引用，避免懒加载与 N+1 问题）；
 * 与 User 通过 Favorite / ArticleLike / Comment 形成多对多与一对多关系；
 * 计数字段（viewCount/likeCount/favoriteCount）为冗余字段，与明细表保持最终一致。
 * </p>
 */
@Entity  // 标识为 JPA 实体
@Table(name = "article")
public class Article {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，便于 Favorite/ArticleLike/Comment 等子表外键引用
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属分类 ID */
    // nullable=false：文章必须归属分类，避免孤儿文章无法在前台导航中被检索
    @Column(nullable = false)
    private Long categoryId;

    /** 文章标题 */
    // length=200：标题需容纳完整描述，200 字符覆盖中长标题，超出则编辑器截断
    @Column(nullable = false, length = 200)
    private String title;

    /** 文章摘要，用于列表展示 */
    // length=500：摘要比标题长，500 字符够列表预览展示，避免详情页正文预加载
    @Column(length = 500)
    private String summary;

    /** 文章正文（Markdown 文本），使用大字段长度 */
    // length=20000：科普文章正文较长，VARCHAR 默认 255 不够用；不用 TEXT 是为保留长度约束防滥用
    @Column(nullable = false, length = 20000)
    private String content;

    /** 阅读量，前台每次访问详情自增 */
    // nullable=false：计数器必须有初值 0，避免 null 参与运算抛 NPE
    @Column(nullable = false)
    private int viewCount = 0;

    /** 点赞数，与 {@link ArticleLike} 表保持冗余计数 */
    // nullable=false：冗余计数避免每次列表查询 JOIN 点赞表，用空间换查询性能
    @Column(nullable = false)
    private int likeCount = 0;

    /** 收藏数，与 {@link Favorite} 表保持冗余计数 */
    // nullable=false：同 likeCount，冗余计数优化列表查询性能，避免 COUNT 聚合
    @Column(nullable = false)
    private int favoriteCount = 0;

    /** 发布状态：true 已发布（前台可见），false 草稿（仅后台可见） */
    // nullable=false：发布状态必须明确，避免草稿因 null 误判为已发布泄露给前台
    @Column(nullable = false)
    private boolean published = true;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public Article() {
    }

    /**
     * 全参构造方法（核心字段）
     *
     * @param categoryId 所属分类 ID
     * @param title      标题
     * @param summary    摘要
     * @param content    正文
     */
    public Article(Long categoryId, String title, String summary, String content) {
        this.categoryId = categoryId;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.published = true;
        this.publishTime = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    /**
     * 获取点赞数。
     * 业务约束：返回值为冗余计数，与 ArticleLike 表行数应保持一致；
     * 出现不一致时以 ArticleLike 表为准并触发计数修正。
     */
    public int getLikeCount() { return likeCount; }

    /**
     * 设置点赞数。
     * 业务约束：调用方需在事务内同步更新 ArticleLike 表，避免计数与明细脱节。
     */
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    /**
     * 获取收藏数。
     * 业务约束：同 likeCount，与 Favorite 表行数保持最终一致。
     */
    public int getFavoriteCount() { return favoriteCount; }

    /**
     * 设置收藏数。
     * 业务约束：调用方需在事务内同步更新 Favorite 表。
     */
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    /**
     * 判断文章是否已发布。
     * 业务约束：false 表示草稿，前台列表与详情接口必须过滤此状态，避免未审核内容外泄。
     */
    public boolean isPublished() { return published; }

    /**
     * 设置发布状态。
     * 业务约束：从草稿切为发布时需同步写入 publishTime；从发布切回草稿不影响已存在 publishTime。
     */
    public void setPublished(boolean published) { this.published = published; }

    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

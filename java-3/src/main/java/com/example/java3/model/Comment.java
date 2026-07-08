// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录评论时间
import java.time.LocalDateTime;

/**
 * 商品留言评论实体
 * <p>
 * 买家可在商品详情页留言提问，卖家或其他用户可回复。
 * 支持 parentId 实现二级回复结构（顶层评论 parentId=null）。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 comments，承载商品留言与回复
@Table(name = "comments")
public class Comment {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联商品 ID */
    // 非空；评论所属商品的 ID，关联 Product 表主键
    @Column(nullable = false)
    private Long productId;

    /** 评论者用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long userId;

    /** 评论内容 */
    // 非空，长度上限 500；限定评论长度防止刷屏
    @Column(nullable = false, length = 500)
    private String content;

    /** 父评论 ID（顶层评论为 null） */
    // 可空；为 null 表示顶层评论，非 null 表示对某条评论的回复，从而实现二级回复结构
    private Long parentId;

    /** 评论时间 */
    // 非空，评论时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Comment() {
    }

    /**
     * 业务构造方法
     *
     * @param productId 商品 ID
     * @param userId    评论者 ID
     * @param content   评论内容
     * @param parentId  父评论 ID（可为 null）
     */
    public Comment(Long productId, Long userId, String content, Long parentId) {
        // 设置关联商品 ID
        this.productId = productId;
        // 设置评论者用户 ID
        this.userId = userId;
        // 设置评论内容
        this.content = content;
        // 设置父评论 ID（顶层评论传 null）
        this.parentId = parentId;
        // 评论时间取当前系统时间
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    // 获取主键 ID
    public Long getId() {
        return id;
    }

    // 设置主键 ID，通常由 JPA 自动填充
    public void setId(Long id) {
        this.id = id;
    }

    // 获取关联商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置关联商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取评论者用户 ID
    public Long getUserId() {
        return userId;
    }

    // 设置评论者用户 ID
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 获取评论内容
    public String getContent() {
        return content;
    }

    // 设置评论内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取父评论 ID（顶层评论返回 null）
    public Long getParentId() {
        return parentId;
    }

    // 设置父评论 ID
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    // 获取评论时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置评论时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

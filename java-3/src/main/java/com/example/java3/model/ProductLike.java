// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录点赞时间
import java.time.LocalDateTime;

/**
 * 商品点赞实体
 * <p>
 * 学生用户对商品点赞，用于热门商品排序参考。
 * (userId, productId) 组合唯一，每人只能点赞一次。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 product_likes，并声明联合唯一约束：同一用户对同一商品只能点赞一次
@Table(name = "product_likes", uniqueConstraints = {
        // 联合唯一约束：userId 与 productId 组合唯一，从数据库层面防止重复点赞
        @UniqueConstraint(columnNames = {"userId", "productId"})
})
public class ProductLike {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 点赞用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long userId;

    /** 商品 ID */
    // 非空；关联 Product 表主键
    @Column(nullable = false)
    private Long productId;

    /** 点赞时间 */
    // 非空，点赞时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public ProductLike() {
    }

    /**
     * 业务构造方法
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    public ProductLike(Long userId, Long productId) {
        // 设置点赞用户 ID
        this.userId = userId;
        // 设置被点赞商品 ID
        this.productId = productId;
        // 点赞时间取当前系统时间
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

    // 获取点赞用户 ID
    public Long getUserId() {
        return userId;
    }

    // 设置点赞用户 ID
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 获取商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取点赞时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置点赞时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录评价时间
import java.time.LocalDateTime;

/**
 * 交易评价实体
 * <p>
 * 订单完成后买家可对卖家评价，构建校园交易信任体系。
 * (orderId) 唯一约束，一单只能评价一次。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 reviews，并声明唯一约束：一个订单只能被评价一次
@Table(name = "reviews", uniqueConstraints = {
        // orderId 唯一约束：从数据库层面保证一单只能评价一次
        @UniqueConstraint(columnNames = {"orderId"})
})
public class Review {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联订单 ID */
    // 非空且唯一；唯一约束保证一单只能评价一次
    @Column(nullable = false, unique = true)
    private Long orderId;

    /** 评价者（买家）用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long reviewerId;

    /** 被评价者（卖家）用户 ID */
    // 非空；冗余存储卖家 ID，便于按被评价者聚合查询信用分
    @Column(nullable = false)
    private Long revieweeId;

    /** 评分 1-5 */
    // 非空；业务层应校验取值范围 1-5，用于卖家信用分计算
    @Column(nullable = false)
    private Integer rating;

    /** 评价内容 */
    // 可空，长度上限 500；买家可只打分不写文字
    @Column(length = 500)
    private String content;

    /** 评价时间 */
    // 非空，评价时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Review() {
    }

    /**
     * 业务构造方法
     *
     * @param orderId    订单 ID
     * @param reviewerId 评价者 ID
     * @param revieweeId 被评价者 ID
     * @param rating     评分
     * @param content    评价内容
     */
    public Review(Long orderId, Long reviewerId, Long revieweeId, Integer rating, String content) {
        // 设置关联订单 ID
        this.orderId = orderId;
        // 设置评价者（买家）用户 ID
        this.reviewerId = reviewerId;
        // 设置被评价者（卖家）用户 ID
        this.revieweeId = revieweeId;
        // 设置评分
        this.rating = rating;
        // 设置评价内容
        this.content = content;
        // 评价时间取当前系统时间
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

    // 获取关联订单 ID
    public Long getOrderId() {
        return orderId;
    }

    // 设置关联订单 ID
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 获取评价者（买家）用户 ID
    public Long getReviewerId() {
        return reviewerId;
    }

    // 设置评价者（买家）用户 ID
    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    // 获取被评价者（卖家）用户 ID
    public Long getRevieweeId() {
        return revieweeId;
    }

    // 设置被评价者（卖家）用户 ID
    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    // 获取评分（1-5）
    public Integer getRating() {
        return rating;
    }

    // 设置评分
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    // 获取评价内容
    public String getContent() {
        return content;
    }

    // 设置评价内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取评价时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置评价时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

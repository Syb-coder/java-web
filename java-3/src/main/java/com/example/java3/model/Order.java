// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录下单与完成时间
import java.time.LocalDateTime;

/**
 * 订单实体
 * <p>
 * 买家对商品下单后生成订单，状态流转：
 * PENDING -> PAID -> COMPLETED，或任意阶段 CANCELLED。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 orders（避开 SQL 关键字 order，加复数后缀）
@Table(name = "orders")
public class Order {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 订单号（业务唯一） */
    // 非空且唯一，长度上限 32；业务层生成的订单号，便于对外暴露而不暴露自增主键
    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;

    /** 商品 ID */
    // 非空；关联 Product 表主键
    @Column(nullable = false)
    private Long productId;

    /** 买家用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long buyerId;

    /** 卖家用户 ID */
    // 非空；冗余存储卖家 ID，便于卖家查询自己收到的订单
    @Column(nullable = false)
    private Long sellerId;

    /** 成交价格（下单时锁定） */
    // 非空；下单时锁定价格，避免后续商品价格调整影响历史订单
    @Column(nullable = false)
    private Double price;

    /** 订单状态 */
    // 声明枚举以字符串形式持久化，便于运维直接读库排查状态
    @Enumerated(EnumType.STRING)
    // 非空，长度上限 20
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /** 买家留言 */
    // 可空，长度上限 500；买家下单时的备注信息
    @Column(length = 500)
    private String buyerRemark;

    /** 纠纷处理备注（管理员介入时填写） */
    // 可空，长度上限 500；管理员处理纠纷时记录处理过程与结论
    @Column(length = 500)
    private String disputeRemark;

    /** 下单时间 */
    // 非空，下单时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 完成时间 */
    // 可空；订单状态流转到 COMPLETED 时才填充
    private LocalDateTime completedAt;

    /** 最后修改时间，数据更新前由 @PreUpdate 自动填充 */
    private LocalDateTime updateTime;

    /** 每次更新前自动填充修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Order() {
    }

    /**
     * 业务构造方法：下单时使用
     *
     * @param orderNo    订单号
     * @param productId  商品 ID
     * @param buyerId    买家 ID
     * @param sellerId   卖家 ID
     * @param price      成交价格
     * @param buyerRemark 买家留言
     */
    public Order(String orderNo, Long productId, Long buyerId, Long sellerId,
                 Double price, String buyerRemark) {
        // 设置订单号
        this.orderNo = orderNo;
        // 设置商品 ID
        this.productId = productId;
        // 设置买家用户 ID
        this.buyerId = buyerId;
        // 设置卖家用户 ID
        this.sellerId = sellerId;
        // 设置成交价格（下单时锁定）
        this.price = price;
        // 设置买家留言
        this.buyerRemark = buyerRemark;
        // 默认订单状态为待付款：买家刚下单尚未付款
        this.status = OrderStatus.PENDING;
        // 下单时间取当前系统时间
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

    // 获取订单号，用于对外展示与查询
    public String getOrderNo() {
        return orderNo;
    }

    // 设置订单号
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // 获取商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取买家用户 ID
    public Long getBuyerId() {
        return buyerId;
    }

    // 设置买家用户 ID
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    // 获取卖家用户 ID
    public Long getSellerId() {
        return sellerId;
    }

    // 设置卖家用户 ID
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    // 获取成交价格
    public Double getPrice() {
        return price;
    }

    // 设置成交价格
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取订单状态，用于状态机判断与流转控制
    public OrderStatus getStatus() {
        return status;
    }

    // 设置订单状态（业务层按状态机规则流转）
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // 获取买家留言
    public String getBuyerRemark() {
        return buyerRemark;
    }

    // 设置买家留言
    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    // 获取纠纷处理备注
    public String getDisputeRemark() {
        return disputeRemark;
    }

    // 设置纠纷处理备注（管理员介入时填写）
    public void setDisputeRemark(String disputeRemark) {
        this.disputeRemark = disputeRemark;
    }

    // 获取下单时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置下单时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 获取完成时间（订单未完成时返回 null）
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // 设置完成时间（订单流转到 COMPLETED 时调用）
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

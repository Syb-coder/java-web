package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 订单实体
 * <p>
 * 买家对商品下单后生成订单，状态流转：
 * PENDING -> PAID -> COMPLETED，或任意阶段 CANCELLED。
 * </p>
 */
@Entity
@Table(name = "orders")
public class Order {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 订单号（业务唯一） */
    @Column(nullable = false, unique = true, length = 32)
    private String orderNo;

    /** 商品 ID */
    @Column(nullable = false)
    private Long productId;

    /** 买家用户 ID */
    @Column(nullable = false)
    private Long buyerId;

    /** 卖家用户 ID */
    @Column(nullable = false)
    private Long sellerId;

    /** 成交价格（下单时锁定） */
    @Column(nullable = false)
    private Double price;

    /** 订单状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /** 买家留言 */
    @Column(length = 500)
    private String buyerRemark;

    /** 纠纷处理备注（管理员介入时填写） */
    @Column(length = 500)
    private String disputeRemark;

    /** 下单时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 完成时间 */
    private LocalDateTime completedAt;

    /** 默认构造方法 */
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
        this.orderNo = orderNo;
        this.productId = productId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.buyerRemark = buyerRemark;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getDisputeRemark() {
        return disputeRemark;
    }

    public void setDisputeRemark(String disputeRemark) {
        this.disputeRemark = disputeRemark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

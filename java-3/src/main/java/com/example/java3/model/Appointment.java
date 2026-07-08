package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 线下自提预约实体
 * <p>
 * 买家下单后可与卖家约定线下自提时间地点。
 * </p>
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联订单 ID */
    @Column(nullable = false)
    private Long orderId;

    /** 预约人（买家）用户 ID */
    @Column(nullable = false)
    private Long buyerId;

    /** 卖家用户 ID */
    @Column(nullable = false)
    private Long sellerId;

    /** 预约时间 */
    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    /** 自提地点 */
    @Column(nullable = false, length = 200)
    private String location;

    /** 备注 */
    @Column(length = 500)
    private String remark;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Appointment() {
    }

    /**
     * 业务构造方法
     *
     * @param orderId         订单 ID
     * @param buyerId         买家 ID
     * @param sellerId        卖家 ID
     * @param appointmentTime 预约时间
     * @param location        地点
     * @param remark          备注
     */
    public Appointment(Long orderId, Long buyerId, Long sellerId,
                       LocalDateTime appointmentTime, String location, String remark) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.appointmentTime = appointmentTime;
        this.location = location;
        this.remark = remark;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

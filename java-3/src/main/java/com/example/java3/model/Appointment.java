// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录预约时间与创建时间
import java.time.LocalDateTime;

/**
 * 线下自提预约实体
 * <p>
 * 买家下单后可与卖家约定线下自提时间地点。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 appointments，承载线下自提预约信息
@Table(name = "appointments")
public class Appointment {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联订单 ID */
    // 非空；关联 Order 表主键，预约基于已下单订单
    @Column(nullable = false)
    private Long orderId;

    /** 预约人（买家）用户 ID */
    // 非空；冗余存储买家 ID，便于直接查询而不必关联订单
    @Column(nullable = false)
    private Long buyerId;

    /** 卖家用户 ID */
    // 非空；冗余存储卖家 ID，便于双方都收到预约提醒
    @Column(nullable = false)
    private Long sellerId;

    /** 预约时间 */
    // 非空；约定线下自提的具体时间点
    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    /** 自提地点 */
    // 非空，长度上限 200；约定线下自提的地点描述
    @Column(nullable = false, length = 200)
    private String location;

    /** 备注 */
    // 可空，长度上限 500；买家对预约的补充说明（如携带零钱、找人代取等）
    @Column(length = 500)
    private String remark;

    /** 创建时间 */
    // 非空，预约记录创建时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
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
        // 设置关联订单 ID
        this.orderId = orderId;
        // 设置买家用户 ID
        this.buyerId = buyerId;
        // 设置卖家用户 ID
        this.sellerId = sellerId;
        // 设置预约时间
        this.appointmentTime = appointmentTime;
        // 设置自提地点
        this.location = location;
        // 设置备注
        this.remark = remark;
        // 创建时间取当前系统时间
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

    // 获取预约时间
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    // 设置预约时间
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    // 获取自提地点
    public String getLocation() {
        return location;
    }

    // 设置自提地点
    public void setLocation(String location) {
        this.location = location;
    }

    // 获取备注
    public String getRemark() {
        return remark;
    }

    // 设置备注
    public void setRemark(String remark) {
        this.remark = remark;
    }

    // 获取预约记录创建时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置预约记录创建时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

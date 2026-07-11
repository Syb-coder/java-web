// 声明包路径
package com.example.java8.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// 导入枚举映射注解
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;
import jakarta.persistence.PreUpdate;

/**
 * 定制订单实体
 * <p>
 * 客户选定款式 + 面料 + 量体数据后生成的定制订单。
 * 总价 = 款式工费 + 面料单价 × 默认 3 米用料。
 * 状态由管理员推进，遵循 {@link OrderStatus} 的固定流程。
 * </p>
 */
@Entity
@Table(name = "orders")
public class Order {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 下单用户（多对一） */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 所选款式（多对一） */
    @ManyToOne
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    /** 所选面料（多对一） */
    @ManyToOne
    @JoinColumn(name = "fabric_id", nullable = false)
    private Fabric fabric;

    /** 所用量体数据（多对一，可为空以支持未量体先下单场景） */
    @ManyToOne
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    /** 总价（元）：下单时计算并冻结，避免后续面料/款式价格变动影响历史订单 */
    @Column(nullable = false)
    private Double totalPrice;

    /** 订单状态 */
    // @Enumerated(EnumType.STRING) 表示枚举值以字符串形式存入数据库
    // 选用 STRING 而非默认的 ORDINAL，避免未来枚举顺序调整导致历史数据错乱
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /** 客户备注，如绣字、内衬颜色等 */
    @Column(length = 500)
    private String remark;

    /** 下单时间 */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 无参构造方法 */
    public Order() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Style getStyle() { return style; }
    public void setStyle(Style style) { this.style = style; }
    public Fabric getFabric() { return fabric; }
    public void setFabric(Fabric fabric) { this.fabric = fabric; }
    public Measurement getMeasurement() { return measurement; }
    public void setMeasurement(Measurement measurement) { this.measurement = measurement; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    @PreUpdate
    public void onPreUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

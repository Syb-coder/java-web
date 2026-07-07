// 声明包路径
package com.example.java8.model;

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
 * 面料实体
 * <p>
 * 描述可供客户选择的面料商品，包含材质、颜色、单价、库存等关键信息。
 * 描述字段使用 length=1000 以容纳详细的产地/手感/适用款式说明，
 * 避免默认 VARCHAR(255) 截断（吸取 java-5 项目 V2EX 同步失败教训）。
 * </p>
 */
@Entity
@Table(name = "fabrics")
public class Fabric {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 面料名称，如「意大利羊毛精纺」 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 材质，如「羊毛」「棉」「麻」「丝」「羊绒」「牛仔」 */
    @Column(nullable = false, length = 30)
    private String material;

    /** 颜色，如「藏青」「炭灰」「米白」 */
    @Column(nullable = false, length = 30)
    private String color;

    /** 单价（元/米），用于订单总价计算 */
    @Column(nullable = false)
    private Double unitPrice;

    /** 库存（米），下单时本系统未做扣减（教学场景），实际生产应扣减 */
    private Double stock;

    /** 详细描述：产地、手感、克重、适用款式等，扩展长度避免截断 */
    @Column(length = 1000)
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 无参构造方法 */
    public Fabric() {
    }

    /**
     * 全参构造方法
     */
    public Fabric(String name, String material, String color, Double unitPrice, Double stock, String description) {
        this.name = name;
        this.material = material;
        this.color = color;
        this.unitPrice = unitPrice;
        this.stock = stock;
        this.description = description;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getStock() { return stock; }
    public void setStock(Double stock) { this.stock = stock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

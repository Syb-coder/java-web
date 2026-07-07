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
 * 款式实体
 * <p>
 * 描述可供客户选择的服装款式模板，包含类别、工费等关键信息。
 * 工费指裁缝师傅加工该款式的手工费（不含面料费用）。
 * 订单总价 = 款式工费 + 面料单价 × 默认 3 米用料。
 * </p>
 */
@Entity
@Table(name = "styles")
public class Style {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 款式名称，如「单排两粒扣商务西装」 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 类别，如「西装」「衬衫」「连衣裙」「风衣」「西裤」「旗袍」 */
    @Column(nullable = false, length = 30)
    private String category;

    /** 工费（元），裁缝师傅加工费 */
    @Column(nullable = false)
    private Double craftFee;

    /** 详细描述：款式特点、设计要点、适用场合等 */
    @Column(length = 1000)
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 无参构造方法 */
    public Style() {
    }

    /**
     * 全参构造方法
     */
    public Style(String name, String category, Double craftFee, String description) {
        this.name = name;
        this.category = category;
        this.craftFee = craftFee;
        this.description = description;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getCraftFee() { return craftFee; }
    public void setCraftFee(Double craftFee) { this.craftFee = craftFee; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

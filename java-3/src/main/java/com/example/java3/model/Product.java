// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录商品发布时间
import java.time.LocalDateTime;

/**
 * 闲置商品实体
 * <p>
 * 商品发布后需经管理员审核（auditStatus=APPROVED）方可在前台展示。
 * 价格、描述等字段使用较大长度以承载详细商品信息。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 products，承载商品主信息
@Table(name = "products")
public class Product {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商品标题 */
    // 非空，长度上限 100；商品核心展示字段
    @Column(nullable = false, length = 100)
    private String title;

    /** 商品描述（详细说明），长度放大到 2000 */
    // 可空，长度上限 2000；放大的目的是承载详细商品说明（成色、规格、配件等）
    @Column(length = 2000)
    private String description;

    /** 期望价格（元） */
    // 非空；商品交易核心字段，单位为元
    @Column(nullable = false)
    private Double price;

    /** 原价（可选，用于展示性价比） */
    // 可空；用于前台展示"划线价"提示性价比
    private Double originalPrice;

    /** 商品图片 URL（多张用分号分隔） */
    // 可空，长度上限 1000；多图 URL 通过分号拼接存储，避免引入额外图片表
    @Column(length = 1000)
    private String images;

    /** 新旧程度描述（如 9 成新） */
    // 可空，长度上限 50
    @Column(length = 50)
    private String conditionLevel;

    /** 所属分类 ID */
    // 非空；关联 ProductCategory 表的主键，但未使用外键约束（便于级联删除与迁移）
    @Column(nullable = false)
    private Long categoryId;

    /** 发布者（学生用户）ID */
    // 非空；关联 User 表主键，标识商品归属
    @Column(nullable = false)
    private Long sellerId;

    /** 审核状态：PENDING/APPROVED/REJECTED */
    // 声明枚举以字符串形式持久化，便于运维直接读库排查
    @Enumerated(EnumType.STRING)
    // 非空，长度上限 20
    @Column(nullable = false, length = 20)
    private ProductAuditStatus auditStatus;

    /** 审核备注（驳回原因等） */
    // 可空，长度上限 500；驳回时管理员填写原因，反馈给卖家
    @Column(length = 500)
    private String auditRemark;

    /** 是否已售出（售出后前台不再展示，但保留记录） */
    // 非空；保留记录是为了交易历史追溯与评价功能
    @Column(nullable = false)
    private Boolean sold;

    /** 发布时间 */
    // 非空，发布时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，数据更新前由 @PreUpdate 自动填充 */
    private LocalDateTime updateTime;

    /** 每次更新前自动填充修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Product() {
    }

    /**
     * 业务构造方法：学生发布商品时使用
     *
     * @param title          标题
     * @param description    描述
     * @param price          价格
     * @param originalPrice  原价
     * @param images         图片 URL
     * @param conditionLevel 新旧程度
     * @param categoryId     分类 ID
     * @param sellerId       发布者 ID
     */
    public Product(String title, String description, Double price, Double originalPrice,
                   String images, String conditionLevel, Long categoryId, Long sellerId) {
        // 设置商品标题
        this.title = title;
        // 设置商品描述
        this.description = description;
        // 设置期望价格
        this.price = price;
        // 设置原价（用于展示性价比）
        this.originalPrice = originalPrice;
        // 设置图片 URL 字符串
        this.images = images;
        // 设置新旧程度描述
        this.conditionLevel = conditionLevel;
        // 设置所属分类 ID
        this.categoryId = categoryId;
        // 设置发布者 ID
        this.sellerId = sellerId;
        // 默认审核状态为待审核：所有新发布商品必须经过人工审核才能上架，防止违规内容
        this.auditStatus = ProductAuditStatus.PENDING;
        // 默认未售出
        this.sold = false;
        // 发布时间取当前系统时间
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

    // 获取商品标题
    public String getTitle() {
        return title;
    }

    // 设置商品标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取商品描述
    public String getDescription() {
        return description;
    }

    // 设置商品描述
    public void setDescription(String description) {
        this.description = description;
    }

    // 获取期望价格
    public Double getPrice() {
        return price;
    }

    // 设置期望价格
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取原价
    public Double getOriginalPrice() {
        return originalPrice;
    }

    // 设置原价
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    // 获取图片 URL 字符串（多张以分号分隔）
    public String getImages() {
        return images;
    }

    // 设置图片 URL 字符串
    public void setImages(String images) {
        this.images = images;
    }

    // 获取新旧程度描述
    public String getConditionLevel() {
        return conditionLevel;
    }

    // 设置新旧程度描述
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    // 获取所属分类 ID
    public Long getCategoryId() {
        return categoryId;
    }

    // 设置所属分类 ID
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // 获取发布者 ID
    public Long getSellerId() {
        return sellerId;
    }

    // 设置发布者 ID
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    // 获取审核状态，用于判断商品是否可前台展示
    public ProductAuditStatus getAuditStatus() {
        return auditStatus;
    }

    // 设置审核状态（管理员审核操作时调用）
    public void setAuditStatus(ProductAuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    // 获取审核备注（驳回原因）
    public String getAuditRemark() {
        return auditRemark;
    }

    // 设置审核备注
    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    // 获取是否已售出标识
    public Boolean getSold() {
        return sold;
    }

    // 设置是否已售出标识
    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    // 获取发布时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置发布时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

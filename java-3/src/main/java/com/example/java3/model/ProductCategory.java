// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录分类创建时间
import java.time.LocalDateTime;

/**
 * 闲置商品分类实体
 * <p>
 * 用于商品归类（教材、数码、家具、运动器材等），管理员可维护。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 categories，承载商品分类元数据
@Table(name = "categories")
public class ProductCategory {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分类名称（唯一） */
    // 非空且唯一，长度上限 50；唯一约束避免重复分类
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 分类图标（可选，CSS 类名或 emoji） */
    // 可空，长度上限 50；存储图标标识便于前台渲染
    @Column(length = 50)
    private String icon;

    /** 排序序号，越小越靠前 */
    // 非空；用于前台分类列表展示排序
    @Column(nullable = false)
    private Integer sortOrder;

    /** 创建时间 */
    // 非空，分类创建时写入
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
    public ProductCategory() {
    }

    /**
     * 业务构造方法
     *
     * @param name      分类名称
     * @param icon      图标
     * @param sortOrder 排序序号
     */
    public ProductCategory(String name, String icon, Integer sortOrder) {
        // 设置分类名称
        this.name = name;
        // 设置分类图标
        this.icon = icon;
        // 设置排序序号
        this.sortOrder = sortOrder;
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

    // 获取分类名称
    public String getName() {
        return name;
    }

    // 设置分类名称
    public void setName(String name) {
        this.name = name;
    }

    // 获取分类图标标识
    public String getIcon() {
        return icon;
    }

    // 设置分类图标标识
    public void setIcon(String icon) {
        this.icon = icon;
    }

    // 获取排序序号，用于前台列表排序
    public Integer getSortOrder() {
        return sortOrder;
    }

    // 设置排序序号
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    // 获取分类创建时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置分类创建时间
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

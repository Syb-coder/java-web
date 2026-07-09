// 声明包路径，存放 JPA 实体类
package com.example.java4.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 图书分类实体
 * <p>
 * 描述图书的中图法分类体系（如"计算机/TP3"、"文学/I"等），用于分类浏览与检索。
 * 一个分类下可包含多本图书（{@link Book}）。
 * </p>
 * <p>
 * 设计说明：分类独立成表而非字符串字段，便于后台管理与分类统计；
 * sortOrder 字段支持自定义排序，方便管理员调整分类展示顺序。
 * </p>
 */
@Entity // 声明本类为 JPA 实体
@Table(name = "book_categories") // 指定映射表名为 book_categories
public class BookCategory {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 分类名称（如"计算机科学"） */
    @Column(nullable = false, unique = true, length = 50) // 非空、唯一、长度 50
    private String name;

    /** 分类描述，简要说明该分类涵盖的图书范围 */
    @Column(length = 200) // 长度 200，允许为空
    private String description;

    /** 排序序号，数值越小越靠前 */
    @Column(nullable = false) // 非空，默认为 0
    private Integer sortOrder;

    /** 创建时间 */
    @Column(nullable = false, updatable = false) // 非空、不可更新
    private LocalDateTime createTime;

    /** 最后修改时间，更新时由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /** 更新前自动设置最后修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public BookCategory() {
    }

    /**
     * 业务构造方法
     *
     * @param name        分类名称
     * @param description 分类描述
     * @param sortOrder   排序序号
     */
    public BookCategory(String name, String description, Integer sortOrder) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

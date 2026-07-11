// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 文章分类实体
 * <p>
 * 对网络安全知识进行主题划分，例如 Web 安全、移动安全、密码学、社会工程学等。
 * 一篇文章归属于一个分类；一个分类下可有多篇文章。
 * </p>
 * <p>
 * 实体关系：与 Article 为一对多；与 Question 为一对多（题库按主题组卷共用此分类）。
 * 分类作为知识体系的纵向维度，被文章与题目共同引用，故删除需级校验引用情况。
 * </p>
 */
@Entity  // 标识为 JPA 实体
@Table(name = "category")
public class Category {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，便于跨表外键引用
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分类名称（唯一），如"Web 安全" */
    // unique=true：分类名唯一防止前台导航出现重复项；length=50：分类名简短，50 字符足够
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 分类描述，用于后台管理与前台分类导览 */
    // length=200：描述性文字，200 字符足以概括分类主题，超出则前端截断
    @Column(length = 200)
    private String description;

    /** 排序序号，越小越靠前 */
    // nullable=false：排序字段必须有值，否则前台导航顺序不确定影响用户体验
    @Column(nullable = false)
    private int sortOrder = 0;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法：JPA 规范要求 */
    public Category() {
    }

    /**
     * 全参构造方法
     *
     * @param name        分类名称
     * @param description 分类描述
     * @param sortOrder   排序序号
     */
    public Category(String name, String description, int sortOrder) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }

    /**
     * 设置分类名称。
     * 业务约束：调用方需先做重名校验，因数据库层 unique 约束触发会抛 DataIntegrityViolationException，
     * 提前校验可返回友好提示而非 500 错误。
     */
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

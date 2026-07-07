package com.example.java6.model; // 定义实体类所在包,model 包存放所有 JPA 实体与业务枚举

import jakarta.persistence.Column; // 导入 JPA @Column 注解,用于配置列的可空性、长度等属性
import jakarta.persistence.Entity; // 导入 JPA @Entity 注解,标记类为数据库表映射实体
import jakarta.persistence.EnumType; // 导入枚举映射类型枚举,STRING 表示以字符串形式存储枚举名
import jakarta.persistence.Enumerated; // 导入 JPA @Enumerated 注解,指定枚举字段的持久化方式
import jakarta.persistence.GeneratedValue; // 导入 JPA @GeneratedValue 注解,声明主键生成策略
import jakarta.persistence.GenerationType; // 导入主键生成策略枚举,IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 导入 JPA @Id 注解,标记实体主键字段
import jakarta.persistence.Table; // 导入 JPA @Table 注解,显式指定数据库表名

import java.time.LocalDateTime; // 导入日期时间类,用于记录文章创建时间

/**
 * 安全知识文章实体
 *
 * <p>承载网络安全科普知识文章数据，包括防护技巧、
 * 科普文章、案例分析、应急响应四大分类。</p>
 */
@Entity // 标记为 JPA 实体,Hibernate 自动建表与持久化管理
@Table(name = "knowledge_article") // 指定表名为 knowledge_article,与 News 等业务表区分
public class KnowledgeArticle {

    /** 主键 ID */
    @Id // 标记为主键字段,建立主键索引加速查询
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略,由 H2 数据库自动分配
    private Long id; // 文章唯一标识 ID

    /** 文章标题 */
    @Column(nullable = false, length = 200) // 列约束:非空、最大长度 200,标题为必填项
    private String title; // 文章标题,前台列表与详情页核心展示字段

    /** 文章分类 */
    @Enumerated(EnumType.STRING) // 以字符串形式存储枚举名,保证数据库可读性与枚举顺序可调整
    @Column(nullable = false, length = 30) // 列约束:非空、最大长度 30,与枚举名长度匹配
    private KnowledgeCategory category; // 文章分类枚举,用于前台分类筛选与后台分类管理

    /** 文章摘要 */
    @Column(length = 500) // 列约束:最大长度 500,允许为空
    private String summary; // 文章摘要,用于列表页快速预览

    /** 文章正文内容 */
    @Column(nullable = false, length = 8000) // 列约束:非空、最大长度 8000,容纳较长科普正文
    private String content; // 文章正文,详情页渲染的核心内容

    /** 作者/编者 */
    @Column(length = 100) // 列约束:最大长度 100,允许为空
    private String author; // 文章作者或编者,标注内容来源以体现权威性

    /** 阅读量 */
    @Column(nullable = false) // 列约束:非空,通过默认值 0 保证统计准确性
    private Integer viewCount = 0; // 阅读量,前台访问详情页时累加,用于热度排序

    /** 记录创建时间 */
    @Column(nullable = false, updatable = false) // 列约束:非空且不可更新,创建后永久不变
    private LocalDateTime createdAt = LocalDateTime.now(); // 记录创建时间,字段初始化即赋值,用于数据审计

    public KnowledgeArticle() {
        // JPA 规范要求的无参构造器,Hibernate 通过反射实例化实体时调用
    }

    public KnowledgeArticle(String title, KnowledgeCategory category, String summary,
                            String content, String author) {
        // 业务便捷构造器:初始化文章核心字段,viewCount 与 createdAt 使用默认值
        this.title = title; // 赋值文章标题
        this.category = category; // 赋值文章分类
        this.summary = summary; // 赋值文章摘要
        this.content = content; // 赋值文章正文
        this.author = author; // 赋值作者/编者
    }

    public Long getId() {
        // 获取文章主键 ID,用于详情查询与外键关联
        return id;
    }

    public void setId(Long id) {
        // 设置文章主键 ID,通常仅由 JPA 持久化层使用
        this.id = id;
    }

    public String getTitle() {
        // 获取文章标题,供前台展示与后台列表渲染
        return title;
    }

    public void setTitle(String title) {
        // 设置文章标题,用于创建或编辑文章
        this.title = title;
    }

    public KnowledgeCategory getCategory() {
        // 获取文章分类枚举,供分类筛选与统计使用
        return category;
    }

    public void setCategory(KnowledgeCategory category) {
        // 设置文章分类,用于文章归类管理
        this.category = category;
    }

    public String getSummary() {
        // 获取文章摘要,供列表页预览展示
        return summary;
    }

    public void setSummary(String summary) {
        // 设置文章摘要,用于编辑文章时填写
        this.summary = summary;
    }

    public String getContent() {
        // 获取文章正文,供详情页渲染
        return content;
    }

    public void setContent(String content) {
        // 设置文章正文,用于创建或编辑文章
        this.content = content;
    }

    public String getAuthor() {
        // 获取作者/编者,供详情页标注内容来源
        return author;
    }

    public void setAuthor(String author) {
        // 设置作者/编者,用于编辑文章时填写
        this.author = author;
    }

    public Integer getViewCount() {
        // 获取阅读量,供热度排序与统计展示
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        // 设置阅读量,通常由访问详情页的拦截器累加更新
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        // 获取记录创建时间,用于数据审计与排序
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        // 设置记录创建时间,主要用于数据迁移或测试场景
        this.createdAt = createdAt;
    }
}

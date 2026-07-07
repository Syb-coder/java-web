package com.example.java6.model; // 定义实体类所在包,model 包集中存放所有 JPA 实体与业务枚举

import jakarta.persistence.Column; // 导入 JPA @Column 注解,用于配置列的可空性、长度等属性
import jakarta.persistence.Entity; // 导入 JPA @Entity 注解,标记类为可持久化的数据库实体
import jakarta.persistence.EnumType; // 导入枚举映射类型枚举,STRING 表示以字符串形式存储枚举名
import jakarta.persistence.Enumerated; // 导入 JPA @Enumerated 注解,指定枚举字段的持久化方式
import jakarta.persistence.GeneratedValue; // 导入 JPA @GeneratedValue 注解,声明主键生成策略
import jakarta.persistence.GenerationType; // 导入主键生成策略枚举,IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 导入 JPA @Id 注解,标记实体主键字段
import jakarta.persistence.Table; // 导入 JPA @Table 注解,显式指定数据库表名

import java.time.LocalDateTime; // 导入日期时间类,用于记录新闻发布时间与记录创建时间

/**
 * 新闻资讯实体
 *
 * <p>承载网络安全官网发布的新闻资讯数据，包括标题、分类、
 * 摘要、正文、来源、发布时间、阅读量等核心字段。</p>
 */
@Entity // 标记为 JPA 实体,Hibernate 会将其映射为数据库表
@Table(name = "news") // 指定表名为 news,简短表名便于 SQL 编写与维护
public class News {

    /** 主键 ID */
    @Id // 标记为主键字段,数据库层面建立主键索引加速查询
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略,由 H2 数据库自动分配
    private Long id; // 新闻唯一标识 ID

    /** 新闻标题 */
    @Column(nullable = false, length = 200) // 列约束:非空、最大长度 200,标题为必填项
    private String title; // 新闻标题,前台列表与详情页的核心展示字段

    /** 新闻分类 */
    @Enumerated(EnumType.STRING) // 以字符串形式存储枚举名,相比 ORDINAL 更利于数据库可读性与枚举顺序调整
    @Column(nullable = false, length = 30) // 列约束:非空、最大长度 30,与枚举名长度匹配
    private NewsCategory category; // 新闻分类枚举,用于前台分类筛选与后台分类管理

    /** 新闻摘要 */
    @Column(length = 500) // 列约束:最大长度 500,允许为空(部分新闻可不填写摘要)
    private String summary; // 新闻摘要,用于列表页快速预览,提升用户浏览效率

    /** 新闻正文内容 */
    @Column(nullable = false, length = 8000) // 列约束:非空、最大长度 8000,容纳较长正文
    private String content; // 新闻正文,详情页渲染的核心内容

    /** 信息来源 */
    @Column(length = 100) // 列约束:最大长度 100,允许为空
    private String source; // 信息来源,标注新闻出处以体现权威性与可追溯性

    /** 发布时间 */
    @Column(nullable = false) // 列约束:非空,发布时间为必填项
    private LocalDateTime publishTime; // 新闻对外展示的发布时间,可与创建时间不同以支持定时发布

    /** 阅读量 */
    @Column(nullable = false) // 列约束:非空,通过默认值 0 保证统计准确性
    private Integer viewCount = 0; // 阅读量,前台访问详情页时累加,用于热度排序

    /** 是否置顶 */
    @Column(nullable = false) // 列约束:非空,通过默认值 false 保证逻辑明确
    private Boolean top = false; // 是否置顶标识,true 时在列表页优先展示

    /** 记录创建时间 */
    @Column(nullable = false, updatable = false) // 列约束:非空且不可更新,创建后永久不变
    private LocalDateTime createdAt = LocalDateTime.now(); // 记录创建时间,字段初始化即赋值,用于数据审计

    public News() {
        // JPA 规范要求的无参构造器,Hibernate 通过反射实例化实体时调用
    }

    public News(String title, NewsCategory category, String summary, String content,
                String source, LocalDateTime publishTime) {
        // 业务便捷构造器:初始化新闻必填与核心字段,viewCount、top、createdAt 使用默认值
        this.title = title; // 赋值新闻标题
        this.category = category; // 赋值新闻分类
        this.summary = summary; // 赋值新闻摘要
        this.content = content; // 赋值新闻正文
        this.source = source; // 赋值信息来源
        this.publishTime = publishTime; // 赋值发布时间
    }

    public Long getId() {
        // 获取新闻主键 ID,用于详情查询与外键关联
        return id;
    }

    public void setId(Long id) {
        // 设置新闻主键 ID,通常仅由 JPA 持久化层使用
        this.id = id;
    }

    public String getTitle() {
        // 获取新闻标题,供前台展示与后台列表渲染
        return title;
    }

    public void setTitle(String title) {
        // 设置新闻标题,用于创建或编辑新闻
        this.title = title;
    }

    public NewsCategory getCategory() {
        // 获取新闻分类枚举,供分类筛选与统计使用
        return category;
    }

    public void setCategory(NewsCategory category) {
        // 设置新闻分类,用于新闻归类管理
        this.category = category;
    }

    public String getSummary() {
        // 获取新闻摘要,供列表页预览展示
        return summary;
    }

    public void setSummary(String summary) {
        // 设置新闻摘要,用于编辑新闻时填写
        this.summary = summary;
    }

    public String getContent() {
        // 获取新闻正文,供详情页渲染
        return content;
    }

    public void setContent(String content) {
        // 设置新闻正文,用于创建或编辑新闻
        this.content = content;
    }

    public String getSource() {
        // 获取信息来源,供详情页标注出处
        return source;
    }

    public void setSource(String source) {
        // 设置信息来源,用于编辑新闻时填写
        this.source = source;
    }

    public LocalDateTime getPublishTime() {
        // 获取发布时间,供前台按时间排序与定时发布判断
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        // 设置发布时间,用于创建或调整新闻发布时间
        this.publishTime = publishTime;
    }

    public Integer getViewCount() {
        // 获取阅读量,供热度排序与统计展示
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        // 设置阅读量,通常由访问详情页的拦截器累加更新
        this.viewCount = viewCount;
    }

    public Boolean getTop() {
        // 获取是否置顶标识,供列表页优先级排序
        return top;
    }

    public void setTop(Boolean top) {
        // 设置是否置顶,用于后台管理员调整新闻展示优先级
        this.top = top;
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

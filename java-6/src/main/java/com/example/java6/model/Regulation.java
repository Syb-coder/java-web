package com.example.java6.model; // 定义实体类所在包,model 包集中存放所有 JPA 实体与业务枚举

import jakarta.persistence.Column; // 导入 JPA @Column 注解,用于配置列的可空性、长度等属性
import jakarta.persistence.Entity; // 导入 JPA @Entity 注解,标记类为数据库表映射实体
import jakarta.persistence.EnumType; // 导入枚举映射类型枚举,STRING 表示以字符串形式存储枚举名
import jakarta.persistence.Enumerated; // 导入 JPA @Enumerated 注解,指定枚举字段的持久化方式
import jakarta.persistence.GeneratedValue; // 导入 JPA @GeneratedValue 注解,声明主键生成策略
import jakarta.persistence.GenerationType; // 导入主键生成策略枚举,IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 导入 JPA @Id 注解,标记实体主键字段
import jakarta.persistence.Table; // 导入 JPA @Table 注解,显式指定数据库表名

import java.time.LocalDate; // 导入日期类(不含时间),用于记录法规发布日期与生效日期

/**
 * 政策法规实体
 *
 * <p>承载国家网络安全相关法律法规数据，按效力层级分类，
 * 包括法律、行政法规、部门规章、规范性文件四类。</p>
 */
@Entity // 标记为 JPA 实体,Hibernate 自动建表与持久化管理
@Table(name = "regulation") // 指定表名为 regulation,与新闻、知识等业务表区分
public class Regulation {

    /** 主键 ID */
    @Id // 标记为主键字段,建立主键索引加速查询
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略,由 H2 数据库自动分配
    private Long id; // 法规唯一标识 ID

    /** 法规标题 */
    @Column(nullable = false, length = 200) // 列约束:非空、最大长度 200,标题为必填项
    private String title; // 法规标题,前台列表与详情页核心展示字段

    /** 颁布机构 */
    @Column(nullable = false, length = 100) // 列约束:非空、最大长度 100,颁布机构为必填项
    private String issuingAuthority; // 颁布机构,标注法规制定主体以体现法律效力层级

    /** 法规分类 */
    @Enumerated(EnumType.STRING) // 以字符串形式存储枚举名,保证数据库可读性与枚举顺序可调整
    @Column(nullable = false, length = 30) // 列约束:非空、最大长度 30,与枚举名长度匹配
    private RegulationCategory category; // 法规分类枚举,按效力层级区分法律、行政法规等

    /** 发布日期 */
    @Column(nullable = false) // 列约束:非空,发布日期为必填项
    private LocalDate publishDate; // 法规对外发布日期,用于按时间排序与时效性判断

    /** 生效日期 */
    private LocalDate effectiveDate; // 法规生效日期,允许为空(部分法规生效日期未定或与发布日期一致);无 @Column 默认按字段名映射

    /** 法规正文内容 */
    @Column(nullable = false, length = 8000) // 列约束:非空、最大长度 8000,容纳较长法律条文
    private String content; // 法规正文,详情页渲染的核心内容

    /** 法规文号 */
    @Column(length = 100) // 列约束:最大长度 100,允许为空
    private String documentNumber; // 法规文号,如"主席令第××号",用于精确检索与引用

    public Regulation() {
        // JPA 规范要求的无参构造器,Hibernate 通过反射实例化实体时调用
    }

    public Regulation(String title, String issuingAuthority, RegulationCategory category,
                      LocalDate publishDate, String content) {
        // 业务便捷构造器:初始化法规必填字段,effectiveDate 与 documentNumber 使用默认值(后填)
        this.title = title; // 赋值法规标题
        this.issuingAuthority = issuingAuthority; // 赋值颁布机构
        this.category = category; // 赋值法规分类
        this.publishDate = publishDate; // 赋值发布日期
        this.content = content; // 赋值法规正文
    }

    public Long getId() {
        // 获取法规主键 ID,用于详情查询与外键关联
        return id;
    }

    public void setId(Long id) {
        // 设置法规主键 ID,通常仅由 JPA 持久化层使用
        this.id = id;
    }

    public String getTitle() {
        // 获取法规标题,供前台展示与后台列表渲染
        return title;
    }

    public void setTitle(String title) {
        // 设置法规标题,用于创建或编辑法规
        this.title = title;
    }

    public String getIssuingAuthority() {
        // 获取颁布机构,供详情页标注制定主体
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        // 设置颁布机构,用于编辑法规时填写
        this.issuingAuthority = issuingAuthority;
    }

    public RegulationCategory getCategory() {
        // 获取法规分类枚举,供分类筛选与统计使用
        return category;
    }

    public void setCategory(RegulationCategory category) {
        // 设置法规分类,用于法规归类管理
        this.category = category;
    }

    public LocalDate getPublishDate() {
        // 获取发布日期,供前台按时间排序与时效性展示
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        // 设置发布日期,用于创建或编辑法规
        this.publishDate = publishDate;
    }

    public LocalDate getEffectiveDate() {
        // 获取生效日期,供判断法规当前是否已生效
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        // 设置生效日期,用于编辑法规时补充或调整
        this.effectiveDate = effectiveDate;
    }

    public String getContent() {
        // 获取法规正文,供详情页渲染
        return content;
    }

    public void setContent(String content) {
        // 设置法规正文,用于创建或编辑法规
        this.content = content;
    }

    public String getDocumentNumber() {
        // 获取法规文号,供精确检索与引用标注
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        // 设置法规文号,用于编辑法规时填写
        this.documentNumber = documentNumber;
    }
}

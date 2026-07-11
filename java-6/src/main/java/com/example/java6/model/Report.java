package com.example.java6.model; // 定义实体类所在包,model 包集中存放所有 JPA 实体与业务枚举

import jakarta.persistence.Column; // 导入 JPA @Column 注解,用于配置列的可空性、长度等属性
import jakarta.persistence.Entity; // 导入 JPA @Entity 注解,标记类为数据库表映射实体
import jakarta.persistence.EnumType; // 导入枚举映射类型枚举,STRING 表示以字符串形式存储枚举名
import jakarta.persistence.Enumerated; // 导入 JPA @Enumerated 注解,指定枚举字段的持久化方式
import jakarta.persistence.GeneratedValue; // 导入 JPA @GeneratedValue 注解,声明主键生成策略
import jakarta.persistence.GenerationType; // 导入主键生成策略枚举,IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 导入 JPA @Id 注解,标记实体主键字段
import jakarta.persistence.PreUpdate; // 导入 JPA @PreUpdate 注解,用于实体更新前自动设置更新时间
import jakarta.persistence.Table; // 导入 JPA @Table 注解,显式指定数据库表名

import java.time.LocalDateTime; // 导入日期时间类,用于记录举报提交时间与处置时间

/**
 * 举报记录实体
 *
 * <p>承载公众提交的违法和不良信息举报记录，包括举报类型、
 * 举报对象、举报描述、举报人信息以及处置状态等字段。</p>
 */
@Entity // 标记为 JPA 实体,Hibernate 自动建表与持久化管理
@Table(name = "report") // 指定表名为 report,与新闻、法规等业务表区分
public class Report {

    /** 主键 ID */
    @Id // 标记为主键字段,建立主键索引加速查询
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略,由 H2 数据库自动分配
    private Long id; // 举报记录唯一标识 ID

    /** 举报类型 */
    @Enumerated(EnumType.STRING) // 以字符串形式存储枚举名,保证数据库可读性与枚举顺序可调整
    @Column(nullable = false, length = 30) // 列约束:非空、最大长度 30,与枚举名长度匹配
    private ReportType reportType; // 举报类型枚举,用于举报中心分类处置与统计研判

    /** 举报对象链接 */
    @Column(length = 500) // 列约束:最大长度 500,允许为空(部分举报可能无明确 URL)
    private String targetUrl; // 举报对象链接,指向被举报的违法不良信息所在页面

    /** 举报描述 */
    @Column(nullable = false, length = 2000) // 列约束:非空、最大长度 2000,描述为必填项
    private String description; // 举报描述,公众详细说明被举报内容违法违规情形

    /** 举报人姓名 */
    @Column(length = 50) // 列约束:最大长度 50,允许为空(支持匿名举报)
    private String reporterName; // 举报人姓名,便于举报中心反馈处置结果

    /** 举报人联系方式 */
    @Column(length = 100) // 列约束:最大长度 100,允许为空(支持匿名举报)
    private String reporterContact; // 举报人联系方式,便于举报中心后续沟通核实

    /** 处理状态 */
    @Enumerated(EnumType.STRING) // 以字符串形式存储枚举名,保证数据库可读性与状态流转可追溯
    @Column(nullable = false, length = 20) // 列约束:非空、最大长度 20,与枚举名长度匹配
    private ReportStatus status = ReportStatus.PENDING; // 处理状态枚举,默认待处理,举报提交时即初始化

    /** 处置备注 */
    @Column(length = 1000) // 列约束:最大长度 1000,允许为空(未处置时无备注)
    private String handleNote; // 处置备注,记录举报中心处置过程与结论说明

    /** 举报提交时间 */
    @Column(nullable = false, updatable = false) // 列约束:非空且不可更新,提交后永久不变
    private LocalDateTime createdAt = LocalDateTime.now(); // 举报提交时间,字段初始化即赋值,用于时效统计

    /** 更新时间 */
    private LocalDateTime updateTime; // 实体更新时间,每次持久化更新时自动刷新

    /** 处置时间 */
    private LocalDateTime handledAt; // 处置完成时间,举报中心处置完毕时填写;无 @Column 默认按字段名映射

    public Report() {
        // JPA 规范要求的无参构造器,Hibernate 通过反射实例化实体时调用
    }

    public Report(ReportType reportType, String targetUrl, String description,
                  String reporterName, String reporterContact) {
        // 业务便捷构造器:初始化举报核心字段,status、handleNote、createdAt、handledAt 使用默认值
        this.reportType = reportType; // 赋值举报类型
        this.targetUrl = targetUrl; // 赋值举报对象链接
        this.description = description; // 赋值举报描述
        this.reporterName = reporterName; // 赋值举报人姓名
        this.reporterContact = reporterContact; // 赋值举报人联系方式
    }

    public Long getId() {
        // 获取举报记录主键 ID,用于详情查询与状态跟踪
        return id;
    }

    public void setId(Long id) {
        // 设置举报记录主键 ID,通常仅由 JPA 持久化层使用
        this.id = id;
    }

    public ReportType getReportType() {
        // 获取举报类型枚举,供分类处置与统计研判使用
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        // 设置举报类型,用于创建举报记录时归类
        this.reportType = reportType;
    }

    public String getTargetUrl() {
        // 获取举报对象链接,供举报中心核查被举报内容
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        // 设置举报对象链接,用于创建或编辑举报记录
        this.targetUrl = targetUrl;
    }

    public String getDescription() {
        // 获取举报描述,供举报中心了解举报详情
        return description;
    }

    public void setDescription(String description) {
        // 设置举报描述,用于创建举报记录时填写
        this.description = description;
    }

    public String getReporterName() {
        // 获取举报人姓名,供举报中心反馈处置结果
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        // 设置举报人姓名,用于创建或编辑举报记录
        this.reporterName = reporterName;
    }

    public String getReporterContact() {
        // 获取举报人联系方式,供举报中心后续沟通核实
        return reporterContact;
    }

    public void setReporterContact(String reporterContact) {
        // 设置举报人联系方式,用于创建或编辑举报记录
        this.reporterContact = reporterContact;
    }

    public ReportStatus getStatus() {
        // 获取处理状态枚举,供举报中心跟踪处置进度与公众查询反馈
        return status;
    }

    public void setStatus(ReportStatus status) {
        // 设置处理状态,举报中心处置过程中更新状态流转
        this.status = status;
    }

    public String getHandleNote() {
        // 获取处置备注,供了解处置过程与结论
        return handleNote;
    }

    public void setHandleNote(String handleNote) {
        // 设置处置备注,举报中心处置完毕时填写结论说明
        this.handleNote = handleNote;
    }

    public LocalDateTime getCreatedAt() {
        // 获取举报提交时间,用于时效统计与排序
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        // 设置举报提交时间,主要用于数据迁移或测试场景
        this.createdAt = createdAt;
    }

    public LocalDateTime getHandledAt() {
        // 获取处置时间,用于计算处置耗时与时效分析
        return handledAt;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        // 设置处置时间,举报中心处置完毕时记录
        this.handledAt = handledAt;
    }

    public LocalDateTime getUpdateTime() {
        // 获取更新时间
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        // 设置更新时间
        this.updateTime = updateTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

package com.example.java5.model; // 声明包路径，归属 model（模型层）

// ===== JPA 相关注解导入 =====
import jakarta.persistence.Column; // 字段列定义注解，用于指定长度等
import jakarta.persistence.Entity; // 实体类标记注解，告诉 JPA 这是一个可持久化的实体
import jakarta.persistence.EnumType; // 枚举映射类型（STRING=存字符串）
import jakarta.persistence.Enumerated; // 枚举字段映射注解
import jakarta.persistence.GeneratedValue; // 主键生成策略注解
import jakarta.persistence.GenerationType; // 主键生成策略枚举
import jakarta.persistence.Id; // 主键标记注解
import jakarta.persistence.Table; // 指定数据库表名注解
import jakarta.persistence.PrePersist; // 持久化前回调钩子注解
import jakarta.persistence.PreUpdate; // 更新前回调钩子注解

// ===== 时间类型导入 =====
import java.time.LocalDate; // 日期类型（不含时间），用于发布日期
import java.time.LocalDateTime; // 日期时间类型，用于创建时间戳

/**
 * 招聘岗位实体类
 * <p>
 * 职责：映射数据库 job_postings 表，承载招聘岗位的全部字段。
 * 被 Repository、Service、Controller 层引用，是系统的核心领域模型。
 * </p>
 * 持久化策略：由 Hibernate 自动建表（ddl-auto=update），
 * 长文本字段通过 @Column(length=...) 显式扩容，避免 V2EX 正文超长插入失败。
 */
@Entity // 标记为 JPA 实体，Hibernate 会为其创建表并管理生命周期
@Table(name = "job_postings") // 指定数据库表名为 job_postings（默认会用类名）
public class JobPosting {

    /** 主键 ID，由数据库自增生成，应用层不主动赋值 */
    @Id // 标记为主键字段
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略：依赖数据库自增列
    private Long id; // 主键类型用 Long（包装类），null 表示尚未持久化

    /** 岗位标题，如"高级 Java 后端工程师" */
    private String title; // 默认 VARCHAR(255)，足够存放标题

    /** 公司名称 */
    private String company; // 公司名，V2EX 数据用作者用户名占位

    /** 工作城市 */
    private String city; // 城市名，V2EX 数据从标题 [xxx] 提取

    /** 岗位分类（枚举：研发/产品/设计等） */
    @Enumerated(EnumType.STRING) // 枚举以字符串形式存入数据库，便于人工阅读和排查
    private JobCategory category; // 分类，V2EX 数据根据标题关键词推断

    /** 经验要求（枚举：应届/1-3年/3-5年等） */
    @Enumerated(EnumType.STRING) // 字符串存储，避免 ORDINAL 修改枚举顺序导致数据错乱
    private ExperienceLevel experience; // 经验等级

    /** 学历要求（枚举：本科/硕士等） */
    @Enumerated(EnumType.STRING) // 同上，字符串存储更安全
    private EducationLevel education; // 学历等级

    /** 薪资下限（单位：元），如 20000 表示 20K */
    private Integer salaryMin; // Integer 允许为 null（薪资面议场景）

    /** 薪资上限（单位：元），如 40000 表示 40K */
    private Integer salaryMax; // Integer 允许为 null

    /** 薪资描述文本，如"20-40K"，便于直接展示 */
    private String salaryDesc; // 冗余字段，避免前端每次拼接

    /** 技能标签，用 / 分隔，如"Java/Spring/MySQL" */
    private String skills; // 前端按 / 切分渲染为多个 tag

    /** 岗位职责：V2EX 正文可能很长，扩容到 4000 */
    @Column(length = 4000) // 显式指定列长度为 4000，避免 255 限制导致 V2EX 正文插入失败
    private String responsibilities; // 岗位职责描述

    /** 任职要求：长文本字段 */
    @Column(length = 4000) // 同样扩容，允许详细描述任职要求
    private String requirements; // 任职要求文本

    /** 福利描述：长文本字段 */
    @Column(length = 1000) // 福利描述通常比职责短，1000 足够
    private String benefits; // 福利待遇文本

    /** 公司简介：长文本字段 */
    @Column(length = 1000) // 公司一句话简介，1000 字符足够
    private String companyDesc; // 公司描述文本

    /** 发布日期（仅日期，不含时分秒） */
    private LocalDate publishedDate; // 用于列表排序和展示

    /** 数据来源（枚举：LOCAL 本地种子 / V2EX 同步） */
    @Enumerated(EnumType.STRING) // 字符串存储，前端按来源区分颜色标签
    private JobSource source; // 区分数据来源，便于运营筛选

    /** 外部数据源的唯一 ID（如 V2EX 主题 ID），用于去重 */
    private String externalId; // LOCAL 数据为 null，V2EX 数据为主题 ID 字符串

    /** 外部数据源的原帖链接 */
    private String externalUrl; // V2EX 主题 URL，LOCAL 数据为 null

    /** 发布人姓名（用户发布时填写，V2EX 同步数据用用户名占位） */
    private String publisherName; // 本次新增字段：发布人姓名，便于求职者联系

    /** 联系方式（邮箱/电话/微信等，用户发布时填写） */
    private String contact; // 本次新增字段：联系方式，V2EX 数据无此字段留空

    /** 记录创建时间戳，由 PrePersist 钩子自动填充 */
    private LocalDateTime createdAt; // 系统字段，用于排序和审计

    private LocalDateTime updateTime;

    /**
     * 无参构造函数
     * <p>
     * 为什么需要：JPA/Hibernate 规范要求实体类必须有无参构造函数，
     * 框架通过反射实例化对象后再逐字段赋值。
     * </p>
     */
    public JobPosting() {
    }

    /**
     * 持久化前回调钩子
     * <p>
     * 职责：在实体首次写入数据库前自动填充 createdAt 字段。
     * 触发时机：Hibernate 执行 INSERT 之前。
     * 为什么用钩子而非构造函数：构造函数在内存对象创建时即触发，
     * 而对象可能被创建后长时间未持久化，导致时间戳偏早；
     * 钩子保证时间戳贴近真实入库时刻。
     * </p>
     */
    @PrePersist // 标记为持久化前回调，Hibernate 在 INSERT 前调用
    void onCreate() {
        // 仅当业务层未主动设置 createdAt 时才填充，避免覆盖外部传入值
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // 取当前系统时间
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ===== 以下为字段对应的 getter/setter，供 MyBatis/Jackson/前端 等反射访问 =====

    /** 获取主键 ID */
    public Long getId() { return id; }
    /** 设置主键 ID（通常由数据库生成，应用层一般不调用） */
    public void setId(Long id) { this.id = id; }
    /** 获取岗位标题 */
    public String getTitle() { return title; }
    /** 设置岗位标题 */
    public void setTitle(String title) { this.title = title; }
    /** 获取公司名称 */
    public String getCompany() { return company; }
    /** 设置公司名称 */
    public void setCompany(String company) { this.company = company; }
    /** 获取工作城市 */
    public String getCity() { return city; }
    /** 设置工作城市 */
    public void setCity(String city) { this.city = city; }
    /** 获取岗位分类枚举 */
    public JobCategory getCategory() { return category; }
    /** 设置岗位分类枚举 */
    public void setCategory(JobCategory category) { this.category = category; }
    /** 获取经验要求枚举 */
    public ExperienceLevel getExperience() { return experience; }
    /** 设置经验要求枚举 */
    public void setExperience(ExperienceLevel experience) { this.experience = experience; }
    /** 获取学历要求枚举 */
    public EducationLevel getEducation() { return education; }
    /** 设置学历要求枚举 */
    public void setEducation(EducationLevel education) { this.education = education; }
    /** 获取薪资下限（元） */
    public Integer getSalaryMin() { return salaryMin; }
    /** 设置薪资下限（元） */
    public void setSalaryMin(Integer salaryMin) { this.salaryMin = salaryMin; }
    /** 获取薪资上限（元） */
    public Integer getSalaryMax() { return salaryMax; }
    /** 设置薪资上限（元） */
    public void setSalaryMax(Integer salaryMax) { this.salaryMax = salaryMax; }
    /** 获取薪资描述文本 */
    public String getSalaryDesc() { return salaryDesc; }
    /** 设置薪资描述文本 */
    public void setSalaryDesc(String salaryDesc) { this.salaryDesc = salaryDesc; }
    /** 获取技能标签（/ 分隔） */
    public String getSkills() { return skills; }
    /** 设置技能标签（/ 分隔） */
    public void setSkills(String skills) { this.skills = skills; }
    /** 获取岗位职责文本 */
    public String getResponsibilities() { return responsibilities; }
    /** 设置岗位职责文本 */
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    /** 获取任职要求文本 */
    public String getRequirements() { return requirements; }
    /** 设置任职要求文本 */
    public void setRequirements(String requirements) { this.requirements = requirements; }
    /** 获取福利待遇文本 */
    public String getBenefits() { return benefits; }
    /** 设置福利待遇文本 */
    public void setBenefits(String benefits) { this.benefits = benefits; }
    /** 获取公司简介文本 */
    public String getCompanyDesc() { return companyDesc; }
    /** 设置公司简介文本 */
    public void setCompanyDesc(String companyDesc) { this.companyDesc = companyDesc; }
    /** 获取发布日期 */
    public LocalDate getPublishedDate() { return publishedDate; }
    /** 设置发布日期 */
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    /** 获取数据来源枚举 */
    public JobSource getSource() { return source; }
    /** 设置数据来源枚举 */
    public void setSource(JobSource source) { this.source = source; }
    /** 获取外部数据源 ID */
    public String getExternalId() { return externalId; }
    /** 设置外部数据源 ID */
    public void setExternalId(String externalId) { this.externalId = externalId; }
    /** 获取外部原帖链接 */
    public String getExternalUrl() { return externalUrl; }
    /** 设置外部原帖链接 */
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    /** 获取发布人姓名 */
    public String getPublisherName() { return publisherName; }
    /** 设置发布人姓名 */
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    /** 获取联系方式 */
    public String getContact() { return contact; }
    /** 设置联系方式 */
    public void setContact(String contact) { this.contact = contact; }
    /** 获取记录创建时间戳 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** 设置记录创建时间戳（一般由 PrePersist 自动填充） */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

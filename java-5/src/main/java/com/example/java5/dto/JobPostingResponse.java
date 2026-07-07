package com.example.java5.dto; // 声明包路径，归属 dto（数据传输对象层）

// ===== 实体类导入，用于 from 转换方法 =====
import com.example.java5.model.JobPosting; // 招聘岗位实体

// ===== 时间类型导入 =====
import java.time.LocalDate; // 发布日期类型
import java.time.LocalDateTime; // 创建时间戳类型

/**
 * 招聘岗位响应 DTO
 * <p>
 * 职责：作为 Controller 返回给前端的统一数据结构，
 * 将 JobPosting 实体转换为对前端友好的扁平化字段，
 * 同时补充枚举的 label 中文标签，避免前端维护枚举映射表。
 * </p>
 * 为什么不用实体直接返回：实体含数据库字段（如 externalId）不应暴露；
 * 且枚举需要同时返回 code 和 label，实体无法直接表达。
 * </p>
 * 字段命名遵循驼峰，Jackson 自动序列化为 JSON 前端可直接消费。
 */
public record JobPostingResponse(
        // ===== 基础信息 =====
        Long id,                    // 主键 ID，前端用于编辑/删除定位
        String title,               // 岗位标题
        String company,             // 公司名称
        String city,                // 工作城市

        // ===== 分类枚举（同时返回 code 和中文 label） =====
        String category,            // 分类枚举名，如"DEVELOPMENT"
        String categoryLabel,       // 分类中文标签，如"研发类"，前端直接展示

        // ===== 经验/学历枚举 =====
        String experience,          // 经验枚举名
        String experienceLabel,     // 经验中文标签
        String education,           // 学历枚举名
        String educationLabel,      // 学历中文标签

        // ===== 薪资信息 =====
        Integer salaryMin,          // 薪资下限（元）
        Integer salaryMax,          // 薪资上限（元）
        String salaryDesc,          // 薪资描述文本

        // ===== 岗位详情 =====
        String skills,              // 技能标签（/ 分隔）
        String responsibilities,    // 岗位职责
        String requirements,        // 任职要求
        String benefits,            // 福利待遇
        String companyDesc,         // 公司简介

        // ===== 时间字段 =====
        LocalDate publishedDate,    // 发布日期
        String source,              // 数据来源枚举名
        String sourceLabel,         // 数据来源中文标签，如"本地种子"/"V2EX酷工作"
        String externalUrl,         // 外部原帖链接（V2EX 数据才有）

        // ===== 发布人信息（本次新增字段） =====
        String publisherName,       // 发布人姓名
        String contact,             // 联系方式

        // ===== 系统字段 =====
        LocalDateTime createdAt     // 记录创建时间戳
) {

    /**
     * 实体转 DTO 的工厂方法
     * <p>
     * 职责：将 JobPosting 实体字段逐一映射到响应 DTO，
     * 对枚举字段做 null 安全处理并额外提取中文 label。
     * </p>
     * 为什么用静态工厂而非构造函数：避免与 record 自动生成的构造函数签名冲突，
     * 且 from(j) 语义比 new JobPostingResponse(...) 更清晰地表达"转换"意图。
     *
     * @param j 源实体对象，不能为 null
     * @return 填充完毕的响应 DTO
     */
    public static JobPostingResponse from(JobPosting j) {
        return new JobPostingResponse(
                // 主键
                j.getId(),                              // 主键 ID
                // 基础信息
                j.getTitle(),                           // 标题
                j.getCompany(),                         // 公司
                j.getCity(),                            // 城市
                // 分类：先取枚举名再取 label，null 时返回 null 保证前端不报错
                j.getCategory() != null ? j.getCategory().name() : null,        // 分类 code
                j.getCategory() != null ? j.getCategory().getLabel() : null,    // 分类中文
                // 经验：同上 null 安全处理
                j.getExperience() != null ? j.getExperience().name() : null,    // 经验 code
                j.getExperience() != null ? j.getExperience().getLabel() : null,// 经验中文
                // 学历：同上 null 安全处理
                j.getEducation() != null ? j.getEducation().name() : null,      // 学历 code
                j.getEducation() != null ? j.getEducation().getLabel() : null,  // 学历中文
                // 薪资
                j.getSalaryMin(),                       // 薪资下限
                j.getSalaryMax(),                       // 薪资上限
                j.getSalaryDesc(),                      // 薪资描述
                // 详情
                j.getSkills(),                          // 技能标签
                j.getResponsibilities(),                // 岗位职责
                j.getRequirements(),                    // 任职要求
                j.getBenefits(),                        // 福利
                j.getCompanyDesc(),                     // 公司简介
                // 时间与来源
                j.getPublishedDate(),                   // 发布日期
                j.getSource() != null ? j.getSource().name() : null,            // 来源 code
                j.getSource() != null ? j.getSource().getLabel() : null,        // 来源中文
                j.getExternalUrl(),                     // 外部链接
                // 发布人信息（本次新增）
                j.getPublisherName(),                   // 发布人姓名
                j.getContact(),                         // 联系方式
                // 系统字段
                j.getCreatedAt()                        // 创建时间
        );
    }
}

package com.example.java5.dto; // 声明包路径，归属 dto（数据传输对象层）

// ===== 枚举类型导入，用于约束字段取值范围 =====
import com.example.java5.model.JobCategory; // 岗位分类枚举
import com.example.java5.model.ExperienceLevel; // 经验要求枚举
import com.example.java5.model.EducationLevel; // 学历要求枚举

// ===== 时间类型导入 =====
import java.time.LocalDate; // 发布日期类型

/**
 * 招聘岗位创建/更新请求 DTO
 * <p>
 * 职责：承载前端发布岗位表单提交的数据，作为 Controller 入参，
 * 经 Service 层转换为 JobPosting 实体后入库。
 * </p>
 * 为什么用 record：Java 16+ 的 record 语法，自动生成构造函数/getter/equals/hashCode，
 * 相比传统 POJO 更简洁，且天然不可变，符合 DTO 只读语义。
 * </p>
 * 字段顺序与前端表单一致，便于对照排查。
 */
public record JobRequest(
        // ===== 基础信息 =====
        String title,              // 岗位标题，必填，如"高级 Java 后端工程师"
        String company,            // 公司名称，可空
        String city,               // 工作城市，可空
        JobCategory category,      // 岗位分类枚举，可空（前端默认选研发类）

        // ===== 要求条件 =====
        ExperienceLevel experience, // 经验要求枚举，可空
        EducationLevel education,   // 学历要求枚举，可空

        // ===== 薪资信息（单位：元，前端传 K 会乘 1000） =====
        Integer salaryMin,          // 薪资下限，可空（薪资面议时为 null）
        Integer salaryMax,          // 薪资上限，可空
        String salaryDesc,          // 薪资描述文本，如"20-40K"，便于直接展示

        // ===== 岗位详情 =====
        String skills,              // 技能标签，用 / 分隔，如"Java/Spring/MySQL"
        String responsibilities,    // 岗位职责描述，长文本
        String requirements,        // 任职要求描述，长文本
        String benefits,            // 福利待遇文本
        String companyDesc,         // 公司一句话简介
        LocalDate publishedDate,    // 发布日期，前端默认传当天

        // ===== 发布人信息（本次新增字段） =====
        String publisherName,       // 发布人姓名，前端必填校验
        String contact              // 联系方式（邮箱/电话/微信），前端必填校验
) {
    // record 体为空：所有逻辑由自动生成的访问器承担
    // 访问方式：req.title() / req.publisherName() 等，而非 getter
}

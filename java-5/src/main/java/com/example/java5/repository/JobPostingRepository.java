package com.example.java5.repository; // 声明包路径，归属 repository（数据访问层）

// ===== 枚举类型导入，用于查询参数类型约束 =====
import com.example.java5.model.EducationLevel;   // 学历枚举
import com.example.java5.model.ExperienceLevel;  // 经验枚举
import com.example.java5.model.JobCategory;      // 分类枚举
import com.example.java5.model.JobPosting;       // 招聘岗位实体
import com.example.java5.model.JobSource;        // 数据来源枚举

// ===== Spring Data JPA =====
import org.springframework.data.jpa.repository.JpaRepository; // JPA 基础仓库接口
import org.springframework.data.jpa.repository.Query;        // 自定义查询注解
import org.springframework.data.repository.query.Param;      // 命名参数绑定
import org.springframework.stereotype.Repository;            // 声明为仓库组件

// ===== JDK 容器 =====
import java.util.List; // 列表容器

/**
 * 招聘岗位数据访问仓库
 * <p>
 * 职责：封装岗位表的数据库操作，继承 JpaRepository 获得基础 CRUD，
 * 并扩展多条件搜索和去重查询方法。
 * </p>
 * 设计要点：
 * 1. 接口无需实现类，Spring Data JPA 运行时自动生成代理实现。
 * 2. 复杂查询用 @Query + JPQL，比方法名派生查询更灵活可读。
 * 3. 去重查询用方法名派生（existsBySourceAndExternalId），简洁直观。
 */
@Repository // 声明为 Spring Repository 组件
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /**
     * 多条件搜索岗位
     * <p>
     * 职责：根据关键词、城市、分类、经验、学历、薪资下限组合查询。
     * </p>
     * 查询逻辑：
     * - 所有条件均为可选（参数为 null 时跳过该条件）
     * - 关键词匹配标题、公司、技能三个字段（OR）
     * - 薪资下限匹配岗位薪资上限（j.salaryMax >= salaryMin）
     * - 按发布日期倒序、ID 倒序排列
     * </p>
     * 为什么用 JPQL 而非原生 SQL：JPQL 面向实体，数据库无关，
     * 后续迁移 MySQL 无需改查询。
     *
     * @param keyword    关键词（标题/公司/技能），可为 null
     * @param city       城市过滤，可为 null
     * @param category   分类过滤，可为 null
     * @param experience 经验过滤，可为 null
     * @param education  学历过滤，可为 null
     * @param salaryMin  薪资下限过滤，可为 null
     * @return 岗位实体列表
     */
    @Query("""
        SELECT j FROM JobPosting j WHERE
          (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:city IS NULL OR j.city = :city)
          AND (:category IS NULL OR j.category = :category)
          AND (:experience IS NULL OR j.experience = :experience)
          AND (:education IS NULL OR j.education = :education)
          AND (:salaryMin IS NULL OR j.salaryMax >= :salaryMin)
        ORDER BY j.publishedDate DESC, j.id DESC
        """) // JPQL 多条件查询，:xxx 为命名参数
    List<JobPosting> search(
            @Param("keyword") String keyword,              // 关键词参数
            @Param("city") String city,                    // 城市参数
            @Param("category") JobCategory category,       // 分类参数
            @Param("experience") ExperienceLevel experience, // 经验参数
            @Param("education") EducationLevel education,  // 学历参数
            @Param("salaryMin") Integer salaryMin          // 薪资下限参数
    );

    /**
     * 按来源和外部 ID 判断是否存在（去重查询）
     * <p>
     * 职责：V2EX 同步时判断某主题是否已入库，避免重复插入。
     * </p>
     * 为什么用 exists 而非 findBy：只需判断存在性，无需加载实体，性能更优。
     * 方法名派生查询：existsBy + 字段名 + And + 字段名，Spring 自动生成 SQL。
     *
     * @param source     数据来源
     * @param externalId 外部 ID
     * @return true=已存在，false=不存在
     */
    boolean existsBySourceAndExternalId(JobSource source, String externalId);
}

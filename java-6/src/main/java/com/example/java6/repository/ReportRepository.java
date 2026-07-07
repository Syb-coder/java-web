package com.example.java6.repository; // 定义 Repository 接口所在包，统一存放数据访问层组件

import com.example.java6.model.Report; // 导入举报记录实体类，对应数据库 report 表
import com.example.java6.model.ReportStatus; // 导入举报处理状态枚举（如待处理、处理中、已处理）
import com.example.java6.model.ReportType; // 导入举报类型枚举（如网络诈骗、信息泄露、恶意软件等）
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JPA 基础 Repository 接口，提供标准 CRUD 能力
import org.springframework.stereotype.Repository; // 导入 @Repository 注解，标记数据访问层组件

import java.util.List; // 导入 List 集合，用于返回多条举报记录

/**
 * 举报记录数据访问层
 * 负责举报记录的持久化访问，支持按状态/类型筛选、按提交时间排序、统计计数等业务场景
 */
@Repository // 标记为 Spring Repository 组件，由 Spring 容器管理为 Bean，并启用异常转换
public interface ReportRepository extends JpaRepository<Report, Long> { // 继承 JpaRepository，主键类型为 Long，自动获得标准 CRUD 方法

    /**
     * 按处理状态查询举报记录（按提交时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM report WHERE status = ? ORDER BY created_at DESC
     * 用于后台按处理状态筛选举报，最新提交的排在最前
     *
     * @param status 处理状态，为 null 时查询全部状态
     * @return 举报记录列表
     */
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status); // 派生查询：findBy Status 按状态过滤，OrderByCreatedAtDesc 按提交时间倒序

    /**
     * 查询全部举报记录（按提交时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM report ORDER BY created_at DESC
     * 用于后台展示全部举报记录，最新提交的排在最前
     *
     * @return 全部举报记录列表
     */
    List<Report> findAllByOrderByCreatedAtDesc(); // 派生查询：findAllBy 表示查询全部，OrderByCreatedAtDesc 按提交时间倒序

    /**
     * 按举报类型查询举报记录
     * Spring Data JPA 派生查询：SELECT * FROM report WHERE report_type = ? ORDER BY created_at DESC
     * 用于按举报类型分类查看，便于按问题类型归集分析
     *
     * @param type 举报类型
     * @return 举报记录列表
     */
    List<Report> findByReportTypeOrderByCreatedAtDesc(ReportType type); // 派生查询：findBy ReportType 按类型过滤，OrderByCreatedAtDesc 按提交时间倒序

    /**
     * 统计指定类型的举报数量
     * Spring Data JPA 派生查询：SELECT COUNT(*) FROM report WHERE report_type = ?
     * 用于首页或统计报表展示各类举报数量分布
     *
     * @param type 举报类型
     * @return 数量
     */
    long countByReportType(ReportType type); // 派生查询：countBy 表示计数，性能优于 findAll 后再 size()

    /**
     * 统计指定状态的举报数量
     * Spring Data JPA 派生查询：SELECT COUNT(*) FROM report WHERE status = ?
     * 用于统计待处理、已处理等状态的举报数量，便于运营监控
     *
     * @param status 处理状态
     * @return 数量
     */
    long countByStatus(ReportStatus status); // 派生查询：countBy 表示计数，直接在数据库层面聚合，避免全表加载
}

package com.example.java6.repository;

import com.example.java6.model.Report;
import com.example.java6.model.ReportStatus;
import com.example.java6.model.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 举报记录数据访问层
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 按处理状态查询举报记录（按提交时间倒序）
     *
     * @param status 处理状态，为 null 时查询全部状态
     * @return 举报记录列表
     */
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    /**
     * 查询全部举报记录（按提交时间倒序）
     *
     * @return 全部举报记录列表
     */
    List<Report> findAllByOrderByCreatedAtDesc();

    /**
     * 按举报类型查询举报记录
     *
     * @param type 举报类型
     * @return 举报记录列表
     */
    List<Report> findByReportTypeOrderByCreatedAtDesc(ReportType type);

    /**
     * 统计指定类型的举报数量
     *
     * @param type 举报类型
     * @return 数量
     */
    long countByReportType(ReportType type);

    /**
     * 统计指定状态的举报数量
     *
     * @param status 处理状态
     * @return 数量
     */
    long countByStatus(ReportStatus status);
}

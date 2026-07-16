package com.example.java11.repository;

import com.example.java11.model.Report;
import com.example.java11.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 举报记录数据访问层
 * <p>
 * 提供对 reports 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 按状态获取举报列表，按创建时间倒序
     *
     * @param status 举报处理状态
     * @return 举报列表
     */
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    /**
     * 查询用户提交的举报记录，按创建时间倒序
     *
     * @param reporterId 举报人 ID
     * @return 举报列表
     */
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
}

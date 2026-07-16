package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.ReportRequest;
import com.example.java11.dto.ReportResponse;
import com.example.java11.model.Report;
import com.example.java11.model.ReportReason;
import com.example.java11.model.ReportStatus;
import com.example.java11.model.TargetType;
import com.example.java11.model.User;
import com.example.java11.repository.ReportRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 举报服务
 * <p>
 * 负责用户举报的创建、查询与处理。
 * 举报状态流转：PENDING -> RESOLVED（成立）或 REJECTED（驳回）。
 * </p>
 */
@Service
public class ReportService {

    /** 举报数据访问层 */
    private final ReportRepository reportRepository;

    /** 用户数据访问层，用于查询举报人用户名 */
    private final UserRepository userRepository;

    /**
     * 构造器注入依赖
     *
     * @param reportRepository 举报数据访问层
     * @param userRepository   用户数据访问层
     */
    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建举报
     *
     * @param reporterId 举报人 ID
     * @param req        举报请求 DTO
     * @return 创建后的举报响应 DTO
     */
    @Transactional
    public ReportResponse createReport(Long reporterId, ReportRequest req) {
        TargetType targetType = parseTargetType(req.getTargetType());
        ReportReason reason = parseReason(req.getReason());
        Report report = new Report(reporterId, targetType, req.getTargetId(), reason);
        report.setDescription(req.getDescription());
        Report saved = reportRepository.save(report);
        return toResponse(saved);
    }

    /**
     * 获取待处理举报列表
     *
     * @return 待处理举报响应列表
     */
    public List<ReportResponse> getPendingReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有举报列表
     *
     * @return 全部举报响应列表
     */
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户提交的举报列表
     *
     * @param reporterId 举报人 ID
     * @return 用户举报历史
     */
    public List<ReportResponse> getReportsByUser(Long reporterId) {
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(reporterId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 处理举报（成立）
     *
     * @param reportId    举报 ID
     * @param handleRemark 处理备注
     */
    @Transactional
    public void resolveReport(Long reportId, String handleRemark) {
        Optional<Report> optional = reportRepository.findById(reportId);
        if (optional.isEmpty()) {
            throw new RuntimeException("举报记录不存在");
        }
        Report report = optional.get();
        report.setStatus(ReportStatus.RESOLVED);
        report.setHandleRemark(handleRemark);
        reportRepository.save(report);
    }

    /**
     * 驳回举报（不成立）
     *
     * @param reportId    举报 ID
     * @param handleRemark 处理备注
     */
    @Transactional
    public void rejectReport(Long reportId, String handleRemark) {
        Optional<Report> optional = reportRepository.findById(reportId);
        if (optional.isEmpty()) {
            throw new RuntimeException("举报记录不存在");
        }
        Report report = optional.get();
        report.setStatus(ReportStatus.REJECTED);
        report.setHandleRemark(handleRemark);
        reportRepository.save(report);
    }

    /**
     * 解析目标类型字符串为枚举
     *
     * @param targetType 目标类型字符串
     * @return 目标类型枚举
     */
    private TargetType parseTargetType(String targetType) {
        try {
            return TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }
    }

    /**
     * 解析举报理由字符串为枚举
     *
     * @param reason 举报理由字符串
     * @return 举报理由枚举
     */
    private ReportReason parseReason(String reason) {
        try {
            return ReportReason.valueOf(reason.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的举报理由: " + reason);
        }
    }

    /**
     * 实体转 DTO
     *
     * @param report 举报实体
     * @return 举报响应 DTO，实体为 null 时返回 null
     */
    private ReportResponse toResponse(Report report) {
        if (report == null) {
            return null;
        }
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setReporterId(report.getReporterId());
        response.setTargetType(report.getTargetType() != null ? report.getTargetType().name() : null);
        response.setTargetId(report.getTargetId());
        response.setReason(report.getReason() != null ? report.getReason().name() : null);
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus() != null ? report.getStatus().name() : null);
        response.setHandleRemark(report.getHandleRemark());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdateTime(report.getUpdateTime());

        // 查询举报人用户名
        if (report.getReporterId() != null) {
            Optional<User> reporter = userRepository.findById(report.getReporterId());
            reporter.ifPresent(user -> response.setReporterName(user.getNickname() != null ? user.getNickname() : user.getUsername()));
        }
        return response;
    }
}

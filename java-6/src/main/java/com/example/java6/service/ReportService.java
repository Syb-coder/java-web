package com.example.java6.service;

import com.example.java6.dto.ReportHandleRequest;
import com.example.java6.dto.ReportRequest;
import com.example.java6.dto.ReportResponse;
import com.example.java6.model.Report;
import com.example.java6.model.ReportStatus;
import com.example.java6.model.ReportType;
import com.example.java6.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 举报记录业务服务
 *
 * <p>封装举报记录的提交、查询、处置以及统计分析等业务逻辑。</p>
 */
@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询举报列表（可按状态过滤）
     *
     * @param status 处理状态，为 null 时返回全部
     * @return 举报响应列表
     */
    public List<ReportResponse> list(ReportStatus status) {
        List<Report> list = (status == null)
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByStatusOrderByCreatedAtDesc(status);
        return list.stream().map(ReportResponse::from).collect(Collectors.toList());
    }

    /**
     * 获取举报详情
     *
     * @param id 举报 ID
     * @return 举报响应，不存在返回 null
     */
    public ReportResponse get(Long id) {
        return repository.findById(id).map(ReportResponse::from).orElse(null);
    }

    /**
     * 提交举报
     *
     * @param req 举报请求
     * @return 提交后的举报响应
     */
    public ReportResponse submit(ReportRequest req) {
        Report r = new Report();
        r.setReportType(req.reportType());
        r.setTargetUrl(req.targetUrl());
        r.setDescription(req.description());
        r.setReporterName(req.reporterName());
        r.setReporterContact(req.reporterContact());
        return ReportResponse.from(repository.save(r));
    }

    /**
     * 处置举报（后台管理用）
     *
     * @param id  举报 ID
     * @param req 处置请求
     * @return 处置后的举报响应，不存在返回 null
     */
    public ReportResponse handle(Long id, ReportHandleRequest req) {
        return repository.findById(id).map(r -> {
            r.setStatus(req.status());
            r.setHandleNote(req.handleNote());
            // 状态流转到终态时记录处置时间
            if (req.status() == ReportStatus.RESOLVED || req.status() == ReportStatus.IGNORED) {
                r.setHandledAt(LocalDateTime.now());
            }
            return ReportResponse.from(repository.save(r));
        }).orElse(null);
    }

    /**
     * 删除举报记录
     *
     * @param id 举报 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 举报数据统计
     *
     * @return 统计结果（总数、按类型、按状态）
     */
    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", repository.count());

        Map<String, Long> byType = new HashMap<>();
        for (ReportType type : ReportType.values()) {
            byType.put(type.name(), repository.countByReportType(type));
        }
        stats.put("byType", byType);

        Map<String, Long> byStatus = new HashMap<>();
        for (ReportStatus status : ReportStatus.values()) {
            byStatus.put(status.name(), repository.countByStatus(status));
        }
        stats.put("byStatus", byStatus);

        return stats;
    }
}

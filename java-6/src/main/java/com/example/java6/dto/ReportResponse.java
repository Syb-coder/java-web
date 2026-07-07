package com.example.java6.dto;

import com.example.java6.model.Report;
import com.example.java6.model.ReportStatus;
import com.example.java6.model.ReportType;

import java.time.LocalDateTime;

/**
 * 举报记录展示响应
 *
 * @param id              主键 ID
 * @param reportType      举报类型
 * @param targetUrl       举报对象链接
 * @param description     举报描述
 * @param reporterName    举报人姓名
 * @param reporterContact 举报人联系方式
 * @param status          处理状态
 * @param handleNote      处置备注
 * @param createdAt       举报提交时间
 * @param handledAt       处置时间
 */
public record ReportResponse(
        Long id,
        ReportType reportType,
        String targetUrl,
        String description,
        String reporterName,
        String reporterContact,
        ReportStatus status,
        String handleNote,
        LocalDateTime createdAt,
        LocalDateTime handledAt
) {

    /**
     * 从实体构造响应对象
     *
     * @param r 举报记录实体
     * @return 响应对象
     */
    public static ReportResponse from(Report r) {
        return new ReportResponse(
                r.getId(),
                r.getReportType(),
                r.getTargetUrl(),
                r.getDescription(),
                r.getReporterName(),
                r.getReporterContact(),
                r.getStatus(),
                r.getHandleNote(),
                r.getCreatedAt(),
                r.getHandledAt()
        );
    }
}

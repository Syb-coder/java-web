package com.example.java6.dto;

import com.example.java6.model.ReportType;

/**
 * 举报提交请求
 *
 * @param reportType      举报类型
 * @param targetUrl       举报对象链接
 * @param description     举报描述
 * @param reporterName    举报人姓名
 * @param reporterContact 举报人联系方式
 */
public record ReportRequest(
        ReportType reportType,
        String targetUrl,
        String description,
        String reporterName,
        String reporterContact
) {
}

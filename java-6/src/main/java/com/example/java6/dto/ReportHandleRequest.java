package com.example.java6.dto;

import com.example.java6.model.ReportStatus;

/**
 * 举报处置请求（后台管理用）
 *
 * @param status     处理状态
 * @param handleNote 处置备注
 */
public record ReportHandleRequest(
        ReportStatus status,
        String handleNote
) {
}

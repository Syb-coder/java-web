package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.ReportRequest;
import com.example.java11.dto.ReportResponse;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.ReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报控制器
 * <p>
 * 提供用户举报创建与举报历史查询接口。
 * 所有接口均需登录，从 session 获取举报人 ID。
 * </p>
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    /** 举报服务 */
    private final ReportService reportService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param reportService 举报服务
     * @param authService   认证服务
     */
    public ReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    /**
     * 创建举报
     *
     * @param req     举报请求 DTO
     * @param session HTTP 会话
     * @return 包含举报响应的统一包装结果，未登录返回 error
     */
    @PostMapping
    public ApiResponse createReport(@Valid @RequestBody ReportRequest req, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        ReportResponse response = reportService.createReport(currentUser.getId(), req);
        return ApiResponse.success("举报已提交", response);
    }

    /**
     * 获取当前用户提交的举报列表
     *
     * @param session HTTP 会话
     * @return 举报响应列表，未登录返回 error
     */
    @GetMapping("/user")
    public ApiResponse getReportsByUser(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<ReportResponse> list = reportService.getReportsByUser(currentUser.getId());
        return ApiResponse.success(list);
    }
}

package com.example.java6.controller;

import com.example.java6.dto.ReportHandleRequest;
import com.example.java6.dto.ReportRequest;
import com.example.java6.dto.ReportResponse;
import com.example.java6.model.ReportStatus;
import com.example.java6.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 举报记录 REST 控制器
 *
 * <p>提供举报提交、查询、处置以及统计接口。
 * 提交接口供前台公众使用，处置与删除接口供后台管理使用。</p>
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 查询举报列表（可按状态过滤）
     *
     * @param status 处理状态（可选）
     * @return 举报列表
     */
    @GetMapping
    public List<ReportResponse> list(@RequestParam(required = false) ReportStatus status) {
        return reportService.list(status);
    }

    /**
     * 举报数据统计
     *
     * @return 统计结果
     */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return reportService.stats();
    }

    /**
     * 获取举报详情
     *
     * @param id 举报 ID
     * @return 举报详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> get(@PathVariable Long id) {
        ReportResponse resp = reportService.get(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 提交举报（前台公众用）
     *
     * @param req 举报请求
     * @return 提交后的举报
     */
    @PostMapping
    public ReportResponse submit(@RequestBody ReportRequest req) {
        return reportService.submit(req);
    }

    /**
     * 处置举报（后台管理用）
     *
     * @param id  举报 ID
     * @param req 处置请求
     * @return 处置后的举报
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> handle(@PathVariable Long id, @RequestBody ReportHandleRequest req) {
        ReportResponse resp = reportService.handle(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除举报记录
     *
     * @param id 举报 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reportService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

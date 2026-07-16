package com.example.demo.controller;

import com.example.demo.entity.Approval;
import com.example.demo.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 审批管理控制器：提供审批 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/approvals      查询所有审批
 * - POST   /api/approvals      创建审批
 * - PUT    /api/approvals/{id} 更新审批
 * - DELETE /api/approvals/{id} 删除审批
 * </p>
 */
@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * 查询所有审批
     *
     * @return 审批列表
     */
    @GetMapping
    public List<Approval> findAll() {
        return approvalService.findAll();
    }

    /**
     * 创建审批
     *
     * @param approval 审批信息
     * @return 200 + 创建后的审批信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Approval approval) {
        return ResponseEntity.ok(approvalService.create(approval));
    }

    /**
     * 更新审批信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 审批不存在时返回 400。
     * </p>
     *
     * @param id 审批 ID
     * @param approval 更新数据
     * @return 200 + 更新后的审批信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Approval approval) {
        Approval updated = approvalService.update(id, approval);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "审批记录不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除审批
     *
     * @param id 审批 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!approvalService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "审批记录不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

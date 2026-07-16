package com.example.demo.controller;

import com.example.demo.entity.OperationLog;
import com.example.demo.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 操作记录控制器：提供操作记录查询接口
 * <p>
 * RESTful 设计：
 * - GET /api/logs 查询所有操作记录
 * </p>
 */
@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Autowired
    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 查询所有操作记录
     *
     * @return 操作记录列表
     */
    @GetMapping
    public List<OperationLog> findAll() {
        return operationLogService.findAll();
    }
}

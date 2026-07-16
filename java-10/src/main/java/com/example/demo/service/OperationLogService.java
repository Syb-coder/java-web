package com.example.demo.service;

import com.example.demo.entity.OperationLog;
import com.example.demo.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 操作记录业务逻辑层
 * <p>
 * 职责：封装操作记录的查询与创建业务。
 * 为什么直接返回 OperationLog 实体：操作记录无敏感字段，无需 DTO 转换。
 * 为什么 time 用格式化字符串而非 LocalDateTime：与前端展示格式统一，
 * 避免前端再做时区转换。
 * </p>
 */
@Service
public class OperationLogService {

    /** 时间格式化器：统一操作记录时间格式为 yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OperationLogRepository operationLogRepository;

    /**
     * 构造器注入依赖
     *
     * @param operationLogRepository 操作记录数据访问层
     */
    @Autowired
    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 查询所有操作记录
     *
     * @return 操作记录列表
     */
    public List<OperationLog> findAll() {
        return operationLogRepository.findAll();
    }

    /**
     * 创建操作记录
     * <p>
     * time 字段取当前时间并格式化为字符串，便于前端直接展示。
     * createdAt 由 Hibernate @CreationTimestamp 自动填充，无需手动设置。
     * </p>
     *
     * @param action 操作动作
     * @param detail 操作详情
     * @return 创建后的操作记录（含自增 ID）
     */
    public OperationLog create(String action, String detail) {
        OperationLog log = new OperationLog();
        log.setAction(action);
        log.setDetail(detail);
        log.setTime(LocalDateTime.now().format(TIME_FORMATTER));
        return operationLogRepository.save(log);
    }
}

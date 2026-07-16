package com.example.java12.service;  // 服务层包，存放业务逻辑

import com.example.java12.model.OperateLog;  // 操作日志实体
import com.example.java12.repository.OperateLogRepository;  // 操作日志数据访问层
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.stereotype.Service;  // Service 注解

/**
 * 操作日志服务
 * <p>
 * 记录管理员/版主的后台操作行为，提供日志查询功能。
 * 被 PostService、PlateService、UserService 等调用。
 * </p>
 */
@Service
public class OperateLogService {

    /** 操作日志数据访问层 */
    private final OperateLogRepository operateLogRepository;

    /**
     * 构造器注入
     *
     * @param operateLogRepository 操作日志数据访问层
     */
    public OperateLogService(OperateLogRepository operateLogRepository) {
        this.operateLogRepository = operateLogRepository;
    }

    /**
     * 记录操作日志
     *
     * @param adminId   操作人 ID
     * @param adminName 操作人昵称
     * @param action    操作类型（如"删除帖子"）
     * @param target    操作对象描述
     * @param ip        操作 IP
     */
    public void log(Long adminId, String adminName, String action, String target, String ip) {
        OperateLog log = new OperateLog(adminId, adminName, action, target, ip);
        operateLogRepository.save(log);
    }

    /**
     * 分页查询操作日志（按时间倒序）
     *
     * @param pageable 分页参数
     * @return 操作日志分页列表
     */
    public Page<OperateLog> findAll(Pageable pageable) {
        return operateLogRepository.findAllByOrderByCreateTimeDesc(pageable);
    }
}

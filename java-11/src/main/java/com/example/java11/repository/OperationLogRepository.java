package com.example.java11.repository;

import com.example.java11.model.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作日志数据访问层
 * <p>
 * 提供对 operation_logs 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * 获取所有操作日志，按创建时间倒序
     *
     * @return 操作日志列表
     */
    List<OperationLog> findAllByOrderByCreatedAtDesc();

    /**
     * 按操作人查询操作日志，按创建时间倒序
     *
     * @param operatorId 操作人 ID
     * @return 操作日志列表
     */
    List<OperationLog> findByOperatorIdOrderByCreatedAtDesc(Long operatorId);

    /**
     * 按操作动作关键词查询日志，按创建时间倒序
     *
     * @param action 操作动作关键词
     * @return 操作日志列表
     */
    List<OperationLog> findByActionContainingOrderByCreatedAtDesc(String action);
}

package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.OperateLog;  // 操作日志实体
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

/**
 * 操作日志数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按时间倒序分页查询操作日志的方法（后台审计日志展示）。
 * </p>
 */
@Repository
public interface OperateLogRepository extends JpaRepository<OperateLog, Long> {

    /**
     * 分页查询全部操作日志（按操作时间倒序）
     *
     * @param pageable 分页参数
     * @return 操作日志分页列表
     */
    Page<OperateLog> findAllByOrderByCreateTimeDesc(Pageable pageable);
}

package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.Transaction; // 引入交易流水实体，对应 transactions 表
import com.example.java9.model.TransactionType; // 引入交易类型枚举（RECHARGE/WITHDRAW/INVEST 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 交易流水 Repository
 */
@Repository // 标识为持久层 Bean
public interface TransactionRepository extends JpaRepository<Transaction, Long> { // 继承 JPA，主键 Long

    /** 根据账户 ID 查询流水（按时间倒序） */
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId); // 用户/商户查看账户明细，最新流水排在最前

    /** 根据账户 ID 和账户类型查询 */
    List<Transaction> findByAccountIdAndAccountTypeOrderByCreatedAtDesc(Long accountId, String accountType); // 区分 USER/MERCHANT 账户类型，避免跨主体混淆

    /** 根据交易类型查询 */
    List<Transaction> findByType(TransactionType type); // 风控/对账按交易类型聚合分析（如统计当日所有提现）
}

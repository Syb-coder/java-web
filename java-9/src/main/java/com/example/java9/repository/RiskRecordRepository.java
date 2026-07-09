package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.RiskRecord; // 引入风控记录实体，对应 risk_records 表
import com.example.java9.model.RiskLevel; // 引入风险等级枚举（LOW/MEDIUM/HIGH 等）
import com.example.java9.model.RiskStatus; // 引入风控状态枚举（PENDING/HANDLED/REJECTED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 风控记录 Repository
 */
@Repository // 标识为持久层 Bean
public interface RiskRecordRepository extends JpaRepository<RiskRecord, Long> { // 继承 JPA，主键 Long

    /** 根据状态查询（风控专员待处理列表） */
    List<RiskRecord> findByStatus(RiskStatus status); // 风控后台拉取待处理(PENDING)工单，按状态分桶处理

    /** 根据目标类型和目标 ID 查询 */
    List<RiskRecord> findByTargetTypeAndTargetId(String targetType, Long targetId); // 查询某用户/某订单/某商户的完整风险历史（targetType 区分主体）

    /** 根据风险等级查询 */
    List<RiskRecord> findByRiskLevel(RiskLevel riskLevel); // 风控主管按等级筛选高风险事件，优先处置 HIGH 级别
}

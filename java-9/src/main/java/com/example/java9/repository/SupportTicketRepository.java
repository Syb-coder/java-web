package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.SupportTicket; // 引入客服工单实体，对应 support_tickets 表
import com.example.java9.model.TicketStatus; // 引入工单状态枚举（OPEN/REPLIED/CLOSED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 客服工单 Repository
 */
@Repository // 标识为持久层 Bean
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> { // 继承 JPA，主键 Long

    /** 根据用户 ID 查询工单 */
    List<SupportTicket> findByUserId(Long userId); // C 端用户查看自己提交的全部工单历史

    /** 根据状态查询工单（客服待处理列表） */
    List<SupportTicket> findByStatus(TicketStatus status); // 客服后台拉取待回复(OPEN)工单队列，按状态分派处理
}

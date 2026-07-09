package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.SupportTicket;  // 导入工单实体
import com.example.java9.model.TicketStatus;  // 导入工单状态枚举（OPEN/REPLIED/CLOSED）
import com.example.java9.repository.SupportTicketRepository;  // 导入工单 Repository
import com.example.java9.repository.UserRepository;  // 导入用户 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.time.LocalDateTime;  // 导入时间类
import java.util.List;  // 导入集合 List

/**
 * 客服工单服务
 * <p>
 * 负责用户咨询/问题反馈工单的创建、回复、关闭及查询。
 * 工单流转：OPEN（待处理）→ REPLIED（已回复）→ CLOSED（已关闭）。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class TicketService {

    private final SupportTicketRepository supportTicketRepository;  // 工单 Repository，final 保证不可变
    private final UserRepository userRepository;  // 用户 Repository，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public TicketService(SupportTicketRepository supportTicketRepository, UserRepository userRepository) {
        this.supportTicketRepository = supportTicketRepository;  // 注入工单 Repository
        this.userRepository = userRepository;  // 注入用户 Repository
    }

    /**
     * 用户创建工单
     * <p>
     * 校验用户存在后创建工单，初始状态为 OPEN。
     * </p>
     *
     * @param userId      用户 ID
     * @param title       工单标题
     * @param description 问题描述
     * @return 已保存的工单实体
     * @throws IllegalArgumentException 用户不存在
     */
    @Transactional  // 声明事务边界：用户校验与工单创建原子化
    public SupportTicket create(Long userId, String title, String description) {
        // 校验用户存在，避免无效用户创建工单
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户，不存在则抛异常
        SupportTicket ticket = new SupportTicket(userId, title, description);  // 构造工单实体，初始状态 OPEN
        return supportTicketRepository.save(ticket);  // 持久化并返回带 ID 的工单
    }

    /**
     * 客服回复工单
     * <p>
     * 设置回复内容、回复人、回复时间，状态置为 REPLIED。
     * </p>
     *
     * @param ticketId  工单 ID
     * @param reply     回复内容
     * @param repliedBy 回复人（管理员用户名）
     * @return 更新后的工单实体
     * @throws IllegalArgumentException 工单不存在
     */
    @Transactional  // 声明事务边界：回复内容、回复人、回复时间、状态更新原子化
    public SupportTicket reply(Long ticketId, String reply, String repliedBy) {
        // 1. 查找工单
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("工单不存在"));  // 查询工单，不存在则抛异常
        // 2. 设置回复内容、回复人、回复时间，状态置为已回复
        ticket.setReply(reply);  // 写入回复内容
        ticket.setRepliedBy(repliedBy);  // 记录回复人（管理员用户名）
        ticket.setRepliedAt(LocalDateTime.now());  // 记录回复时间
        ticket.setStatus(TicketStatus.REPLIED);  // 状态流转为已回复
        return supportTicketRepository.save(ticket);  // 持久化并返回
    }

    /**
     * 关闭工单
     * <p>
     * 将工单状态置为 CLOSED，表示问题已解决或用户主动关闭。
     * </p>
     *
     * @param ticketId 工单 ID
     * @return 更新后的工单实体
     * @throws IllegalArgumentException 工单不存在
     */
    @Transactional  // 声明事务边界：工单状态更新原子化
    public SupportTicket close(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("工单不存在"));  // 查询工单，不存在则抛异常
        ticket.setStatus(TicketStatus.CLOSED);  // 状态流转为已关闭，终止工单生命周期
        return supportTicketRepository.save(ticket);  // 持久化并返回
    }

    /**
     * 查询用户工单
     *
     * @param userId 用户 ID
     * @return 该用户的工单列表
     */
    public List<SupportTicket> findByUserId(Long userId) {
        return supportTicketRepository.findByUserId(userId);  // 按用户 ID 查询工单
    }

    /**
     * 查询所有工单（客服后台用）
     *
     * @return 全部工单列表
     */
    public List<SupportTicket> findAll() {
        return supportTicketRepository.findAll();  // 查询全部工单
    }

    /**
     * 根据状态查询工单
     *
     * @param status 工单状态
     * @return 符合状态的工单列表
     */
    public List<SupportTicket> findByStatus(TicketStatus status) {
        return supportTicketRepository.findByStatus(status);  // 按状态查询工单
    }

    /**
     * 根据 ID 查询工单
     *
     * @param id 工单 ID
     * @return 工单实体
     * @throws IllegalArgumentException 工单不存在
     */
    public SupportTicket findById(Long id) {
        return supportTicketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("工单不存在"));  // 查询工单，不存在则抛异常
    }
}

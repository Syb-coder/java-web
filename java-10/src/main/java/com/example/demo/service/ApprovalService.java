package com.example.demo.service;

import com.example.demo.entity.Approval;
import com.example.demo.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审批业务逻辑层
 * <p>
 * 职责：封装审批相关 CRUD 业务操作。
 * 为什么直接返回 Approval 实体：审批信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    /**
     * 构造器注入依赖
     *
     * @param approvalRepository 审批数据访问层
     */
    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    /**
     * 查询所有审批
     *
     * @return 审批列表
     */
    public List<Approval> findAll() {
        return approvalRepository.findAll();
    }

    /**
     * 创建审批
     *
     * @param approval 审批信息
     * @return 创建后的审批信息（含自增 ID）
     */
    public Approval create(Approval approval) {
        return approvalRepository.save(approval);
    }

    /**
     * 更新审批信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 审批 ID
     * @param approval 更新数据
     * @return 更新后的审批信息，审批不存在时返回 null
     */
    public Approval update(Long id, Approval approval) {
        Approval existing = approvalRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (approval.getStudentNo() != null) {
            existing.setStudentNo(approval.getStudentNo());
        }
        if (approval.getType() != null) {
            existing.setType(approval.getType());
        }
        if (approval.getReason() != null) {
            existing.setReason(approval.getReason());
        }
        if (approval.getStatus() != null) {
            existing.setStatus(approval.getStatus());
        }
        if (approval.getOpinion() != null) {
            existing.setOpinion(approval.getOpinion());
        }
        return approvalRepository.save(existing);
    }

    /**
     * 删除审批
     *
     * @param id 审批 ID
     * @return 删除成功返回 true，审批不存在返回 false
     */
    public boolean delete(Long id) {
        if (!approvalRepository.existsById(id)) {
            return false;
        }
        approvalRepository.deleteById(id);
        return true;
    }
}

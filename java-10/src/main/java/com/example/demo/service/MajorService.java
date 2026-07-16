package com.example.demo.service;

import com.example.demo.entity.Major;
import com.example.demo.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专业业务逻辑层
 * <p>
 * 职责：封装专业相关 CRUD 业务操作。
 * 为什么直接返回 Major 实体：专业信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class MajorService {

    private final MajorRepository majorRepository;

    /**
     * 构造器注入依赖
     *
     * @param majorRepository 专业数据访问层
     */
    @Autowired
    public MajorService(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    /**
     * 查询所有专业
     *
     * @return 专业列表
     */
    public List<Major> findAll() {
        return majorRepository.findAll();
    }

    /**
     * 创建专业
     *
     * @param major 专业信息
     * @return 创建后的专业信息（含自增 ID）
     */
    public Major create(Major major) {
        return majorRepository.save(major);
    }

    /**
     * 更新专业信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 专业 ID
     * @param major 更新数据
     * @return 更新后的专业信息，专业不存在时返回 null
     */
    public Major update(Long id, Major major) {
        Major existing = majorRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (major.getName() != null) {
            existing.setName(major.getName());
        }
        if (major.getCollege() != null) {
            existing.setCollege(major.getCollege());
        }
        if (major.getRemark() != null) {
            existing.setRemark(major.getRemark());
        }
        return majorRepository.save(existing);
    }

    /**
     * 删除专业
     *
     * @param id 专业 ID
     * @return 删除成功返回 true，专业不存在返回 false
     */
    public boolean delete(Long id) {
        if (!majorRepository.existsById(id)) {
            return false;
        }
        majorRepository.deleteById(id);
        return true;
    }
}

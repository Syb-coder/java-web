package com.example.demo.service;

import com.example.demo.entity.ClassInfo;
import com.example.demo.repository.ClassInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级业务逻辑层
 * <p>
 * 职责：封装班级相关 CRUD 业务操作。
 * 为什么直接返回 ClassInfo 实体：班级信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class ClassInfoService {

    private final ClassInfoRepository classInfoRepository;

    /**
     * 构造器注入依赖
     *
     * @param classInfoRepository 班级数据访问层
     */
    @Autowired
    public ClassInfoService(ClassInfoRepository classInfoRepository) {
        this.classInfoRepository = classInfoRepository;
    }

    /**
     * 查询所有班级
     *
     * @return 班级列表
     */
    public List<ClassInfo> findAll() {
        return classInfoRepository.findAll();
    }

    /**
     * 创建班级
     *
     * @param classInfo 班级信息
     * @return 创建后的班级信息（含自增 ID）
     */
    public ClassInfo create(ClassInfo classInfo) {
        return classInfoRepository.save(classInfo);
    }

    /**
     * 更新班级信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 班级 ID
     * @param classInfo 更新数据
     * @return 更新后的班级信息，班级不存在时返回 null
     */
    public ClassInfo update(Long id, ClassInfo classInfo) {
        ClassInfo existing = classInfoRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (classInfo.getName() != null) {
            existing.setName(classInfo.getName());
        }
        if (classInfo.getCapacity() != null) {
            existing.setCapacity(classInfo.getCapacity());
        }
        if (classInfo.getTeacherType() != null) {
            existing.setTeacherType(classInfo.getTeacherType());
        }
        return classInfoRepository.save(existing);
    }

    /**
     * 删除班级
     *
     * @param id 班级 ID
     * @return 删除成功返回 true，班级不存在返回 false
     */
    public boolean delete(Long id) {
        if (!classInfoRepository.existsById(id)) {
            return false;
        }
        classInfoRepository.deleteById(id);
        return true;
    }
}

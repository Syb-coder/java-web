package com.example.demo.service;

import com.example.demo.entity.Teacher;
import com.example.demo.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师业务逻辑层
 * <p>
 * 职责：封装教师相关 CRUD 业务操作。
 * 为什么直接返回 Teacher 实体：教师信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    /**
     * 构造器注入依赖
     *
     * @param teacherRepository 教师数据访问层
     */
    @Autowired
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    /**
     * 查询所有教师
     *
     * @return 教师列表
     */
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    /**
     * 创建教师
     *
     * @param teacher 教师信息
     * @return 创建后的教师信息（含自增 ID）
     */
    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    /**
     * 更新教师信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 教师 ID
     * @param teacher 更新数据
     * @return 更新后的教师信息，教师不存在时返回 null
     */
    public Teacher update(Long id, Teacher teacher) {
        Teacher existing = teacherRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (teacher.getName() != null) {
            existing.setName(teacher.getName());
        }
        if (teacher.getTeacherNo() != null) {
            existing.setTeacherNo(teacher.getTeacherNo());
        }
        if (teacher.getGender() != null) {
            existing.setGender(teacher.getGender());
        }
        if (teacher.getAge() != null) {
            existing.setAge(teacher.getAge());
        }
        if (teacher.getPhone() != null) {
            existing.setPhone(teacher.getPhone());
        }
        return teacherRepository.save(existing);
    }

    /**
     * 删除教师
     *
     * @param id 教师 ID
     * @return 删除成功返回 true，教师不存在返回 false
     */
    public boolean delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            return false;
        }
        teacherRepository.deleteById(id);
        return true;
    }
}

package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生业务逻辑层
 * <p>
 * 职责：封装学生相关 CRUD 业务操作。
 * 为什么直接返回 Student 实体：学生信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * 构造器注入依赖
     *
     * @param studentRepository 学生数据访问层
     */
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * 查询所有学生
     *
     * @return 学生列表
     */
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    /**
     * 创建学生
     *
     * @param student 学生信息
     * @return 创建后的学生信息（含自增 ID）
     */
    public Student create(Student student) {
        return studentRepository.save(student);
    }

    /**
     * 更新学生信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 学生 ID
     * @param student 更新数据
     * @return 更新后的学生信息，学生不存在时返回 null
     */
    public Student update(Long id, Student student) {
        Student existing = studentRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (student.getName() != null) {
            existing.setName(student.getName());
        }
        if (student.getStudentNo() != null) {
            existing.setStudentNo(student.getStudentNo());
        }
        if (student.getGender() != null) {
            existing.setGender(student.getGender());
        }
        if (student.getAge() != null) {
            existing.setAge(student.getAge());
        }
        if (student.getClassName() != null) {
            existing.setClassName(student.getClassName());
        }
        if (student.getPhone() != null) {
            existing.setPhone(student.getPhone());
        }
        return studentRepository.save(existing);
    }

    /**
     * 删除学生
     *
     * @param id 学生 ID
     * @return 删除成功返回 true，学生不存在返回 false
     */
    public boolean delete(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }
}

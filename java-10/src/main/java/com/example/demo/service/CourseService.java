package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程业务逻辑层
 * <p>
 * 职责：封装课程相关 CRUD 业务操作。
 * 为什么直接返回 Course 实体：课程信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * 构造器注入依赖
     *
     * @param courseRepository 课程数据访问层
     */
    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * 查询所有课程
     *
     * @return 课程列表
     */
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    /**
     * 创建课程
     *
     * @param course 课程信息
     * @return 创建后的课程信息（含自增 ID）
     */
    public Course create(Course course) {
        return courseRepository.save(course);
    }

    /**
     * 更新课程信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 课程 ID
     * @param course 更新数据
     * @return 更新后的课程信息，课程不存在时返回 null
     */
    public Course update(Long id, Course course) {
        Course existing = courseRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (course.getName() != null) {
            existing.setName(course.getName());
        }
        if (course.getRemark() != null) {
            existing.setRemark(course.getRemark());
        }
        return courseRepository.save(existing);
    }

    /**
     * 删除课程
     *
     * @param id 课程 ID
     * @return 删除成功返回 true，课程不存在返回 false
     */
    public boolean delete(Long id) {
        if (!courseRepository.existsById(id)) {
            return false;
        }
        courseRepository.deleteById(id);
        return true;
    }
}

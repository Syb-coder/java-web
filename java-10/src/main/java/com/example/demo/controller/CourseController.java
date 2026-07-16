package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器：提供课程 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/courses      查询所有课程
 * - POST   /api/courses      创建课程
 * - PUT    /api/courses/{id} 更新课程
 * - DELETE /api/courses/{id} 删除课程
 * </p>
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 查询所有课程
     *
     * @return 课程列表
     */
    @GetMapping
    public List<Course> findAll() {
        return courseService.findAll();
    }

    /**
     * 创建课程
     *
     * @param course 课程信息
     * @return 200 + 创建后的课程信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.create(course));
    }

    /**
     * 更新课程信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 课程不存在时返回 400。
     * </p>
     *
     * @param id 课程 ID
     * @param course 更新数据
     * @return 200 + 更新后的课程信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Course course) {
        Course updated = courseService.update(id, course);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "课程不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除课程
     *
     * @param id 课程 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!courseService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "课程不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

package com.example.demo.controller;

import com.example.demo.entity.Teacher;
import com.example.demo.service.TeacherService;
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
 * 教师管理控制器：提供教师 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/teachers      查询所有教师
 * - POST   /api/teachers      创建教师
 * - PUT    /api/teachers/{id} 更新教师
 * - DELETE /api/teachers/{id} 删除教师
 * </p>
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * 查询所有教师
     *
     * @return 教师列表
     */
    @GetMapping
    public List<Teacher> findAll() {
        return teacherService.findAll();
    }

    /**
     * 创建教师
     *
     * @param teacher 教师信息
     * @return 200 + 创建后的教师信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.create(teacher));
    }

    /**
     * 更新教师信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 教师不存在时返回 400。
     * </p>
     *
     * @param id 教师 ID
     * @param teacher 更新数据
     * @return 200 + 更新后的教师信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        Teacher updated = teacherService.update(id, teacher);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "教师不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除教师
     *
     * @param id 教师 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!teacherService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "教师不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

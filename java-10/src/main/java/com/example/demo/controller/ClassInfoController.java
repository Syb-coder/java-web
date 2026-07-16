package com.example.demo.controller;

import com.example.demo.entity.ClassInfo;
import com.example.demo.service.ClassInfoService;
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
 * 班级管理控制器：提供班级 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/classes      查询所有班级
 * - POST   /api/classes      创建班级
 * - PUT    /api/classes/{id} 更新班级
 * - DELETE /api/classes/{id} 删除班级
 * </p>
 */
@RestController
@RequestMapping("/api/classes")
public class ClassInfoController {

    private final ClassInfoService classInfoService;

    @Autowired
    public ClassInfoController(ClassInfoService classInfoService) {
        this.classInfoService = classInfoService;
    }

    /**
     * 查询所有班级
     *
     * @return 班级列表
     */
    @GetMapping
    public List<ClassInfo> findAll() {
        return classInfoService.findAll();
    }

    /**
     * 创建班级
     *
     * @param classInfo 班级信息
     * @return 200 + 创建后的班级信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ClassInfo classInfo) {
        return ResponseEntity.ok(classInfoService.create(classInfo));
    }

    /**
     * 更新班级信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 班级不存在时返回 400。
     * </p>
     *
     * @param id 班级 ID
     * @param classInfo 更新数据
     * @return 200 + 更新后的班级信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        ClassInfo updated = classInfoService.update(id, classInfo);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "班级不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除班级
     *
     * @param id 班级 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!classInfoService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "班级不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

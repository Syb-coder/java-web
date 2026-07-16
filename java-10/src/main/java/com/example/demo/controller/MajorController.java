package com.example.demo.controller;

import com.example.demo.entity.Major;
import com.example.demo.service.MajorService;
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
 * 专业管理控制器：提供专业 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/majors      查询所有专业
 * - POST   /api/majors      创建专业
 * - PUT    /api/majors/{id} 更新专业
 * - DELETE /api/majors/{id} 删除专业
 * </p>
 */
@RestController
@RequestMapping("/api/majors")
public class MajorController {

    private final MajorService majorService;

    @Autowired
    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    /**
     * 查询所有专业
     *
     * @return 专业列表
     */
    @GetMapping
    public List<Major> findAll() {
        return majorService.findAll();
    }

    /**
     * 创建专业
     *
     * @param major 专业信息
     * @return 200 + 创建后的专业信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Major major) {
        return ResponseEntity.ok(majorService.create(major));
    }

    /**
     * 更新专业信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 专业不存在时返回 400。
     * </p>
     *
     * @param id 专业 ID
     * @param major 更新数据
     * @return 200 + 更新后的专业信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Major major) {
        Major updated = majorService.update(id, major);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "专业不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除专业
     *
     * @param id 专业 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!majorService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "专业不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

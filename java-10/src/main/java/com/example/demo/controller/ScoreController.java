package com.example.demo.controller;

import com.example.demo.entity.Score;
import com.example.demo.service.ScoreService;
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
 * 成绩管理控制器：提供成绩 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/scores      查询所有成绩
 * - POST   /api/scores      创建成绩
 * - PUT    /api/scores/{id} 更新成绩
 * - DELETE /api/scores/{id} 删除成绩
 * </p>
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /**
     * 查询所有成绩
     *
     * @return 成绩列表
     */
    @GetMapping
    public List<Score> findAll() {
        return scoreService.findAll();
    }

    /**
     * 创建成绩
     *
     * @param score 成绩信息
     * @return 200 + 创建后的成绩信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Score score) {
        return ResponseEntity.ok(scoreService.create(score));
    }

    /**
     * 更新成绩信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 成绩不存在时返回 400。
     * </p>
     *
     * @param id 成绩 ID
     * @param score 更新数据
     * @return 200 + 更新后的成绩信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Score score) {
        Score updated = scoreService.update(id, score);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "成绩不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除成绩
     *
     * @param id 成绩 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!scoreService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "成绩不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

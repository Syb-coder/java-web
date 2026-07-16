package com.example.demo.controller;

import com.example.demo.entity.Notice;
import com.example.demo.service.NoticeService;
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
 * 公告管理控制器：提供公告 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/notices      查询所有公告
 * - POST   /api/notices      创建公告
 * - PUT    /api/notices/{id} 更新公告
 * - DELETE /api/notices/{id} 删除公告
 * </p>
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 查询所有公告
     *
     * @return 公告列表
     */
    @GetMapping
    public List<Notice> findAll() {
        return noticeService.findAll();
    }

    /**
     * 创建公告
     *
     * @param notice 公告信息
     * @return 200 + 创建后的公告信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.create(notice));
    }

    /**
     * 更新公告信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 公告不存在时返回 400。
     * </p>
     *
     * @param id 公告 ID
     * @param notice 更新数据
     * @return 200 + 更新后的公告信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Notice notice) {
        Notice updated = noticeService.update(id, notice);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "公告不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除公告
     *
     * @param id 公告 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!noticeService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "公告不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}

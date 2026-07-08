package com.example.java3.controller;

import com.example.java3.dto.AnnouncementRequest;
import com.example.java3.dto.AnnouncementResponse;
import com.example.java3.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公告控制器（前台查询 + 后台管理共用）
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 查询全部公告（前台展示，GET 放行）
     *
     * @return 公告列表
     */
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(announcementService.findAll());
    }

    /**
     * 公告详情
     *
     * @param id 公告 ID
     * @return 公告响应
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(announcementService.findAll().stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("公告不存在")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入公告请求 DTO
import com.example.java3.dto.AnnouncementRequest;
// 导入公告响应 DTO
import com.example.java3.dto.AnnouncementResponse;
// 导入公告服务
import com.example.java3.service.AnnouncementService;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 HttpStatus
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 Map
import java.util.Map;

/**
 * 公告控制器（前台查询 + 后台管理共用）
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/announcements")：基础路径 /api/announcements
@RequestMapping("/api/announcements")
public class AnnouncementController {

    // 注入的公告服务
    private final AnnouncementService announcementService;

    // 构造器注入公告服务
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 查询全部公告（前台展示，GET 放行）
     *
     * @return 公告列表
     */
    // @GetMapping：处理 GET /api/announcements 请求
    @GetMapping
    public ResponseEntity<?> list() {
        // 返回 200 OK 和全部公告列表
        return ResponseEntity.ok(announcementService.findAll());
    }

    /**
     * 公告详情
     *
     * @param id 公告 ID
     * @return 公告响应
     */
    // @GetMapping("/{id}")：处理 GET /api/announcements/{id} 请求
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            // 从全部公告中过滤出指定 ID 的公告
            return ResponseEntity.ok(announcementService.findAll().stream()
                    // 过滤：ID 匹配
                    .filter(a -> a.getId().equals(id))
                    // 取第一个匹配项
                    .findFirst()
                    // 不存在则抛异常
                    .orElseThrow(() -> new IllegalArgumentException("公告不存在")));
        } catch (IllegalArgumentException e) {
            // 公告不存在，返回 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

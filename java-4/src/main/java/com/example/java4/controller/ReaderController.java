// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO、Service 与配置
import com.example.java4.config.LoginInterceptor;
import com.example.java4.dto.ReaderResponse;
import com.example.java4.dto.UserProfileRequest;
import com.example.java4.service.ReaderService;

// 导入 Spring 与 Servlet 工具
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 读者控制器（用户端个人信息管理）
 * <p>
 * 提供读者查看与修改个人信息接口，需读者登录。
 * </p>
 */
@RestController
@RequestMapping("/api/reader")
public class ReaderController {

    /** 读者服务 */
    private final ReaderService readerService;

    /**
     * 构造方法注入依赖
     */
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    /**
     * 获取个人信息
     *
     * @param session HTTP 会话
     * @return 读者信息响应（401 未登录）
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReaderResponse resp = readerService.getProfile(readerId);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 修改个人信息
     *
     * @param req     个人信息修改请求
     * @param session HTTP 会话
     * @return 修改后的读者信息响应
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileRequest req, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            ReaderResponse resp = readerService.updateProfile(readerId, req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 从 Session 获取当前登录的读者 ID
     *
     * @param session HTTP 会话
     * @return 读者 ID（未登录返回 null）
     */
    private Long getCurrentReaderId(HttpSession session) {
        Object userId = session.getAttribute(LoginInterceptor.SESSION_USER_ID_KEY);
        Object role = session.getAttribute(LoginInterceptor.SESSION_USER_ROLE_KEY);
        if (userId != null && "reader".equals(role)) {
            return (Long) userId;
        }
        return null;
    }
}

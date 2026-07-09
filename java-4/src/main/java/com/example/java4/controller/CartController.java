// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO、Service 与配置
import com.example.java4.config.LoginInterceptor;
import com.example.java4.dto.CartItemResponse;
import com.example.java4.service.CartService;

// 导入 Spring 与 Servlet 工具
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 借阅车控制器（用户端）
 * <p>
 * 提供借阅车的加入、移除、查看、提交借阅等接口。
 * 所有接口均需读者登录。
 * </p>
 * <p>
 * API 路径设计：
 * <ul>
 *   <li>GET /api/cart - 查看借阅车</li>
 *   <li>POST /api/cart/{bookId} - 加入借阅车</li>
 *   <li>DELETE /api/cart/{itemId} - 移除借阅车项</li>
 *   <li>POST /api/cart/submit - 提交借阅</li>
 *   <li>GET /api/cart/count - 获取借阅车数量</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    /** 借阅车服务 */
    private final CartService cartService;

    /**
     * 构造方法注入依赖
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 查看借阅车
     *
     * @param session HTTP 会话
     * @return 借阅车项列表（401 未登录）
     */
    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<CartItemResponse> cart = cartService.getCart(readerId);
        return ResponseEntity.ok(cart);
    }

    /**
     * 加入借阅车
     *
     * @param bookId  图书 ID
     * @param session HTTP 会话
     * @return 借阅车项响应
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<?> addToCart(@PathVariable Long bookId, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            CartItemResponse resp = cartService.addToCart(readerId, bookId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 移除借阅车项
     *
     * @param itemId  借阅车项 ID
     * @param session HTTP 会话
     * @return 200 成功
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            cartService.removeFromCart(readerId, itemId);
            Map<String, String> result = new HashMap<>();
            result.put("message", "已移出借阅车");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 提交借阅：将借阅车中的全部图书批量借阅
     *
     * @param session HTTP 会话
     * @return 借阅成功数量
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitBorrow(HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            int successCount = cartService.submitBorrow(readerId);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("message", "成功借阅 " + successCount + " 本图书");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 获取借阅车数量
     *
     * @param session HTTP 会话
     * @return 借阅车项数量
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartCount(HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Long> result = new HashMap<>();
        result.put("count", cartService.getCartCount(readerId));
        return ResponseEntity.ok(result);
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

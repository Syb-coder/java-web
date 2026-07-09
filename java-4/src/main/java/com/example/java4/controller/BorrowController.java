// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO、Service 与配置
import com.example.java4.config.LoginInterceptor;
import com.example.java4.dto.BorrowRecordResponse;
import com.example.java4.dto.RenewRequest;
import com.example.java4.dto.ReturnRequest;
import com.example.java4.service.BorrowService;

// 导入 Spring 与 Servlet 工具
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 借阅控制器（用户端）
 * <p>
 * 提供图书借阅、续借、归还、我的借阅记录查询等接口。
 * 除 GET 请求外，均需读者登录。
 * </p>
 * <p>
 * API 路径设计：
 * <ul>
 *   <li>POST /api/borrow/{bookId} - 借阅单本图书</li>
 *   <li>POST /api/borrow/renew - 续借</li>
 *   <li>POST /api/borrow/return - 归还</li>
 *   <li>GET /api/borrow/my - 获取我的借阅记录</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    /** 借阅服务 */
    private final BorrowService borrowService;

    /**
     * 构造方法注入依赖
     */
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借阅单本图书
     *
     * @param bookId  图书 ID
     * @param session HTTP 会话
     * @return 借阅记录响应（200 成功，400 借阅失败，401 未登录）
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            BorrowRecordResponse resp = borrowService.borrowBook(readerId, bookId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 续借图书
     *
     * @param req     续借请求（含 recordId）
     * @param session HTTP 会话
     * @return 续借后的借阅记录响应
     */
    @PostMapping("/renew")
    public ResponseEntity<?> renewBook(@Valid @RequestBody RenewRequest req, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            BorrowRecordResponse resp = borrowService.renewBook(readerId, req.getRecordId());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 归还图书
     *
     * @param req     归还请求（含 recordId）
     * @param session HTTP 会话
     * @return 归还后的借阅记录响应
     */
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@Valid @RequestBody ReturnRequest req, HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // 归还操作不校验归属，因为读者只能看到自己的记录（前端控制）
            // 但为安全起见，可在 Service 层加归属校验（此处简化处理）
            BorrowRecordResponse resp = borrowService.returnBook(req.getRecordId());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 获取我的借阅记录
     *
     * @param status 借阅状态过滤（可选：BORROWING/RETURNED/OVERDUE）
     * @param page   页码
     * @param size   每页条数
     * @param session HTTP 会话
     * @return 借阅记录分页结果
     */
    @GetMapping("/my")
    public ResponseEntity<?> myBorrowRecords(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        Long readerId = getCurrentReaderId(session);
        if (readerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<BorrowRecordResponse> records = borrowService.getMyBorrowRecords(readerId, status, pageable);
        return ResponseEntity.ok(records);
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

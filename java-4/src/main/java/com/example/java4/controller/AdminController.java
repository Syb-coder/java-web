// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO 与 Service
import com.example.java4.dto.BookRequest;
import com.example.java4.dto.BookResponse;
import com.example.java4.dto.BorrowRecordResponse;
import com.example.java4.dto.CategoryRequest;
import com.example.java4.dto.CategoryResponse;
import com.example.java4.dto.ReaderResponse;
import com.example.java4.service.BookService;
import com.example.java4.service.BorrowService;
import com.example.java4.service.CategoryService;
import com.example.java4.service.ReaderService;

// 导入 Spring 工具
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理控制器
 * <p>
 * 提供图书管理、分类管理、读者管理、借阅记录管理等后台接口。
 * 写操作（POST/PUT/DELETE）需管理员登录（由拦截器强制校验）。
 * </p>
 * <p>
 * API 路径设计：
 * <ul>
 *   <li>POST/PUT/DELETE /api/admin/books[/{id}] - 图书 CRUD</li>
 *   <li>POST/PUT/DELETE /api/admin/categories[/{id}] - 分类 CRUD</li>
 *   <li>GET /api/admin/readers - 分页查询读者</li>
 *   <li>GET /api/admin/readers/{id} - 读者详情</li>
 *   <li>GET /api/admin/borrow-records - 查询全部借阅记录</li>
 *   <li>POST /api/admin/borrow-records/return/{recordId} - 管理员处理归还</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** 图书服务 */
    private final BookService bookService;

    /** 分类服务 */
    private final CategoryService categoryService;

    /** 读者服务 */
    private final ReaderService readerService;

    /** 借阅服务 */
    private final BorrowService borrowService;

    /**
     * 构造方法注入依赖
     */
    public AdminController(BookService bookService, CategoryService categoryService,
                           ReaderService readerService, BorrowService borrowService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.readerService = readerService;
        this.borrowService = borrowService;
    }

    // ===== 图书管理 =====

    /**
     * 新增图书
     *
     * @param req 图书请求
     * @return 新创建的图书响应
     */
    @PostMapping("/books")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookRequest req) {
        try {
            return ResponseEntity.ok(bookService.createBook(req));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    /**
     * 修改图书
     *
     * @param id  图书 ID
     * @param req 图书请求
     * @return 修改后的图书响应
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest req) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, req));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    /**
     * 删除图书
     *
     * @param id 图书 ID
     * @return 200 成功
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    // ===== 分类管理 =====

    /**
     * 新增分类
     *
     * @param req 分类请求
     * @return 新创建的分类响应
     */
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(req));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    /**
     * 修改分类
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 修改后的分类响应
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, req));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    /**
     * 删除分类
     *
     * @param id 分类 ID
     * @return 200 成功
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    // ===== 读者管理 =====

    /**
     * 分页查询读者
     *
     * @param keyword 搜索关键字（姓名或学号，可选）
     * @param page    页码
     * @param size    每页条数
     * @return 读者分页结果
     */
    @GetMapping("/readers")
    public ResponseEntity<Page<ReaderResponse>> searchReaders(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(readerService.searchReaders(keyword, pageable));
    }

    /**
     * 获取读者详情
     *
     * @param id 读者 ID
     * @return 读者响应
     */
    @GetMapping("/readers/{id}")
    public ResponseEntity<ReaderResponse> getReaderById(@PathVariable Long id) {
        ReaderResponse resp = readerService.getReaderById(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    // ===== 借阅记录管理 =====

    /**
     * 查询全部借阅记录（支持状态过滤）
     *
     * @param status 借阅状态（可选）
     * @param page   页码
     * @param size   每页条数
     * @return 借阅记录分页结果
     */
    @GetMapping("/borrow-records")
    public ResponseEntity<Page<BorrowRecordResponse>> getAllBorrowRecords(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(borrowService.getAllBorrowRecords(status, pageable));
    }

    /**
     * 管理员处理归还
     *
     * @param recordId 借阅记录 ID
     * @return 归还后的借阅记录响应
     */
    @PostMapping("/borrow-records/return/{recordId}")
    public ResponseEntity<?> returnBook(@PathVariable Long recordId) {
        try {
            return ResponseEntity.ok(borrowService.returnBook(recordId));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    // ===== 工具方法 =====

    /**
     * 构造 400 错误响应
     *
     * @param message 错误信息
     * @return 400 响应
     */
    private ResponseEntity<Map<String, String>> badRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

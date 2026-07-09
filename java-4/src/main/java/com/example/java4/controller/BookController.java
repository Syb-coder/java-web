// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO 与 Service
import com.example.java4.dto.BookResponse;
import com.example.java4.service.BookService;

// 导入 Spring 工具
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图书控制器（用户端查询）
 * <p>
 * 提供图书浏览、搜索、分类筛选、详情查看等 GET 接口，无需登录即可访问。
 * </p>
 * <p>
 * API 路径设计：
 * <ul>
 *   <li>GET /api/books - 分页搜索图书（支持 keyword 参数）</li>
 *   <li>GET /api/books/category/{categoryId} - 按分类分页查询</li>
 *   <li>GET /api/books/latest - 获取最新入库图书</li>
 *   <li>GET /api/books/{id} - 获取图书详情</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    /** 图书服务 */
    private final BookService bookService;

    /**
     * 构造方法注入依赖
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 分页搜索图书
     *
     * @param keyword 搜索关键字（书名或作者，可选）
     * @param page    页码（从 0 开始，默认 0）
     * @param size    每页条数（默认 12）
     * @return 图书分页结果
     */
    @GetMapping
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.searchBooks(keyword, pageable));
    }

    /**
     * 按分类分页查询图书
     *
     * @param categoryId 分类 ID
     * @param keyword    搜索关键字（可选）
     * @param page       页码
     * @param size       每页条数
     * @return 图书分页结果
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<BookResponse>> searchByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.searchByCategory(categoryId, keyword, pageable));
    }

    /**
     * 获取最新入库图书（主页展示）
     *
     * @param page 页码
     * @param size 每页条数（默认 8）
     * @return 图书分页结果
     */
    @GetMapping("/latest")
    public ResponseEntity<Page<BookResponse>> getLatestBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.getLatestBooks(pageable));
    }

    /**
     * 获取图书详情
     *
     * @param id 图书 ID
     * @return 图书详情（404 不存在）
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }
}

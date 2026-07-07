// 声明包路径，归类为 controller 控制器层，承接 HTTP 请求并调用 service
package com.example.java1.controller;

// 以下导入本模块内的 DTO 与服务，遵循分层架构：controller 不直接访问 repository
import com.example.java1.dto.BookRequest; // 引入图书请求 DTO，封装新增/修改图书参数
import com.example.java1.dto.BookResponse; // 引入图书响应 DTO，对外返回脱敏后的图书信息
import com.example.java1.service.BookService; // 引入图书服务，封装图书增删改查业务逻辑
// 以下导入校验注解，配合 DTO 上的约束注解实现参数自动校验
import jakarta.validation.Valid; // 引入 @Valid，触发请求体参数的 Bean Validation
import org.springframework.http.ResponseEntity; // 引入响应实体，可灵活控制状态码与响应体
import org.springframework.web.bind.annotation.DeleteMapping; // 引入 @DeleteMapping，映射 HTTP DELETE 请求
import org.springframework.web.bind.annotation.GetMapping; // 引入 @GetMapping，映射 HTTP GET 请求
import org.springframework.web.bind.annotation.PathVariable; // 引入 @PathVariable，绑定 URL 路径变量
import org.springframework.web.bind.annotation.PostMapping; // 引入 @PostMapping，映射 HTTP POST 请求
import org.springframework.web.bind.annotation.PutMapping; // 引入 @PutMapping，映射 HTTP PUT 请求
import org.springframework.web.bind.annotation.RequestBody; // 引入 @RequestBody，绑定请求体到 DTO
import org.springframework.web.bind.annotation.RequestMapping; // 引入 @RequestMapping，定义控制器根路径
import org.springframework.web.bind.annotation.RequestParam; // 引入 @RequestParam，绑定查询参数
import org.springframework.web.bind.annotation.RestController; // 引入 @RestController，声明 RESTful 控制器

import java.util.List; // 引入 List 集合接口，用于返回列表数据

/**
 * 图书控制器
 * <p>
 * 提供图书的增删改查与检索接口。GET 请求公开访问，写操作需管理员登录（由拦截器控制）。
 * </p>
 * <p>
 * 设计说明：图书档案是校园借阅系统的核心资源，查询浏览对师生开放以提升检索效率，
 * 而增删改仅限管理员以保障数据权威性，权限边界由 LoginInterceptor 统一控制。
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回值自动序列化为 JSON，不渲染视图
@RequestMapping("/api/books") // 统一前缀 /api/books，遵循 RESTful 资源命名约定
public class BookController {

    private final BookService bookService; // 图书服务依赖，final 保证构造后不可变

    /**
     * 构造方法注入
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param bookService 图书服务
     */
    public BookController(BookService bookService) { // 构造方法注入依赖
        this.bookService = bookService; // 赋值图书服务
    }

    /**
     * 查询全部图书
     *
     * @return 图书列表
     */
    @GetMapping // 映射 GET /api/books，查询为幂等读操作，由拦截器放行
    public ResponseEntity<List<BookResponse>> list() { // 无参数，返回全部图书
        return ResponseEntity.ok(bookService.getAllBooks()); // 委托服务层查询并以 200 返回
    }

    /**
     * 按关键字检索（书名或作者）
     *
     * @param keyword 关键字
     * @return 匹配的图书列表
     */
    @GetMapping("/search") // 映射 GET /api/books/search，检索为读操作公开访问
    public ResponseEntity<List<BookResponse>> search(@RequestParam(required = false) String keyword) { // 关键字可选，为空时由服务层降级返回全部
        return ResponseEntity.ok(bookService.searchBooks(keyword)); // 委托服务层模糊检索并以 200 返回
    }

    /**
     * 按分类查询
     *
     * @param category 分类
     * @return 图书列表
     */
    @GetMapping("/category/{category}") // 映射 GET /api/books/category/{category}，分类作为路径变量便于缓存与分享
    public ResponseEntity<List<BookResponse>> byCategory(@PathVariable String category) { // 绑定路径变量到分类参数
        return ResponseEntity.ok(bookService.getBooksByCategory(category)); // 委托服务层按分类查询并以 200 返回
    }

    /**
     * 按 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书响应（404 若不存在）
     */
    @GetMapping("/{id}") // 映射 GET /api/books/{id}，按主键查询资源
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) { // 绑定路径变量到图书 ID
        BookResponse resp = bookService.getBookById(id); // 委托服务层按 ID 查询，可能返回 null
        if (resp == null) { // 图书不存在场景
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，符合 RESTful 资源缺失语义
        }
        return ResponseEntity.ok(resp); // 图书存在则返回 200 与图书响应
    }

    /**
     * 新增图书（需管理员登录）
     *
     * @param req 图书请求
     * @return 新建的图书响应（400 参数校验失败）
     */
    @PostMapping // 映射 POST /api/books，新增资源用 POST，写操作由拦截器校验管理员登录态
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest req) { // @Valid 触发请求体校验
        try {
            return ResponseEntity.ok(bookService.createBook(req)); // 委托服务层创建并以 200 返回新建图书
        } catch (IllegalArgumentException e) {
            // 参数业务校验失败（如 ISBN 重复、分类不存在）
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，表示请求参数业务校验未通过
        }
    }

    /**
     * 修改图书（需管理员登录）
     *
     * @param id  图书 ID
     * @param req 图书请求
     * @return 更新后的图书响应
     */
    @PutMapping("/{id}") // 映射 PUT /api/books/{id}，整体更新资源用 PUT，写操作由拦截器校验管理员登录态
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest req) { // 路径变量指定更新目标，@Valid 校验请求体
        try {
            return ResponseEntity.ok(bookService.updateBook(id, req)); // 委托服务层更新并以 200 返回更新后图书
        } catch (IllegalArgumentException e) {
            // 图书不存在时服务层抛出参数异常
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示待更新资源不存在
        }
    }

    /**
     * 删除图书（需管理员登录）
     *
     * @param id 图书 ID
     * @return 204（成功） / 404（不存在）
     */
    @DeleteMapping("/{id}") // 映射 DELETE /api/books/{id}，删除资源用 DELETE，写操作由拦截器校验管理员登录态
    public ResponseEntity<Void> delete(@PathVariable Long id) { // 路径变量指定删除目标，无响应体
        try {
            bookService.deleteBook(id); // 委托服务层删除图书
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        } catch (IllegalArgumentException e) {
            // 图书不存在时服务层抛出参数异常
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示待删除资源不存在
        }
    }
}

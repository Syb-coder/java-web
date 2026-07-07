package com.example.java7.controller; // 声明当前类所在的包路径，归类为 controller 控制器层

import com.example.java7.dto.BookRequest; // 引入图书请求 DTO，接收前端新增/更新图书参数
import com.example.java7.dto.BookResponse; // 引入图书响应 DTO，返回给前端图书数据
import com.example.java7.model.BookCategory; // 引入图书分类枚举，用于分类过滤查询
import com.example.java7.service.BookService; // 引入图书业务服务，控制器委托其处理业务逻辑
import jakarta.validation.Valid; // 引入 JSR-380 校验注解，触发参数自动校验
import org.springframework.http.HttpStatus; // 引入 HTTP 状态码枚举，用于构建响应
import org.springframework.http.ResponseEntity; // 引入响应实体类，封装响应体与状态码
import org.springframework.web.bind.annotation.CrossOrigin; // 引入跨域注解，允许前端跨域访问
import org.springframework.web.bind.annotation.DeleteMapping; // 引入 DELETE 映射注解
import org.springframework.web.bind.annotation.GetMapping; // 引入 GET 映射注解
import org.springframework.web.bind.annotation.PathVariable; // 引入路径参数注解
import org.springframework.web.bind.annotation.PostMapping; // 引入 POST 映射注解
import org.springframework.web.bind.annotation.PutMapping; // 引入 PUT 映射注解
import org.springframework.web.bind.annotation.RequestBody; // 引入请求体注解，反序列化 JSON
import org.springframework.web.bind.annotation.RequestMapping; // 引入请求映射注解，定义类级别路径前缀
import org.springframework.web.bind.annotation.RequestParam; // 引入请求参数注解，从 query string 取值
import org.springframework.web.bind.annotation.RestController; // 引入 REST 控制器注解，返回 JSON 而非视图

import java.util.List; // 引入 List 集合接口
import java.util.Map; // 引入 Map 接口，用于构建错误响应体

/**
 * 图书控制器
 * <p>
 * 职责：处理图书 CRUD 相关的 HTTP 请求，遵循 RESTful 规范。
 * 路径前缀：/api/books
 * 状态码约定：200 成功、201 创建、400 参数错误、404 资源不存在、422 校验失败、500 内部错误
 * </p>
 */
@RestController // 声明为 REST 控制器，方法返回值自动序列化为 JSON 响应体
@RequestMapping("/api/books") // 类级别路径前缀，所有方法路径均以此开头
@CrossOrigin(origins = "*") // 允许所有来源跨域访问，便于前端本地开发联调
public class BookController {

    /** 图书服务 */
    private final BookService bookService; // 注入图书服务，final 保证不可变

    /**
     * 构造方法注入服务
     *
     * @param bookService 图书服务
     */
    public BookController(BookService bookService) { // 构造方法注入，Spring 自动注入单例服务
        this.bookService = bookService; // 赋值成员变量
    }

    /**
     * 查询图书列表（支持关键字检索与分类过滤）
     * <p>
     * 参数优先级：keyword > category > 全部。
     * </p>
     *
     * @param keyword  关键字（书名或作者），可选
     * @param category 分类，可选
     * @return 图书响应列表
     */
    @GetMapping // 处理 HTTP GET 请求，路径为类前缀 /api/books
    public ResponseEntity<List<BookResponse>> listBooks(
            @RequestParam(required = false) String keyword, // 从 query string 获取关键字参数，非必填
            @RequestParam(required = false) BookCategory category) { // 从 query string 获取分类参数，非必填，自动转换枚举
        List<BookResponse> books; // 声明结果列表
        if (keyword != null && !keyword.isBlank()) { // 关键字优先级最高：非空时按关键字检索
            books = bookService.searchBooks(keyword); // 调用服务按关键字检索
        } else if (category != null) { // 其次按分类过滤
            books = bookService.getBooksByCategory(category); // 调用服务按分类查询
        } else {
            books = bookService.getAllBooks(); // 无过滤条件时返回全部图书
        }
        return ResponseEntity.ok(books); // 构建 HTTP 200 响应并返回图书列表
    }

    /**
     * 根据 ID 查询图书详情
     *
     * @param id 图书 ID
     * @return 图书响应
     */
    @GetMapping("/{id}") // 处理 GET 请求，路径为 /api/books/{id}
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) { // 从 URL 路径中提取参数值 id
        try { // 包裹可能抛出异常的业务调用
            BookResponse book = bookService.getBookById(id); // 调用服务查询图书详情
            return ResponseEntity.ok(book); // 构建 HTTP 200 响应返回图书详情
        } catch (IllegalArgumentException e) { // 捕获图书不存在的非法参数异常
            // 资源不存在返回 404
            return ResponseEntity.notFound().build(); // 构建 HTTP 404 无响应体返回
        }
    }

    /**
     * 新增图书
     *
     * @param request 图书请求 DTO（自动校验）
     * @return 创建的图书响应，状态码 201
     */
    @PostMapping // 处理 HTTP POST 请求，路径为 /api/books
    public ResponseEntity<?> addBook(@Valid @RequestBody BookRequest request) { // @Valid 触发 JSR-380 参数校验，@RequestBody 将请求体 JSON 反序列化为 Java 对象
        try { // 包裹业务异常
            BookResponse book = bookService.addBook(request); // 调用服务新增图书
            return ResponseEntity.status(HttpStatus.CREATED).body(book); // 构建 HTTP 201 Created 响应返回新建图书
        } catch (Exception e) { // 捕获所有业务异常（如校验失败、唯一约束冲突等）
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 构建 HTTP 400 响应
                    .body(Map.of("error", e.getMessage())); // 返回包含错误信息的 JSON 体
        }
    }

    /**
     * 更新图书信息
     *
     * @param id      图书 ID
     * @param request 图书请求 DTO
     * @return 更新后的图书响应
     */
    @PutMapping("/{id}") // 处理 HTTP PUT 请求，路径为 /api/books/{id}
    public ResponseEntity<?> updateBook(@PathVariable Long id, // 从 URL 路径提取图书 ID
                                        @Valid @RequestBody BookRequest request) { // 触发参数校验并反序列化请求体
        try { // 包裹业务异常
            BookResponse book = bookService.updateBook(id, request); // 调用服务更新图书
            return ResponseEntity.ok(book); // 构建 HTTP 200 响应返回更新后的图书
        } catch (IllegalArgumentException e) { // 捕获图书不存在的异常
            return ResponseEntity.notFound().build(); // 返回 404
        } catch (IllegalStateException e) { // 捕获业务规则冲突异常（如馆藏数小于已借出数）
            // 业务规则冲突返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY) // 构建 HTTP 422 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        }
    }

    /**
     * 删除图书
     *
     * @param id 图书 ID
     * @return 204 无内容或错误信息
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，路径为 /api/books/{id}
    public ResponseEntity<?> deleteBook(@PathVariable Long id) { // 从 URL 路径提取图书 ID
        try { // 包裹业务异常
            bookService.deleteBook(id); // 调用服务删除图书
            return ResponseEntity.noContent().build(); // 构建 HTTP 204 No Content 响应，表示删除成功无返回体
        } catch (IllegalArgumentException e) { // 捕获图书不存在异常
            return ResponseEntity.notFound().build(); // 返回 404
        } catch (IllegalStateException e) { // 捕获存在未归还记录的业务冲突
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY) // 构建 422 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        }
    }

    /**
     * 获取所有图书分类（前端下拉框使用）
     *
     * @return 分类枚举数组
     */
    @GetMapping("/categories") // 处理 GET 请求，路径为 /api/books/categories
    public ResponseEntity<BookCategory[]> getCategories() { // 返回分类枚举数组方法
        return ResponseEntity.ok(BookCategory.values()); // 构建 200 响应返回所有枚举值数组，供前端下拉框使用
    }
}

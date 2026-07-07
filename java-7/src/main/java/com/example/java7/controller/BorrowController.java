package com.example.java7.controller; // 声明当前类所在的包路径，归类为 controller 控制器层

import com.example.java7.dto.BorrowRecordResponse; // 引入借阅记录响应 DTO，返回借阅信息给前端
import com.example.java7.dto.BorrowRequest; // 引入借书请求 DTO，封装借书参数
import com.example.java7.dto.ReturnRequest; // 引入还书请求 DTO，封装还书参数
import com.example.java7.dto.StatsResponse; // 引入统计响应 DTO，用于首页数据展示
import com.example.java7.model.BorrowStatus; // 引入借阅状态枚举，用于按状态过滤
import com.example.java7.service.BorrowService; // 引入借阅业务服务，控制器委托其处理业务逻辑
import jakarta.validation.Valid; // 引入 JSR-380 校验注解，触发参数自动校验
import org.springframework.http.HttpStatus; // 引入 HTTP 状态码枚举
import org.springframework.http.ResponseEntity; // 引入响应实体类，封装响应体与状态码
import org.springframework.web.bind.annotation.CrossOrigin; // 引入跨域注解
import org.springframework.web.bind.annotation.GetMapping; // 引入 GET 映射注解
import org.springframework.web.bind.annotation.PathVariable; // 引入路径参数注解
import org.springframework.web.bind.annotation.PostMapping; // 引入 POST 映射注解
import org.springframework.web.bind.annotation.RequestBody; // 引入请求体注解，反序列化 JSON
import org.springframework.web.bind.annotation.RequestMapping; // 引入请求映射注解，定义类级别路径前缀
import org.springframework.web.bind.annotation.RequestParam; // 引入请求参数注解
import org.springframework.web.bind.annotation.RestController; // 引入 REST 控制器注解

import java.util.List; // 引入 List 集合接口
import java.util.Map; // 引入 Map 接口，用于构建错误响应体

/**
 * 借阅记录控制器
 * <p>
 * 职责：处理借书、还书、借阅历史查询、统计等 HTTP 请求。
 * 路径前缀：/api/borrow
 * </p>
 */
@RestController // 声明为 REST 控制器，方法返回值自动序列化为 JSON 响应体
@RequestMapping("/api/borrow") // 类级别路径前缀，所有方法路径均以此开头
@CrossOrigin(origins = "*") // 允许所有来源跨域访问，便于前端本地开发联调
public class BorrowController {

    /** 借阅服务 */
    private final BorrowService borrowService; // 注入借阅服务，final 保证不可变

    /**
     * 构造方法注入服务
     *
     * @param borrowService 借阅服务
     */
    public BorrowController(BorrowService borrowService) { // 构造方法注入，Spring 自动注入单例服务
        this.borrowService = borrowService; // 赋值成员变量
    }

    /**
     * 借书操作
     *
     * @param request 借书请求 DTO
     * @return 借阅记录响应，状态码 201
     */
    @PostMapping // 处理 HTTP POST 请求，路径为 /api/borrow
    public ResponseEntity<?> borrow(@Valid @RequestBody BorrowRequest request) { // @Valid 触发 JSR-380 参数校验，@RequestBody 将请求体 JSON 反序列化为 Java 对象
        try { // 包裹业务异常
            BorrowRecordResponse record = borrowService.borrowBook(request); // 调用服务执行借书
            return ResponseEntity.status(HttpStatus.CREATED).body(record); // 构建 HTTP 201 Created 响应返回借阅记录
        } catch (IllegalArgumentException e) { // 捕获参数非法异常（如日期不合法）
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 构建 HTTP 400 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        } catch (IllegalStateException e) { // 捕获业务冲突异常（如库存不足）
            // 库存不足等业务冲突返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY) // 构建 HTTP 422 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        }
    }

    /**
     * 还书操作
     *
     * @param request 还书请求 DTO
     * @return 更新后的借阅记录响应
     */
    @PostMapping("/return") // 处理 HTTP POST 请求，路径为 /api/borrow/return
    public ResponseEntity<?> returnBook(@Valid @RequestBody ReturnRequest request) { // 触发参数校验并反序列化请求体
        try { // 包裹业务异常
            BorrowRecordResponse record = borrowService.returnBook(request); // 调用服务执行还书
            return ResponseEntity.ok(record); // 构建 HTTP 200 响应返回更新后的记录
        } catch (IllegalArgumentException e) { // 捕获记录不存在异常
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 构建 HTTP 404 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        } catch (IllegalStateException e) { // 捕获重复还书等业务冲突
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY) // 构建 HTTP 422 响应
                    .body(Map.of("error", e.getMessage())); // 返回错误信息 JSON
        }
    }

    /**
     * 查询借阅记录列表
     * <p>
     * 支持按状态、借阅人、图书 ID 过滤。
     * 参数优先级：bookId > status > borrower > 全部。
     * </p>
     *
     * @param status      借阅状态
     * @param borrower    借阅人关键字
     * @param bookId      图书 ID
     * @return 借阅记录列表
     */
    @GetMapping // 处理 HTTP GET 请求，路径为 /api/borrow
    public ResponseEntity<List<BorrowRecordResponse>> listRecords(
            @RequestParam(required = false) BorrowStatus status, // 从 query string 获取状态参数，非必填，自动转换枚举
            @RequestParam(required = false) String borrower, // 从 query string 获取借阅人关键字，非必填
            @RequestParam(required = false) Long bookId) { // 从 query string 获取图书 ID，非必填
        List<BorrowRecordResponse> records; // 声明结果列表
        if (bookId != null) { // bookId 优先级最高：按图书 ID 查询借阅历史
            records = borrowService.getRecordsByBookId(bookId); // 调用服务按图书 ID 查询
        } else if (status != null) { // 其次按状态过滤
            records = borrowService.getRecordsByStatus(status); // 调用服务按状态查询
        } else if (borrower != null && !borrower.isBlank()) { // 其次按借阅人关键字模糊检索
            records = borrowService.searchByBorrower(borrower); // 调用服务按借阅人检索
        } else {
            records = borrowService.getAllRecords(); // 无过滤条件时返回全部记录
        }
        return ResponseEntity.ok(records); // 构建 HTTP 200 响应返回记录列表
    }

    /**
     * 根据记录 ID 查询详情
     *
     * @param id 记录 ID
     * @return 借阅记录响应
     */
    @GetMapping("/{id}") // 处理 GET 请求，路径为 /api/borrow/{id}
    public ResponseEntity<BorrowRecordResponse> getRecord(@PathVariable Long id) { // 从 URL 路径中提取参数值 id
        return borrowService.getAllRecords().stream() // 获取全部记录并开启流处理（简化实现，未直接走仓储按 ID 查询）
                .filter(r -> r.getId().equals(id)) // 过滤出与路径 ID 匹配的记录
                .findFirst() // 取第一条匹配记录（Optional 包装）
                .map(ResponseEntity::ok) // 存在则构建 200 响应
                .orElseGet(() -> ResponseEntity.notFound().build()); // 不存在则构建 404 响应
    }

    /**
     * 获取首页统计数据
     *
     * @return 统计响应
     */
    @GetMapping("/stats") // 处理 GET 请求，路径为 /api/borrow/stats
    public ResponseEntity<StatsResponse> getStats() { // 首页统计方法
        return ResponseEntity.ok(borrowService.getStats()); // 构建 HTTP 200 响应返回统计数据
    }
}

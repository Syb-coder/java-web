// 声明包路径，归类为 controller 控制器层，承接 HTTP 请求并调用 service
package com.example.java1.controller;

// 以下导入本模块内的 DTO 与服务、枚举，遵循分层架构：controller 不直接访问 repository
import com.example.java1.dto.BorrowRecordResponse; // 引入借阅记录响应 DTO，对外返回借阅信息
import com.example.java1.dto.BorrowRequest; // 引入借书请求 DTO，封装借书参数
import com.example.java1.dto.RenewRequest; // 引入续借请求 DTO，封装续借参数
import com.example.java1.dto.ReturnRequest; // 引入还书请求 DTO，封装还书参数
import com.example.java1.model.BorrowStatus; // 引入借阅状态枚举（BORROWED / RETURNED / OVERDUE）
import com.example.java1.service.BorrowService; // 引入借阅服务，封装借还书业务逻辑
// 以下导入校验注解，配合 DTO 上的约束注解实现参数自动校验
import jakarta.validation.Valid; // 引入 @Valid，触发请求体参数的 Bean Validation
import org.springframework.http.ResponseEntity; // 引入响应实体，可灵活控制状态码与响应体
import org.springframework.web.bind.annotation.GetMapping; // 引入 @GetMapping，映射 HTTP GET 请求
import org.springframework.web.bind.annotation.PathVariable; // 引入 @PathVariable，绑定 URL 路径变量
import org.springframework.web.bind.annotation.PostMapping; // 引入 @PostMapping，映射 HTTP POST 请求
import org.springframework.web.bind.annotation.RequestBody; // 引入 @RequestBody，绑定请求体到 DTO
import org.springframework.web.bind.annotation.RequestMapping; // 引入 @RequestMapping，定义控制器根路径
import org.springframework.web.bind.annotation.RequestParam; // 引入 @RequestParam，绑定查询参数
import org.springframework.web.bind.annotation.RestController; // 引入 @RestController，声明 RESTful 控制器

import java.util.List; // 引入 List 集合接口，用于返回列表数据

/**
 * 借阅控制器
 * <p>
 * 提供借书、还书、续借、借阅记录查询接口。
 * 借还书与续借为 POST 写操作，需管理员登录（由拦截器控制）。
 * 查询接口公开访问。
 * </p>
 * <p>
 * 设计说明：校园图书借阅场景中，借还手续由图书管理员在柜台代办，
 * 因此写操作统一要求管理员登录态，读者仅可查询浏览记录。
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回值自动序列化为 JSON，不渲染视图
@RequestMapping("/api/borrows") // 统一前缀 /api/borrows，遵循 RESTful 资源命名约定
public class BorrowController {

    private final BorrowService borrowService; // 借阅服务依赖，final 保证构造后不可变

    /**
     * 构造方法注入
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param borrowService 借阅服务
     */
    public BorrowController(BorrowService borrowService) { // 构造方法注入依赖
        this.borrowService = borrowService; // 赋值借阅服务
    }

    /**
     * 借书（需管理员登录）
     * <p>校园场景由管理员代办借还手续。</p>
     *
     * @param req 借书请求
     * @return 借阅记录响应（400 参数错误 / 422 库存不足或超上限）
     */
    @PostMapping // 映射 POST /api/borrows，借书为写操作，由拦截器校验管理员登录态
    public ResponseEntity<BorrowRecordResponse> borrow(@Valid @RequestBody BorrowRequest req) { // @Valid 触发请求体校验
        try {
            return ResponseEntity.ok(borrowService.borrowBook(req)); // 委托服务层处理借书并以 200 返回记录
        } catch (IllegalArgumentException e) {
            // 图书或读者不存在
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，表示请求参数指向的资源不存在
        } catch (IllegalStateException e) {
            // 库存不足或超出借阅上限
            return ResponseEntity.status(422).build(); // 返回 422 Unprocessable Entity，表示请求格式正确但业务约束未满足
        }
    }

    /**
     * 还书（需管理员登录）
     *
     * @param req 还书请求
     * @return 借阅记录响应（400 记录不存在 / 422 已归还）
     */
    @PostMapping("/return") // 映射 POST /api/borrows/return，还书为写操作，由拦截器校验管理员登录态
    public ResponseEntity<BorrowRecordResponse> returnBook(@Valid @RequestBody ReturnRequest req) { // @Valid 触发请求体校验
        try {
            return ResponseEntity.ok(borrowService.returnBook(req)); // 委托服务层处理还书并以 200 返回记录
        } catch (IllegalArgumentException e) {
            // 借阅记录不存在
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，表示请求参数指向的记录不存在
        } catch (IllegalStateException e) {
            // 记录已归还，禁止重复操作
            return ResponseEntity.status(422).build(); // 返回 422 Unprocessable Entity，表示业务状态不允许该操作
        }
    }

    /**
     * 续借（需管理员登录）
     *
     * @param req 续借请求
     * @return 借阅记录响应（400 记录不存在 / 422 状态不允许续借）
     */
    @PostMapping("/renew") // 映射 POST /api/borrows/renew，续借为写操作，由拦截器校验管理员登录态
    public ResponseEntity<BorrowRecordResponse> renew(@Valid @RequestBody RenewRequest req) { // @Valid 触发请求体校验
        try {
            return ResponseEntity.ok(borrowService.renewBook(req)); // 委托服务层处理续借并以 200 返回记录
        } catch (IllegalArgumentException e) {
            // 借阅记录不存在
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，表示请求参数指向的记录不存在
        } catch (IllegalStateException e) {
            // 状态不允许续借（如已归还、已逾期）
            return ResponseEntity.status(422).build(); // 返回 422 Unprocessable Entity，表示业务状态不允许该操作
        }
    }

    /**
     * 查询全部借阅记录
     *
     * @return 借阅记录列表
     */
    @GetMapping // 映射 GET /api/borrows，查询为幂等读操作，由拦截器放行
    public ResponseEntity<List<BorrowRecordResponse>> list() { // 无参数，返回全部借阅记录
        return ResponseEntity.ok(borrowService.getAllRecords()); // 委托服务层查询并以 200 返回
    }

    /**
     * 按读者查询借阅记录
     *
     * @param readerId 读者 ID
     * @return 借阅记录列表
     */
    @GetMapping("/reader/{readerId}") // 映射 GET /api/borrows/reader/{readerId}，按读者维度查询历史
    public ResponseEntity<List<BorrowRecordResponse>> byReader(@PathVariable Long readerId) { // 绑定路径变量到读者 ID
        return ResponseEntity.ok(borrowService.getRecordsByReaderId(readerId)); // 委托服务层按读者查询并以 200 返回
    }

    /**
     * 按图书查询借阅历史
     *
     * @param bookId 图书 ID
     * @return 借阅记录列表
     */
    @GetMapping("/book/{bookId}") // 映射 GET /api/borrows/book/{bookId}，按图书维度查询流转历史
    public ResponseEntity<List<BorrowRecordResponse>> byBook(@PathVariable Long bookId) { // 绑定路径变量到图书 ID
        return ResponseEntity.ok(borrowService.getRecordsByBookId(bookId)); // 委托服务层按图书查询并以 200 返回
    }

    /**
     * 按状态查询借阅记录
     *
     * @param status 借阅状态（BORROWED / RETURNED / OVERDUE）
     * @return 借阅记录列表
     */
    @GetMapping("/status/{status}") // 映射 GET /api/borrows/status/{status}，按状态维度筛选记录
    public ResponseEntity<List<BorrowRecordResponse>> byStatus(@PathVariable BorrowStatus status) { // 路径变量自动转换为枚举
        return ResponseEntity.ok(borrowService.getRecordsByStatus(status)); // 委托服务层按状态查询并以 200 返回
    }
}

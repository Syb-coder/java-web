// 声明包路径，归类为 controller 控制器层，承接 HTTP 请求并调用 service
package com.example.java1.controller;

// 以下导入本模块内的 DTO 与服务、实体，遵循分层架构：controller 不直接访问 repository
import com.example.java1.dto.LoginRequest; // 引入登录请求 DTO，复用管理员的登录请求结构
import com.example.java1.dto.ReaderRequest; // 引入读者请求 DTO，封装新增/修改读者参数
import com.example.java1.dto.ReaderResponse; // 引入读者响应 DTO，对外返回脱敏后的读者信息
import com.example.java1.model.Reader; // 引入读者实体，对应数据库 readers 表
import com.example.java1.service.ReaderService; // 引入读者服务，封装读者增删改查业务逻辑
// 以下导入 Servlet 会话相关类型，读者登录态同样采用 HttpSession 维持
import jakarta.servlet.http.HttpSession; // 引入 HTTP 会话，用于存储读者登录态
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
 * 读者控制器
 * <p>
 * 提供读者的增删改查、登录接口。读者登录后可查询个人借阅记录（通过 BorrowController）。
 * 写操作需管理员登录（由拦截器控制）。
 * </p>
 * <p>
 * 设计说明：校园借阅场景中读者为借阅主体（学生/教师），读者登录仅用于查询个人借阅记录，
 * 档案维护（增删改）权限归属管理员，避免读者自行篡改信息。
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回值自动序列化为 JSON，不渲染视图
@RequestMapping("/api/readers") // 统一前缀 /api/readers，遵循 RESTful 资源命名约定
public class ReaderController {

    /** Session 中存储当前登录读者的键名，对外公开以便拦截器与其它组件复用 */
    public static final String SESSION_READER_KEY = "reader"; // 常量键名，避免魔法字符串散落各处

    private final ReaderService readerService; // 读者服务依赖，final 保证构造后不可变

    /**
     * 构造方法注入
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param readerService 读者服务
     */
    public ReaderController(ReaderService readerService) { // 构造方法注入依赖
        this.readerService = readerService; // 赋值读者服务
    }

    /**
     * 读者登录
     *
     * @param req     登录请求（username 为学号/工号）
     * @param session HTTP 会话
     * @return 读者响应（成功）或 401（失败）
     */
    @PostMapping("/login") // 映射 POST /api/readers/login，登录为写操作故用 POST，由拦截器放行
    public ResponseEntity<ReaderResponse> login(@Valid @RequestBody LoginRequest req, HttpSession session) { // @Valid 触发请求体校验，session 由容器注入
        Reader reader = readerService.login(req.username(), req.password()); // 委托服务层校验学号与密码，返回读者实体或 null
        if (reader == null) { // 登录失败：学号不存在或密码错误
            return ResponseEntity.status(401).build(); // 返回 401 Unauthorized，统一错误信息避免泄露账号是否存在
        }
        session.setAttribute(SESSION_READER_KEY, reader); // 将读者实体存入 Session，后续查询个人借阅记录时复用
        return ResponseEntity.ok(ReaderResponse.from(reader)); // 转换为脱敏响应 DTO 返回 200，前端引导至读者个人页
    }

    /**
     * 读者登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout") // 映射 POST /api/readers/logout，登出改变服务端状态故用 POST
    public ResponseEntity<Void> logout(HttpSession session) { // session 由容器注入
        session.invalidate(); // 销毁当前会话，清除读者登录态与 Session 数据
        return ResponseEntity.noContent().build(); // 返回 204 No Content，表示操作成功且无响应体
    }

    /**
     * 获取当前登录读者信息
     *
     * @param session HTTP 会话
     * @return 读者响应（已登录）或 401（未登录）
     */
    @GetMapping("/me") // 映射 GET /api/readers/me，仅查询当前会话登录态，幂等且无副作用
    public ResponseEntity<ReaderResponse> me(HttpSession session) { // session 由容器注入
        Object user = session.getAttribute(SESSION_READER_KEY); // 从 Session 读取读者属性，可能为 null
        if (user instanceof Reader reader) { // 类型守卫：仅当属性为 Reader 时才视为已登录，防止篡改
            return ResponseEntity.ok(ReaderResponse.from(reader)); // 转换为脱敏响应 DTO 返回 200
        }
        return ResponseEntity.status(401).build(); // 未登录或类型不符返回 401，前端引导至登录页
    }

    /**
     * 查询全部读者（需管理员登录）
     *
     * @return 读者列表
     */
    @GetMapping // 映射 GET /api/readers，查询为幂等读操作，由拦截器放行
    public ResponseEntity<List<ReaderResponse>> list() { // 无参数，返回全部读者
        return ResponseEntity.ok(readerService.getAllReaders()); // 委托服务层查询并以 200 返回
    }

    /**
     * 按姓名检索读者
     *
     * @param name 姓名关键字
     * @return 匹配的读者列表
     */
    @GetMapping("/search") // 映射 GET /api/readers/search，检索为读操作公开访问
    public ResponseEntity<List<ReaderResponse>> search(@RequestParam(required = false) String name) { // 关键字可选，为空时由服务层降级返回全部
        return ResponseEntity.ok(readerService.searchReaders(name)); // 委托服务层模糊检索并以 200 返回
    }

    /**
     * 按 ID 查询读者
     *
     * @param id 读者 ID
     * @return 读者响应（404 若不存在）
     */
    @GetMapping("/{id}") // 映射 GET /api/readers/{id}，按主键查询资源
    public ResponseEntity<ReaderResponse> getById(@PathVariable Long id) { // 绑定路径变量到读者 ID
        ReaderResponse resp = readerService.getReaderById(id); // 委托服务层按 ID 查询，可能返回 null
        if (resp == null) { // 读者不存在场景
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，符合 RESTful 资源缺失语义
        }
        return ResponseEntity.ok(resp); // 读者存在则返回 200 与读者响应
    }

    /**
     * 新增读者（需管理员登录）
     *
     * @param req 读者请求
     * @return 新建的读者响应（400 参数校验失败或学号重复）
     */
    @PostMapping // 映射 POST /api/readers，新增资源用 POST，写操作由拦截器校验管理员登录态
    public ResponseEntity<ReaderResponse> create(@Valid @RequestBody ReaderRequest req) { // @Valid 触发请求体校验
        try {
            return ResponseEntity.ok(readerService.createReader(req)); // 委托服务层创建并以 200 返回新建读者
        } catch (IllegalArgumentException e) {
            // 参数业务校验失败（如学号重复、格式不合规）
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，表示请求参数业务校验未通过
        }
    }

    /**
     * 修改读者（需管理员登录）
     *
     * @param id  读者 ID
     * @param req 读者请求
     * @return 更新后的读者响应
     */
    @PutMapping("/{id}") // 映射 PUT /api/readers/{id}，整体更新资源用 PUT，写操作由拦截器校验管理员登录态
    public ResponseEntity<ReaderResponse> update(@PathVariable Long id, @Valid @RequestBody ReaderRequest req) { // 路径变量指定更新目标，@Valid 校验请求体
        try {
            return ResponseEntity.ok(readerService.updateReader(id, req)); // 委托服务层更新并以 200 返回更新后读者
        } catch (IllegalArgumentException e) {
            // 读者不存在时服务层抛出参数异常
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示待更新资源不存在
        }
    }

    /**
     * 删除读者（需管理员登录）
     *
     * @param id 读者 ID
     * @return 204（成功） / 404（不存在）
     */
    @DeleteMapping("/{id}") // 映射 DELETE /api/readers/{id}，删除资源用 DELETE，写操作由拦截器校验管理员登录态
    public ResponseEntity<Void> delete(@PathVariable Long id) { // 路径变量指定删除目标，无响应体
        try {
            readerService.deleteReader(id); // 委托服务层删除读者
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        } catch (IllegalArgumentException e) {
            // 读者不存在时服务层抛出参数异常
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示待删除资源不存在
        }
    }
}

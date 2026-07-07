package com.example.java5.controller; // 声明包路径，归属 controller（控制层）

// ===== DTO 导入 =====
import com.example.java5.dto.JobPostingResponse; // 响应 DTO
import com.example.java5.dto.JobRequest;         // 请求 DTO

// ===== 枚举类型导入，用于查询参数绑定 =====
import com.example.java5.model.EducationLevel;   // 学历枚举
import com.example.java5.model.ExperienceLevel;  // 经验枚举
import com.example.java5.model.JobCategory;      // 分类枚举

// ===== 服务层导入 =====
import com.example.java5.service.JobService;          // 岗位业务服务
import com.example.java5.service.V2exJobSyncService;  // V2EX 同步服务

// ===== Spring Web 注解 =====
import org.springframework.http.ResponseEntity;          // 响应实体，可携带状态码
import org.springframework.web.bind.annotation.DeleteMapping; // DELETE 映射
import org.springframework.web.bind.annotation.GetMapping;    // GET 映射
import org.springframework.web.bind.annotation.PathVariable;  // 路径变量绑定
import org.springframework.web.bind.annotation.PostMapping;   // POST 映射
import org.springframework.web.bind.annotation.PutMapping;    // PUT 映射
import org.springframework.web.bind.annotation.RequestBody;   // 请求体绑定
import org.springframework.web.bind.annotation.RequestMapping;// 类级路由
import org.springframework.web.bind.annotation.RequestParam;  // 查询参数绑定
import org.springframework.web.bind.annotation.RestController; // REST 控制器

// ===== JDK 容器 =====
import java.util.List; // 列表容器
import java.util.Map;  // 键值对容器

/**
 * 招聘岗位 REST 控制器
 * <p>
 * 职责：暴露岗位 CRUD 与统计、V2EX 同步的 HTTP 接口，
 * 负责参数绑定与响应封装，不包含业务逻辑（委托给 Service）。
 * </p>
 * 设计要点：
 * 1. @RestController = @Controller + @ResponseBody，所有方法返回值自动序列化为 JSON。
 * 2. 类级 @RequestMapping("/api/jobs") 统一前缀，方法级只写子路径。
 * 3. 查询类用 GET，创建用 POST，更新用 PUT，删除用 DELETE，符合 RESTful 语义。
 */
@RestController // 声明为 REST 控制器，返回值自动转 JSON
@RequestMapping("/api/jobs") // 类级路由前缀，所有接口均以 /api/jobs 开头
public class JobController {

    /** 岗位业务服务，处理 CRUD 与统计 */
    private final JobService jobService;
    /** V2EX 同步服务，拉取 V2EX 酷工作数据 */
    private final V2exJobSyncService v2exJobSyncService;

    /**
     * 构造函数注入两个服务
     * <p>
     * 为什么同时注入两个服务：Controller 是请求入口，
     * 需要协调岗位业务和 V2EX 同步两类操作。
     * </p>
     *
     * @param jobService       岗位业务服务
     * @param v2exJobSyncService V2EX 同步服务
     */
    public JobController(JobService jobService, V2exJobSyncService v2exJobSyncService) {
        this.jobService = jobService;                       // 赋值岗位服务
        this.v2exJobSyncService = v2exJobSyncService;       // 赋值同步服务
    }

    /**
     * 多条件搜索岗位列表
     * <p>
     * 职责：接收前端筛选参数，透传给 Service 查询并返回岗位列表。
     * </p>
     * 为什么所有参数 required=false：支持无筛选全量查询，前端不传则不过滤。
     *
     * @param keyword    关键词（标题/公司/技能）
     * @param city       城市
     * @param category   分类枚举
     * @param experience 经验枚举
     * @param education  学历枚举
     * @param salaryMin  薪资下限
     * @return 岗位列表 JSON
     */
    @GetMapping // GET /api/jobs
    public List<JobPostingResponse> search(
            @RequestParam(required = false) String keyword,          // 关键词，可空
            @RequestParam(required = false) String city,             // 城市，可空
            @RequestParam(required = false) JobCategory category,    // 分类，可空（自动枚举转换）
            @RequestParam(required = false) ExperienceLevel experience, // 经验，可空
            @RequestParam(required = false) EducationLevel education,   // 学历，可空
            @RequestParam(required = false) Integer salaryMin) {        // 薪资下限，可空
        return jobService.search(keyword, city, category, experience, education, salaryMin); // 委托服务查询
    }

    /**
     * 获取首页统计数据
     * <p>
     * 职责：返回总数及按分类/城市/来源的分组统计，供前端仪表盘渲染。
     * </p>
     *
     * @return 统计 Map
     */
    @GetMapping("/stats") // GET /api/jobs/stats
    public Map<String, Object> stats() {
        return jobService.stats(); // 委托服务统计
    }

    /**
     * 根据 ID 查询岗位详情
     * <p>
     * 职责：查询单条岗位，不存在返回 404。
     * </p>
     * 为什么用 ResponseEntity：需要根据存在与否返回 200 或 404。
     *
     * @param id 岗位 ID
     * @return 200+DTO 或 404
     */
    @GetMapping("/{id}") // GET /api/jobs/{id}
    public ResponseEntity<JobPostingResponse> get(@PathVariable Long id) { // 从路径取 id
        JobPostingResponse resp = jobService.get(id); // 查询
        if (resp == null) {                           // 不存在
            return ResponseEntity.notFound().build(); // 返回 404
        }
        return ResponseEntity.ok(resp);               // 返回 200 + DTO
    }

    /**
     * 创建新岗位
     * <p>
     * 职责：接收发布表单 JSON，委托 Service 创建并返回新岗位 DTO。
     * </p>
     * 前端发布岗位的入口，publisherName、contact 由表单携带。
     *
     * @param req 创建请求 DTO
     * @return 已持久化的岗位 DTO（含 ID）
     */
    @PostMapping // POST /api/jobs
    public JobPostingResponse create(@RequestBody JobRequest req) { // @RequestBody 绑定 JSON 到 record
        return jobService.create(req); // 委托服务创建
    }

    /**
     * 更新已有岗位
     * <p>
     * 职责：按 ID 更新岗位，不存在返回 404。
     * </p>
     *
     * @param id  岗位 ID
     * @param req 更新请求 DTO
     * @return 200+DTO 或 404
     */
    @PutMapping("/{id}") // PUT /api/jobs/{id}
    public ResponseEntity<JobPostingResponse> update(@PathVariable Long id, @RequestBody JobRequest req) {
        JobPostingResponse resp = jobService.update(id, req); // 委托服务更新
        if (resp == null) {                                   // 不存在
            return ResponseEntity.notFound().build();         // 返回 404
        }
        return ResponseEntity.ok(resp);                       // 返回 200 + DTO
    }

    /**
     * 删除岗位
     * <p>
     * 职责：按 ID 删除，成功返回 204，不存在返回 404。
     * </p>
     * 为什么成功用 204：删除成功无响应体，204 No Content 语义更准确。
     *
     * @param id 岗位 ID
     * @return 204 或 404
     */
    @DeleteMapping("/{id}") // DELETE /api/jobs/{id}
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (jobService.delete(id)) {             // 删除成功
            return ResponseEntity.noContent().build(); // 返回 204
        }
        return ResponseEntity.notFound().build();      // 不存在返回 404
    }

    /**
     * 触发 V2EX 酷工作数据同步
     * <p>
     * 职责：调用同步服务拉取 V2EX 最新主题并入库，返回新增数量。
     * </p>
     * 为什么用 POST 而非 GET：同步是写操作（会入库），GET 应为幂等无副作用。
     *
     * @return 新增数量，如 {"inserted": 10}
     */
    @PostMapping("/sync-v2ex") // POST /api/jobs/sync-v2ex
    public Map<String, Integer> syncV2ex() {
        int inserted = v2exJobSyncService.syncLatest(); // 调用同步
        return Map.of("inserted", inserted);            // 返回新增数
    }
}

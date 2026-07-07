package com.example.java6.controller; // 声明当前类所在的包路径，属于举报中心模块的 Controller 层

import com.example.java6.dto.ReportHandleRequest; // 导入举报处置请求 DTO，封装处置结果、处置备注等字段（后台用）
import com.example.java6.dto.ReportRequest; // 导入举报提交请求 DTO，封装举报类型、URL、描述、举报人信息（前台用）
import com.example.java6.dto.ReportResponse; // 导入举报响应 DTO，封装返回前端的字段（避免暴露实体类内部细节）
import com.example.java6.model.ReportStatus; // 导入举报状态枚举，定义举报处理流程状态（如待处理、已处理等）
import com.example.java6.service.ReportService; // 导入举报服务层接口，封装举报提交、查询、处置、统计等业务逻辑
import org.springframework.http.ResponseEntity; // 导入 Spring 响应实体，可携带 HTTP 状态码与响应体返回
import org.springframework.web.bind.annotation.DeleteMapping; // 导入 DELETE 请求映射注解，标记删除资源接口
import org.springframework.web.bind.annotation.GetMapping; // 导入 GET 请求映射注解，标记查询类接口
import org.springframework.web.bind.annotation.PathVariable; // 导入路径变量绑定注解，从 URL 路径中提取参数
import org.springframework.web.bind.annotation.PostMapping; // 导入 POST 请求映射注解，标记创建资源接口
import org.springframework.web.bind.annotation.PutMapping; // 导入 PUT 请求映射注解，标记更新资源接口
import org.springframework.web.bind.annotation.RequestBody; // 导入请求体绑定注解，将 JSON 请求体反序列化为 Java 对象
import org.springframework.web.bind.annotation.RequestMapping; // 导入路径映射注解，声明 Controller 基础路径
import org.springframework.web.bind.annotation.RequestParam; // 导入查询参数绑定注解，从 URL query string 中提取参数
import org.springframework.web.bind.annotation.RestController; // 导入 REST 控制器注解，标识此类返回 JSON 而非视图

import java.util.List; // 导入 List 集合，用于返回举报列表
import java.util.Map; // 导入 Map 集合，用于返回统计数据（键值对结构）

/**
 * 举报记录 REST 控制器
 *
 * <p>提供举报提交、查询、处置以及统计接口。
 * 提交接口供前台公众使用，处置与删除接口供后台管理使用。</p>
 */
@RestController // 标记为 REST 控制器，所有方法返回值默认转为 JSON 响应体
@RequestMapping("/api/reports") // 基础路径 /api/reports，所有举报相关接口都挂在此路径下，遵循 RESTful 资源命名（使用复数形式）
public class ReportController {

    private final ReportService reportService; // 通过构造器注入举报服务，声明为 final 保证不可变（线程安全）

    public ReportController(ReportService reportService) { // 构造器注入：Spring 自动装配 ReportService 实现类
        this.reportService = reportService; // 完成字段赋值
    }

    /**
     * 查询举报列表（可按状态过滤）
     *
     * @param status 处理状态（可选）
     * @return 举报列表
     */
    @GetMapping // GET /api/reports，后台管理列表页调用此接口查询举报记录（GET 请求由拦截器放行）
    public List<ReportResponse> list(@RequestParam(required = false) ReportStatus status) { // @RequestParam 从 query string 取状态参数；required=false 表示该参数可选，不传则返回全部
        return reportService.list(status); // 调用服务层按状态过滤查询，返回举报列表
    }

    /**
     * 举报数据统计
     *
     * @return 统计结果
     */
    @GetMapping("/stats") // GET /api/reports/stats，后台首页仪表盘调用此接口获取举报数据统计概览
    public Map<String, Object> stats() { // 无需参数，返回各状态数量、总数等统计键值对
        return reportService.stats(); // 调用服务层聚合统计举报数据，返回 Map 结构便于前端灵活读取
    }

    /**
     * 获取举报详情
     *
     * @param id 举报 ID
     * @return 举报详情
     */
    @GetMapping("/{id}") // GET /api/reports/{id}，根据举报 ID 查询举报详情，后台处理详情页使用
    public ResponseEntity<ReportResponse> get(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取举报 ID
        ReportResponse resp = reportService.get(id); // 调用服务层按主键查询举报详情
        if (resp == null) { // 服务层返回 null 表示该 ID 对应的举报不存在
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，告知前端资源不存在
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带举报详情
    }

    /**
     * 提交举报（前台公众用）
     *
     * @param req 举报请求
     * @return 提交后的举报
     */
    @PostMapping // POST /api/reports，前台公众提交举报信息（写操作，但拦截器对此特定路径放行，允许未登录用户举报）
    public ReportResponse submit(@RequestBody ReportRequest req) { // @RequestBody 从请求体解析 JSON 为 ReportRequest
        return reportService.submit(req); // 调用服务层保存举报记录并返回新建后的举报（含生成的 ID 与初始状态）
    }

    /**
     * 处置举报（后台管理用）
     *
     * @param id  举报 ID
     * @param req 处置请求
     * @return 处置后的举报
     */
    @PutMapping("/{id}") // PUT /api/reports/{id}，后台管理员处置指定 ID 的举报（写操作，需登录）
    public ResponseEntity<ReportResponse> handle(@PathVariable Long id, @RequestBody ReportHandleRequest req) { // @PathVariable 取举报 ID；@RequestBody 取处置信息
        ReportResponse resp = reportService.handle(id, req); // 调用服务层更新举报状态与处置备注
        if (resp == null) { // 返回 null 表示该 ID 举报不存在，无法处置
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带处置后的举报
    }

    /**
     * 删除举报记录
     *
     * @param id 举报 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}") // DELETE /api/reports/{id}，后台管理员删除指定 ID 的举报记录（写操作，需登录）
    public ResponseEntity<Void> delete(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取举报 ID
        if (reportService.delete(id)) { // 调用服务层删除举报记录，返回 true 表示删除成功
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        }
        return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示该 ID 举报不存在
    }
}

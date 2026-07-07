package com.example.java6.controller; // 声明当前类所在的包路径，属于政策法规模块的 Controller 层

import com.example.java6.dto.RegulationRequest; // 导入法规创建/更新请求 DTO，封装标题、颁布机构、生效日期等字段
import com.example.java6.dto.RegulationResponse; // 导入法规响应 DTO，封装返回前端的字段（避免暴露实体类内部细节）
import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，定义法规的层级类别（如法律、行政法规、部门规章等）
import com.example.java6.service.RegulationService; // 导入法规服务层接口，封装法规增删改查业务逻辑
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

import java.util.List; // 导入 List 集合，用于返回法规列表

/**
 * 政策法规 REST 控制器
 *
 * <p>提供政策法规的查询、详情、增删改接口。</p>
 */
@RestController // 标记为 REST 控制器，所有方法返回值默认转为 JSON 响应体
@RequestMapping("/api/regulations") // 基础路径 /api/regulations，所有政策法规接口都挂在此路径下，遵循 RESTful 资源命名（使用复数形式）
public class RegulationController {

    private final RegulationService regulationService; // 通过构造器注入法规服务，声明为 final 保证不可变（线程安全）

    public RegulationController(RegulationService regulationService) { // 构造器注入：Spring 自动装配 RegulationService 实现类
        this.regulationService = regulationService; // 完成字段赋值
    }

    /**
     * 查询法规列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 法规列表
     */
    @GetMapping // GET /api/regulations，前台官网与后台管理列表页均调用此接口查询政策法规
    public List<RegulationResponse> list(@RequestParam(required = false) RegulationCategory category) { // @RequestParam 从 query string 取分类参数；required=false 表示该参数可选，不传则返回全部
        return regulationService.list(category); // 调用服务层按分类过滤查询，返回法规列表
    }

    /**
     * 查询最新法规（首页用）
     *
     * @return 最新法规列表
     */
    @GetMapping("/latest") // GET /api/regulations/latest，前台首页展示最新若干条政策法规
    public List<RegulationResponse> latest() { // 无需任何参数，服务层内部固定返回最新 N 条
        return regulationService.latest(); // 调用服务层按颁布日期倒序取最新法规列表
    }

    /**
     * 获取法规详情
     *
     * @param id 法规 ID
     * @return 法规详情
     */
    @GetMapping("/{id}") // GET /api/regulations/{id}，根据法规 ID 查询法规详情，前台详情页与后台编辑页共用
    public ResponseEntity<RegulationResponse> get(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取法规 ID
        RegulationResponse resp = regulationService.get(id); // 调用服务层按主键查询法规详情
        if (resp == null) { // 服务层返回 null 表示该 ID 对应的法规不存在
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，告知前端资源不存在
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带法规详情
    }

    /**
     * 创建法规
     *
     * @param req 创建请求
     * @return 创建后的法规
     */
    @PostMapping // POST /api/regulations，后台管理员创建政策法规（写操作，由 LoginInterceptor 校验登录态）
    public RegulationResponse create(@RequestBody RegulationRequest req) { // @RequestBody 从请求体解析 JSON 为 RegulationRequest
        return regulationService.create(req); // 调用服务层创建法规并返回新建后的法规（含生成的 ID）
    }

    /**
     * 更新法规
     *
     * @param id  法规 ID
     * @param req 更新请求
     * @return 更新后的法规
     */
    @PutMapping("/{id}") // PUT /api/regulations/{id}，后台管理员更新指定 ID 的法规（写操作，需登录）
    public ResponseEntity<RegulationResponse> update(@PathVariable Long id, @RequestBody RegulationRequest req) { // @PathVariable 取法规 ID；@RequestBody 取更新内容
        RegulationResponse resp = regulationService.update(id, req); // 调用服务层按 ID 更新法规
        if (resp == null) { // 返回 null 表示该 ID 法规不存在，无法更新
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带更新后的法规
    }

    /**
     * 删除法规
     *
     * @param id 法规 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}") // DELETE /api/regulations/{id}，后台管理员删除指定 ID 的法规（写操作，需登录）
    public ResponseEntity<Void> delete(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取法规 ID
        if (regulationService.delete(id)) { // 调用服务层删除法规，返回 true 表示删除成功
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        }
        return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示该 ID 法规不存在
    }
}

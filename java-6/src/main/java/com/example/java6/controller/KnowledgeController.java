package com.example.java6.controller; // 声明当前类所在的包路径，属于安全知识模块的 Controller 层

import com.example.java6.dto.KnowledgeRequest; // 导入知识文章创建/更新请求 DTO，封装标题、分类、正文等字段
import com.example.java6.dto.KnowledgeResponse; // 导入知识文章响应 DTO，封装返回前端的字段（避免暴露实体类内部细节）
import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，定义知识文章的业务类别（如防护技巧、案例分析等）
import com.example.java6.service.KnowledgeService; // 导入知识服务层接口，封装知识文章增删改查业务逻辑
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

import java.util.List; // 导入 List 集合，用于返回知识文章列表

/**
 * 安全知识文章 REST 控制器
 *
 * <p>提供安全知识文章的查询、详情、增删改接口。</p>
 */
@RestController // 标记为 REST 控制器，所有方法返回值默认转为 JSON 响应体
@RequestMapping("/api/knowledge") // 基础路径 /api/knowledge，所有安全知识文章接口都挂在此路径下，遵循 RESTful 资源命名
public class KnowledgeController {

    private final KnowledgeService knowledgeService; // 通过构造器注入知识服务，声明为 final 保证不可变（线程安全）

    public KnowledgeController(KnowledgeService knowledgeService) { // 构造器注入：Spring 自动装配 KnowledgeService 实现类
        this.knowledgeService = knowledgeService; // 完成字段赋值
    }

    /**
     * 查询知识文章列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 文章列表
     */
    @GetMapping // GET /api/knowledge，前台官网与后台管理列表页均调用此接口查询知识文章
    public List<KnowledgeResponse> list(@RequestParam(required = false) KnowledgeCategory category) { // @RequestParam 从 query string 取分类参数；required=false 表示该参数可选，不传则返回全部
        return knowledgeService.list(category); // 调用服务层按分类过滤查询，返回知识文章列表
    }

    /**
     * 查询最新文章（首页用）
     *
     * @return 最新文章列表
     */
    @GetMapping("/latest") // GET /api/knowledge/latest，前台首页展示最新若干篇安全知识文章
    public List<KnowledgeResponse> latest() { // 无需任何参数，服务层内部固定返回最新 N 篇
        return knowledgeService.latest(); // 调用服务层按发布时间倒序取最新知识文章列表
    }

    /**
     * 获取文章详情
     *
     * @param id 文章 ID
     * @return 文章详情
     */
    @GetMapping("/{id}") // GET /api/knowledge/{id}，根据文章 ID 查询文章详情，前台详情页与后台编辑页共用
    public ResponseEntity<KnowledgeResponse> get(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取文章 ID
        KnowledgeResponse resp = knowledgeService.get(id); // 调用服务层按主键查询知识文章详情
        if (resp == null) { // 服务层返回 null 表示该 ID 对应的文章不存在
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，告知前端资源不存在
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带文章详情
    }

    /**
     * 创建知识文章
     *
     * @param req 创建请求
     * @return 创建后的文章
     */
    @PostMapping // POST /api/knowledge，后台管理员创建知识文章（写操作，由 LoginInterceptor 校验登录态）
    public KnowledgeResponse create(@RequestBody KnowledgeRequest req) { // @RequestBody 从请求体解析 JSON 为 KnowledgeRequest
        return knowledgeService.create(req); // 调用服务层创建知识文章并返回新建后的文章（含生成的 ID）
    }

    /**
     * 更新知识文章
     *
     * @param id  文章 ID
     * @param req 更新请求
     * @return 更新后的文章
     */
    @PutMapping("/{id}") // PUT /api/knowledge/{id}，后台管理员更新指定 ID 的知识文章（写操作，需登录）
    public ResponseEntity<KnowledgeResponse> update(@PathVariable Long id, @RequestBody KnowledgeRequest req) { // @PathVariable 取文章 ID；@RequestBody 取更新内容
        KnowledgeResponse resp = knowledgeService.update(id, req); // 调用服务层按 ID 更新知识文章
        if (resp == null) { // 返回 null 表示该 ID 文章不存在，无法更新
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带更新后的文章
    }

    /**
     * 删除知识文章
     *
     * @param id 文章 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}") // DELETE /api/knowledge/{id}，后台管理员删除指定 ID 的知识文章（写操作，需登录）
    public ResponseEntity<Void> delete(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取文章 ID
        if (knowledgeService.delete(id)) { // 调用服务层删除知识文章，返回 true 表示删除成功
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        }
        return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示该 ID 文章不存在
    }
}

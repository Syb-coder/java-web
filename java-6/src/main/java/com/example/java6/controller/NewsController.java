package com.example.java6.controller; // 声明当前类所在的包路径，属于新闻资讯模块的 Controller 层

import com.example.java6.dto.NewsRequest; // 导入新闻创建/更新请求 DTO，封装标题、分类、正文等字段
import com.example.java6.dto.NewsResponse; // 导入新闻响应 DTO，封装返回前端的新闻字段（避免暴露实体类内部细节）
import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，定义新闻的业务类别（如安全动态、漏洞预警等）
import com.example.java6.service.NewsService; // 导入新闻服务层接口，封装新闻增删改查业务逻辑
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

import java.util.List; // 导入 List 集合，用于返回新闻列表

/**
 * 新闻资讯 REST 控制器
 *
 * <p>提供新闻资讯的查询、详情、增删改接口，供前台官网与后台管理共用。</p>
 */
@RestController // 标记为 REST 控制器，所有方法返回值默认转为 JSON 响应体
@RequestMapping("/api/news") // 基础路径 /api/news，所有新闻资讯接口都挂在此路径下，遵循 RESTful 资源命名
public class NewsController {

    private final NewsService newsService; // 通过构造器注入新闻服务，声明为 final 保证不可变（线程安全）

    public NewsController(NewsService newsService) { // 构造器注入：Spring 自动装配 NewsService 实现类
        this.newsService = newsService; // 完成字段赋值
    }

    /**
     * 查询新闻列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 新闻列表
     */
    @GetMapping // GET /api/news，前台首页与后台管理列表页均调用此接口查询新闻
    public List<NewsResponse> list(@RequestParam(required = false) NewsCategory category) { // @RequestParam 从 query string 取分类参数；required=false 表示该参数可选，不传则返回全部
        return newsService.list(category); // 调用服务层按分类过滤查询，返回新闻列表（无分页，适合数据量较小场景）
    }

    /**
     * 查询最新新闻（首页用）
     *
     * @return 最新新闻列表
     */
    @GetMapping("/latest") // GET /api/news/latest，前台首页展示最新若干条新闻动态
    public List<NewsResponse> latest() { // 无需任何参数，服务层内部固定返回最新 N 条
        return newsService.latest(); // 调用服务层按发布时间倒序取最新新闻列表
    }

    /**
     * 查询置顶新闻
     *
     * @return 置顶新闻列表
     */
    @GetMapping("/top") // GET /api/news/top，前台首页展示置顶新闻，便于突出重要资讯
    public List<NewsResponse> top() { // 无需参数，查询所有 is_top=true 的新闻
        return newsService.topNews(); // 调用服务层查询置顶新闻列表
    }

    /**
     * 获取新闻详情
     *
     * @param id 新闻 ID
     * @return 新闻详情
     */
    @GetMapping("/{id}") // GET /api/news/{id}，根据新闻 ID 查询新闻详情，前台详情页与后台编辑页共用
    public ResponseEntity<NewsResponse> get(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取新闻 ID
        NewsResponse resp = newsService.get(id); // 调用服务层按主键查询新闻详情
        if (resp == null) { // 服务层返回 null 表示该 ID 对应的新闻不存在
            return ResponseEntity.notFound().build(); // 返回 404 Not Found，告知前端资源不存在
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带新闻详情
    }

    /**
     * 创建新闻
     *
     * @param req 创建请求
     * @return 创建后的新闻
     */
    @PostMapping // POST /api/news，后台管理员创建新闻（写操作，由 LoginInterceptor 校验登录态）
    public NewsResponse create(@RequestBody NewsRequest req) { // @RequestBody 从请求体解析 JSON 为 NewsRequest
        return newsService.create(req); // 调用服务层创建新闻并返回新建后的新闻（含生成的 ID）
    }

    /**
     * 更新新闻
     *
     * @param id  新闻 ID
     * @param req 更新请求
     * @return 更新后的新闻
     */
    @PutMapping("/{id}") // PUT /api/news/{id}，后台管理员更新指定 ID 的新闻（写操作，需登录）
    public ResponseEntity<NewsResponse> update(@PathVariable Long id, @RequestBody NewsRequest req) { // @PathVariable 取新闻 ID；@RequestBody 取更新内容
        NewsResponse resp = newsService.update(id, req); // 调用服务层按 ID 更新新闻
        if (resp == null) { // 返回 null 表示该 ID 新闻不存在，无法更新
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带更新后的新闻
    }

    /**
     * 删除新闻
     *
     * @param id 新闻 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}") // DELETE /api/news/{id}，后台管理员删除指定 ID 的新闻（写操作，需登录）
    public ResponseEntity<Void> delete(@PathVariable Long id) { // @PathVariable 从 URL 路径中提取新闻 ID
        if (newsService.delete(id)) { // 调用服务层删除新闻，返回 true 表示删除成功
            return ResponseEntity.noContent().build(); // 返回 204 No Content，表示删除成功且无响应体
        }
        return ResponseEntity.notFound().build(); // 返回 404 Not Found，表示该 ID 新闻不存在
    }
}

// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.ArticleListItem;
import com.example.java2.dto.ArticleRequest;
import com.example.java2.dto.ArticleResponse;
import com.example.java2.model.User;
import com.example.java2.service.ArticleService;

// 导入 Spring Web 注解
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 文章控制器
 * <p>
 * 提供前台文章列表/详情/搜索/收藏/点赞接口，以及后台文章 CRUD 接口。
 * 写操作（收藏/点赞）需用户登录，CRUD 需管理员登录（由拦截器保证）。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/articles 以"文章"资源集合为根，
// 前台接口直接挂在根路径下（GET 列表/详情、POST 收藏/点赞），后台 CRUD 挂在 /admin 子路径下区分权限边界
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 前台文章列表分页查询
     *
     * @param categoryId 分类 ID（可选）
     * @param keyword    关键词（可选）
     * @param page       页码（0 起）
     * @param size       每页条数
     * @return 文章列表项分页
     */
    @GetMapping
    // GET 查询资源集合：无副作用幂等读，支持分页与筛选参数，符合 RESTful 用 GET 表达列表获取的约定
    public ResponseEntity<Page<ArticleListItem>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // categoryId 与 keyword 均为可选，支持"全量浏览 / 按分类筛选 / 关键词搜索"三种模式复用同一接口
        // page 默认 0 起始，遵循 Spring Data Pageable 约定，避免前端做 +1 偏移
        // 200 OK 携带分页结果返回
        return ResponseEntity.ok(articleService.listForUser(categoryId, keyword, page, size));
    }

    /**
     * 文章详情查询（前台）
     * <p>每次访问自动累加阅读量。</p>
     *
     * @param id     文章 ID
     * @param user   当前登录用户（未登录为 null）
     * @return 文章详情响应
     */
    @GetMapping("/{id}")
    // GET 查询单个资源：/{id} 路径变量定位具体文章，符合 RESTful 用路径表达资源定位的约定
    public ResponseEntity<ArticleResponse> detail(
            @PathVariable Long id,
            // @SessionAttribute 工作原理：Spring MVC 从 HttpSession 按 key 取属性注入参数，
            // required = false 让未登录用户进入方法体注入 null，使前台详情接口对匿名用户可见
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        try {
            // 未登录传 null userId，service 据此判断是否记录"已登录用户阅读历史"
            Long userId = user != null ? user.getId() : null;
            // 200 OK 携带文章详情返回
            return ResponseEntity.ok(articleService.getDetail(id, userId));
        } catch (org.springframework.web.server.ResponseStatusException e) {
            // 文章不存在时 service 抛 ResponseStatusException，透传其状态码（如 404）
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    /**
     * 收藏文章（需登录）
     *
     * @param id     文章 ID
     * @param user   当前登录用户
     * @return 200（成功） / 401（未登录） / 400（文章不存在）
     */
    @PostMapping("/{id}/favorite")
    // POST 表达"收藏"动作执行：/{id}/favorite 子路径表达对文章资源的收藏子操作，符合 RESTful 子资源约定
    public ResponseEntity<Void> favorite(@PathVariable Long id,
                                         @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验之一（方法内判断）：拦截器虽对非 GET 请求要求登录，但收藏接口路径不在 admin 子路径下，
        // 拦截器走 requireUser 分支已校验；此处二次判 null 防御拦截器规则调整或 Session 过期的边界场景
        if (user == null) {
            // 401 Unauthorized 表示未登录，前端据此引导登录
            return ResponseEntity.status(401).build();
        }
        try {
            articleService.favorite(user.getId(), id);
            // 200 OK 表示收藏成功
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 文章不存在等参数错误返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 取消收藏（需登录）
     */
    @DeleteMapping("/{id}/favorite")
    // DELETE 表达"取消收藏"：与 POST /{id}/favorite 形成对称操作对，DELETE 移除收藏关联资源
    public ResponseEntity<Void> unfavorite(@PathVariable Long id,
                                           @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：未登录返回 401，防御拦截器规则遗漏
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        articleService.unfavorite(user.getId(), id);
        // 200 OK 表示取消收藏成功
        return ResponseEntity.ok().build();
    }

    /**
     * 点赞文章（需登录）
     */
    @PostMapping("/{id}/like")
    // POST 表达"点赞"动作执行：/{id}/like 子路径表达对文章资源的点赞子操作，与 favorite 模式一致
    public ResponseEntity<Void> like(@PathVariable Long id,
                                     @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：未登录返回 401
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            articleService.like(user.getId(), id);
            // 200 OK 表示点赞成功
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 文章不存在返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 取消点赞（需登录）
     */
    @DeleteMapping("/{id}/like")
    // DELETE 表达"取消点赞"：与 POST /{id}/like 形成对称操作对，DELETE 移除点赞关联资源
    public ResponseEntity<Void> unlike(@PathVariable Long id,
                                       @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：未登录返回 401
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        articleService.unlike(user.getId(), id);
        // 200 OK 表示取消点赞成功
        return ResponseEntity.ok().build();
    }

    /**
     * 查询当前用户收藏的文章列表（个人中心用，需登录）
     *
     * @param user 当前登录用户
     * @return 收藏文章列表
     */
    @GetMapping("/favorites")
    // GET 查询当前用户收藏列表：路径 /favorites 表达"我的收藏"子资源集合，符合 RESTful 子资源约定
    public ResponseEntity<java.util.List<ArticleListItem>> favorites(
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：收藏列表属个人数据，未登录返回 401，防止匿名枚举他人收藏
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // 200 OK 携带收藏文章列表返回
        return ResponseEntity.ok(articleService.listFavorites(user.getId()));
    }

    // ===== 后台管理接口（/api/articles/admin/**） =====

    /**
     * 后台文章列表（含未发布）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 文章列表项分页
     */
    @GetMapping("/admin")
    // GET 后台列表查询：/admin 子路径区分后台权限边界，由拦截器 requireAdmin 强制校验管理员登录
    public ResponseEntity<Page<ArticleListItem>> adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 200 OK 携带含未发布文章的分页结果返回（前台 listForUser 仅返回已发布）
        return ResponseEntity.ok(articleService.listForAdmin(page, size));
    }

    /**
     * 后台根据 ID 查询文章（编辑回显，含未发布）
     *
     * @param id 文章 ID
     * @return 文章实体（不存在返回 404）
     */
    @GetMapping("/admin/{id}")
    // GET 后台查询单个文章：编辑回显用，含未发布草稿，/{id} 定位具体资源
    public ResponseEntity<com.example.java2.model.Article> adminGet(@PathVariable Long id) {
        com.example.java2.model.Article a = articleService.getById(id);
        if (a == null) {
            // 文章不存在返回 404
            return ResponseEntity.notFound().build();
        }
        // 200 OK 携带文章完整实体返回（含未发布字段，供编辑回显）
        return ResponseEntity.ok(a);
    }

    /**
     * 新增文章（管理员）
     *
     * @param req 文章请求
     * @return 新建文章实体
     */
    @PostMapping("/admin")
    // POST 后台新增文章：向 /admin 集合根 POST 创建新文章，符合 RESTful 用 POST 表达资源创建的约定
    public ResponseEntity<com.example.java2.model.Article> adminCreate(@Valid @RequestBody ArticleRequest req) {
        // @Valid 触发 Bean Validation，校验 ArticleRequest 字段约束，失败返回 400
        try {
            // 200 OK 携带新建文章实体返回
            return ResponseEntity.ok(articleService.create(req));
        } catch (IllegalArgumentException e) {
            // 参数错误（如分类不存在）返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新文章（管理员）
     */
    @PutMapping("/admin/{id}")
    // PUT 后台更新文章：向 /admin/{id} PUT 整体替换文章属性，符合 RESTful 用 PUT 表达资源完整更新的约定
    public ResponseEntity<com.example.java2.model.Article> adminUpdate(
            @PathVariable Long id, @Valid @RequestBody ArticleRequest req) {
        try {
            // 200 OK 携带更新后的文章实体返回
            return ResponseEntity.ok(articleService.update(id, req));
        } catch (IllegalArgumentException e) {
            // 参数错误（如文章不存在或分类无效）返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除文章（管理员）
     */
    @DeleteMapping("/admin/{id}")
    // DELETE 后台删除文章：向 /admin/{id} DELETE 移除文章，符合 RESTful 用 DELETE 表达资源删除的约定
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        try {
            articleService.delete(id);
            // 204 No Content 表示删除成功且无响应体，符合 RESTful 删除资源语义
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // 文章不存在返回 404
            return ResponseEntity.notFound().build();
        }
    }
}

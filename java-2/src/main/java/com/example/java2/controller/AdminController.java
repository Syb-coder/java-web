// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与 Service
import com.example.java2.dto.StatsResponse;
import com.example.java2.dto.UserResponse;
import com.example.java2.service.ArticleService;
import com.example.java2.service.CategoryService;
import com.example.java2.service.CommentService;
import com.example.java2.service.QuestionService;
import com.example.java2.service.TestService;
import com.example.java2.service.UserService;

// 导入 Spring Web 注解
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 导入集合类
import java.util.List;

/**
 * 后台管理控制器
 * <p>
 * 提供仪表板统计、用户管理（列表/启用/禁用）接口。
 * 所有接口均由 {@code LoginInterceptor} 强制要求管理员登录。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/admin 以"后台管理"资源域为根，
// 子路径 stats/users 表达仪表板统计与用户管理子操作，所有接口均由拦截器 requireAdmin 强制校验管理员登录
@RequestMapping("/api/admin")
public class AdminController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final QuestionService questionService;
    private final CommentService commentService;
    private final TestService testService;

    public AdminController(ArticleService articleService,
                           CategoryService categoryService,
                           UserService userService,
                           QuestionService questionService,
                           CommentService commentService,
                           TestService testService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.questionService = questionService;
        this.commentService = commentService;
        this.testService = testService;
    }

    /**
     * 仪表板统计
     *
     * @return 平台核心数据概览
     */
    @GetMapping("/stats")
    // GET 查询仪表板统计：幂等读操作，聚合各业务模块的核心指标，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<StatsResponse> stats() {
        // 200 OK 携带统计概览返回：已发布文章数、分类数、用户数、题目数、评论数、总阅读量、答题次数
        return ResponseEntity.ok(new StatsResponse(
                articleService.countPublished(),
                categoryService.count(),
                userService.count(),
                questionService.count(),
                commentService.count(),
                articleService.totalViewCount(),
                testService.count()
        ));
    }

    /**
     * 用户列表（后台管理用）
     *
     * @return 用户响应列表
     */
    @GetMapping("/users")
    // GET 查询用户列表：幂等读操作，供后台管理员审核用户，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<List<UserResponse>> users() {
        // 200 OK 携带全量用户脱敏列表返回（UserResponse 不含密码字段）
        return ResponseEntity.ok(userService.listAll());
    }

    /**
     * 启用/禁用用户
     *
     * @param id      用户 ID
     * @param enabled 是否启用
     * @return 200（成功） / 404（用户不存在）
     */
    @PutMapping("/users/{id}/enabled")
    // PUT 更新用户启用状态：向 /users/{id}/enabled PUT 修改用户启用标志，符合 RESTful 用 PUT 表达资源部分更新的约定
    // /enabled 子路径表达"启用状态"这一子资源，避免与用户主体资源的 PUT 更新混淆
    public ResponseEntity<Void> setUserEnabled(@PathVariable Long id,
                                               @RequestParam boolean enabled) {
        boolean ok = userService.setEnabled(id, enabled);
        // 三元运算：成功返回 200 OK，用户不存在返回 404 Not Found
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

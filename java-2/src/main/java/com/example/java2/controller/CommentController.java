// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.CommentRequest;
import com.example.java2.dto.CommentResponse;
import com.example.java2.model.User;
import com.example.java2.service.CommentService;

// 导入 Spring Web 注解
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 评论控制器
 * <p>
 * 提供按文章查询评论、发表评论（需登录）、后台评论列表与删除接口。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/comments 以"评论"资源集合为根，
// 前台发表评论直接挂在根路径，后台审核删除挂在 /admin 子路径下区分权限边界
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 按文章查询评论分页
     *
     * @param articleId 文章 ID
     * @param page      页码
     * @param size      每页条数
     * @return 评论分页
     */
    @GetMapping
    // GET 查询资源集合：无副作用幂等读，按 articleId 筛选某文章的评论分页，符合 RESTful 用 GET 表达列表获取的约定
    public ResponseEntity<Page<CommentResponse>> listByArticle(
            @RequestParam Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 200 OK 携带评论分页结果返回
        return ResponseEntity.ok(commentService.listByArticle(articleId, page, size));
    }

    /**
     * 发表评论（需登录）
     *
     * @param articleId 文章 ID
     * @param req       评论请求
     * @param user      当前登录用户
     * @return 评论响应
     */
    @PostMapping
    // POST 新增资源：发表评论即"创建评论"，符合 RESTful 用 POST 表达资源创建的约定
    public ResponseEntity<CommentResponse> publish(
            @RequestParam Long articleId,
            @Valid @RequestBody CommentRequest req,
            // @SessionAttribute 工作原理：Spring MVC 从 HttpSession 按 key 取属性注入参数，
            // required = false 让未登录用户进入方法体注入 null，便于方法内统一返回 401
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：评论需归属登录用户，未登录返回 401，确保评论作者可追溯
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // 200 OK 携带新评论响应返回（严格 RESTful 应返回 201 Created，此处简化统一用 200）
        return ResponseEntity.ok(commentService.publish(articleId, user.getId(), req));
    }

    // ===== 后台管理接口 =====

    /**
     * 后台评论列表分页（按时间倒序）
     */
    @GetMapping("/admin")
    // GET 后台评论列表：/admin 子路径区分后台权限边界，由拦截器 requireAdmin 强制校验管理员登录
    public ResponseEntity<Page<CommentResponse>> adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 200 OK 携带全量评论分页返回（按时间倒序，供管理员审核）
        return ResponseEntity.ok(commentService.listAll(page, size));
    }

    /**
     * 删除评论（管理员审核）
     *
     * @param id 评论 ID
     * @return 204（成功） / 404（评论不存在）
     */
    @DeleteMapping("/admin/{id}")
    // DELETE 后台删除评论：向 /admin/{id} DELETE 移除违规评论，符合 RESTful 用 DELETE 表达资源删除的约定
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        try {
            commentService.delete(id);
            // 204 No Content 表示删除成功且无响应体，符合 RESTful 删除资源语义
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // 评论不存在返回 404
            return ResponseEntity.notFound().build();
        }
    }
}

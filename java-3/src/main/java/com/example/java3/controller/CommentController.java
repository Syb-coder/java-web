// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入评论请求 DTO
import com.example.java3.dto.CommentRequest;
// 导入评论响应 DTO
import com.example.java3.dto.CommentResponse;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入评论服务
import com.example.java3.service.CommentService;
// 导入 HttpSession
import jakarta.servlet.http.HttpSession;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 HttpStatus
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 List
import java.util.List;
// 导入 Map
import java.util.Map;

/**
 * 评论控制器
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/products/{productId}/comments")：嵌套路径，评论归属于某商品
// 路径中的 {productId} 自动绑定到方法参数 @PathVariable Long productId
@RequestMapping("/api/products/{productId}/comments")
public class CommentController {

    // 注入的评论服务
    private final CommentService commentService;

    // 构造器注入评论服务
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 查询商品评论列表
     *
     * @param productId 商品 ID
     * @return 评论列表
     */
    // @GetMapping：处理 GET /api/products/{productId}/comments 请求
    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long productId) {
        // 调用服务查询该商品的全部评论
        List<CommentResponse> list = commentService.listByProduct(productId);
        // 返回 200 OK 和评论列表
        return ResponseEntity.ok(list);
    }

    /**
     * 发表评论
     *
     * @param productId 商品 ID
     * @param req       评论请求
     * @param session   会话
     * @return 评论响应
     */
    // @PostMapping：处理 POST /api/products/{productId}/comments 请求
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long productId,
                                    @Valid @RequestBody CommentRequest req,
                                    HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务发表评论，关联商品 ID 与用户 ID
            return ResponseEntity.ok(commentService.create(productId, u.getId(), req));
        } catch (IllegalArgumentException e) {
            // 商品不存在等，返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入分类请求 DTO
import com.example.java3.dto.CategoryRequest;
// 导入分类响应 DTO
import com.example.java3.dto.CategoryResponse;
// 导入商品请求 DTO
import com.example.java3.dto.ProductRequest;
// 导入商品响应 DTO
import com.example.java3.dto.ProductResponse;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入分类服务
import com.example.java3.service.CategoryService;
// 导入商品服务
import com.example.java3.service.ProductService;
// 导入 HttpSession
import jakarta.servlet.http.HttpSession;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 Page，分页结果
import org.springframework.data.domain.Page;
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
 * 商品控制器
 * <p>
 * 提供商品发布、查询、点赞收藏、我的发布等接口。
 * </p>
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/products")：基础路径 /api/products
@RequestMapping("/api/products")
public class ProductController {

    // 注入的商品服务
    private final ProductService productService;
    // 注入的分类服务
    private final CategoryService categoryService;

    // 构造器注入两个服务
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * 分页查询已审核通过的商品（前台展示）
     *
     * @param page       页码（0 起）
     * @param size       每页数量
     * @param categoryId 分类 ID（可选）
     * @param keyword    关键词（可选）
     * @return 商品分页
     */
    // @GetMapping：处理 GET /api/products 请求（无子路径）
    @GetMapping
    // @RequestParam(defaultValue=...)：参数缺省时使用默认值
    // required = false：参数可选
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) String keyword) {
        // 调用服务分页查询已审核通过的商品
        Page<ProductResponse> result = productService.listApproved(page, size, categoryId, keyword);
        // 返回 200 OK 和分页结构（content/totalElements/totalPages/page）
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "page", page
        ));
    }

    /**
     * 商品详情
     *
     * @param id 商品 ID
     * @return 商品详情
     */
    // @GetMapping("/{id}")：处理 GET /api/products/{id} 请求
    // @PathVariable：从路径提取商品 ID
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            // 返回 200 OK 和商品详情
            return ResponseEntity.ok(productService.detail(id));
        } catch (IllegalArgumentException e) {
            // 商品不存在，返回 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 发布商品（需登录）
     *
     * @param req     商品请求
     * @param session 会话
     * @return 商品响应
     */
    // @PostMapping：处理 POST /api/products 请求
    @PostMapping
    public ResponseEntity<?> publish(@Valid @RequestBody ProductRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务发布商品，关联卖家 ID
            return ResponseEntity.ok(productService.publish(u.getId(), req));
        } catch (IllegalArgumentException e) {
            // 参数非法（分类不存在等），返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 我的发布
     *
     * @param session 会话
     * @return 商品列表
     */
    // @GetMapping("/mine")：处理 GET /api/products/mine 请求
    @GetMapping("/mine")
    public ResponseEntity<?> myProducts(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和当前用户发布的商品列表
        return ResponseEntity.ok(productService.myProducts(u.getId()));
    }

    /**
     * 删除自己的商品
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 空响应
     */
    // @DeleteMapping("/{id}")：处理 DELETE /api/products/{id} 请求
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 仅卖家本人可删除（管理员删除走 /api/admin/products/{id}/takeDown）
        try {
            // 查询商品详情以校验卖家
            ProductResponse p = productService.detail(id);
            // 不是本人商品，禁止删除
            if (!p.getSellerId().equals(u.getId())) {
                // 返回 403 Forbidden
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "无权删除他人商品"));
            }
            // 调用服务删除商品
            productService.delete(id);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "已删除"));
        } catch (IllegalArgumentException e) {
            // 商品不存在，返回 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 点赞商品（已点赞则取消）
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 当前是否已点赞
     */
    // @PostMapping("/{id}/like")：处理 POST /api/products/{id}/like 请求
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 切换点赞状态（已点赞取消，未点赞则点赞）
        boolean liked = productService.toggleLike(u.getId(), id);
        // 返回 200 OK 和当前点赞状态
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    /**
     * 收藏商品（已收藏则取消）
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 当前是否已收藏
     */
    // @PostMapping("/{id}/favorite")：处理 POST /api/products/{id}/favorite 请求
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 切换收藏状态
        boolean favorited = productService.toggleFavorite(u.getId(), id);
        // 返回 200 OK 和当前收藏状态
        return ResponseEntity.ok(Map.of("favorited", favorited));
    }

    /**
     * 查询当前用户对某商品的点赞/收藏状态
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 状态 map
     */
    // @GetMapping("/{id}/interactions")：处理 GET /api/products/{id}/interactions 请求
    @GetMapping("/{id}/interactions")
    public ResponseEntity<?> interactions(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录返回默认 false 状态（允许浏览）
            return ResponseEntity.ok(Map.of("liked", false, "favorited", false));
        }
        // 返回 200 OK 和当前用户的点赞/收藏状态
        return ResponseEntity.ok(productService.userInteractions(u.getId(), id));
    }

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    // @GetMapping("/categories")：处理 GET /api/products/categories 请求
    @GetMapping("/categories")
    public ResponseEntity<?> categories() {
        // 返回 200 OK 和全部分类
        return ResponseEntity.ok(categoryService.findAll());
    }
}

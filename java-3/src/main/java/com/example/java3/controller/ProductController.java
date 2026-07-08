package com.example.java3.controller;

import com.example.java3.dto.CategoryRequest;
import com.example.java3.dto.CategoryResponse;
import com.example.java3.dto.ProductRequest;
import com.example.java3.dto.ProductResponse;
import com.example.java3.model.User;
import com.example.java3.service.CategoryService;
import com.example.java3.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * <p>
 * 提供商品发布、查询、点赞收藏、我的发布等接口。
 * </p>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

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
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) String keyword) {
        Page<ProductResponse> result = productService.listApproved(page, size, categoryId, keyword);
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
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.detail(id));
        } catch (IllegalArgumentException e) {
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
    @PostMapping
    public ResponseEntity<?> publish(@Valid @RequestBody ProductRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(productService.publish(u.getId(), req));
        } catch (IllegalArgumentException e) {
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
    @GetMapping("/mine")
    public ResponseEntity<?> myProducts(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(productService.myProducts(u.getId()));
    }

    /**
     * 删除自己的商品
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 仅卖家本人可删除（管理员删除走 /api/admin/products/{id}/takeDown）
        try {
            ProductResponse p = productService.detail(id);
            if (!p.getSellerId().equals(u.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "无权删除他人商品"));
            }
            productService.delete(id);
            return ResponseEntity.ok(Map.of("message", "已删除"));
        } catch (IllegalArgumentException e) {
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
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        boolean liked = productService.toggleLike(u.getId(), id);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    /**
     * 收藏商品（已收藏则取消）
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 当前是否已收藏
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        boolean favorited = productService.toggleFavorite(u.getId(), id);
        return ResponseEntity.ok(Map.of("favorited", favorited));
    }

    /**
     * 查询当前用户对某商品的点赞/收藏状态
     *
     * @param id      商品 ID
     * @param session 会话
     * @return 状态 map
     */
    @GetMapping("/{id}/interactions")
    public ResponseEntity<?> interactions(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.ok(Map.of("liked", false, "favorited", false));
        }
        return ResponseEntity.ok(productService.userInteractions(u.getId(), id));
    }

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<?> categories() {
        return ResponseEntity.ok(categoryService.findAll());
    }
}

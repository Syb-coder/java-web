package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器
import com.example.java12.dto.ApiResponse;  // 统一响应
import com.example.java12.dto.PostRequest;  // 帖子请求
import com.example.java12.dto.PostResponse;  // 帖子响应
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.PostService;  // 帖子服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.validation.Valid;  // 参数校验
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.web.bind.annotation.*;  // Web 注解

import java.util.List;  // 列表
import java.util.Map;  // Map

/**
 * 帖子控制器
 * <p>
 * 提供帖子的查询、发布、编辑、删除、点赞、收藏、搜索等接口。
 * GET 请求公开（游客可浏览），写操作需登录。
 * </p>
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    /** 帖子服务 */
    private final PostService postService;

    /**
     * 构造器注入
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 查询最新帖子（公开）
     *
     * @param page 页码（从0开始，默认0）
     * @param size 每页条数（默认20）
     * @return 帖子分页列表
     */
    @GetMapping("/latest")
    public ApiResponse<Page<PostResponse>> findLatest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(postService.findLatest(page, size));
    }

    /**
     * 查询热门帖子（公开）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 帖子分页列表
     */
    @GetMapping("/hot")
    public ApiResponse<Page<PostResponse>> findHot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(postService.findHot(page, size));
    }

    /**
     * 查询置顶帖子（公开）
     *
     * @return 置顶帖子列表
     */
    @GetMapping("/top")
    public ApiResponse<List<PostResponse>> findTopPosts() {
        return ApiResponse.success(postService.findTopPosts());
    }

    /**
     * 搜索帖子（公开）
     *
     * @param keyword   关键词
     * @param plateId   板块 ID
     * @param timeRange 时间范围：today/week/month/all
     * @param sortBy    排序方式：hot/time
     * @param page      页码
     * @param size      每页条数
     * @return 帖子分页列表
     */
    @GetMapping("/search")
    public ApiResponse<Page<PostResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long plateId,
            @RequestParam(defaultValue = "all") String timeRange,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(postService.search(keyword, plateId, timeRange, sortBy, page, size));
    }

    /**
     * 查询指定板块的帖子（公开）
     *
     * @param plateId 板块 ID
     * @param page    页码
     * @param size    每页条数
     * @return 帖子分页列表
     */
    @GetMapping("/plate/{plateId}")
    public ApiResponse<Page<PostResponse>> findByPlate(
            @PathVariable Long plateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(postService.findByPlate(plateId, page, size));
    }

    /**
     * 查询帖子详情（公开）
     * <p>
     * 如果用户已登录，返回是否已点赞/已收藏状态。
     * </p>
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 帖子详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> findById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(postService.findById(id, currentUser));
    }

    /**
     * 发布帖子（需登录）
     *
     * @param req     帖子请求
     * @param request HTTP 请求
     * @return 新发布的帖子
     */
    @PostMapping
    public ApiResponse<PostResponse> create(@Valid @RequestBody PostRequest req,
                                             HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("发布成功", postService.create(req, currentUser));
    }

    /**
     * 编辑帖子（需登录，仅作者可编辑）
     *
     * @param id      帖子 ID
     * @param req     帖子请求
     * @param request HTTP 请求
     * @return 更新后的帖子
     */
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody PostRequest req,
                                             HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("修改成功", postService.update(id, req, currentUser));
    }

    /**
     * 删除帖子（需登录，作者或管理员可删除）
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        postService.delete(id, currentUser, request.getRemoteAddr());
        return ApiResponse.success("帖子已删除", null);
    }

    /**
     * 点赞/取消点赞（需登录，幂等切换）
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 操作结果（liked: true已点赞 / false已取消）
     */
    @PostMapping("/{id}/like")
    public ApiResponse<Map<String, Boolean>> toggleLike(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        boolean liked = postService.toggleLike(id, currentUser);
        return ApiResponse.success(liked ? "点赞成功" : "已取消点赞", Map.of("liked", liked));
    }

    /**
     * 收藏/取消收藏（需登录，幂等切换）
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 操作结果（collected: true已收藏 / false已取消）
     */
    @PostMapping("/{id}/collect")
    public ApiResponse<Map<String, Boolean>> toggleCollect(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        boolean collected = postService.toggleCollect(id, currentUser);
        return ApiResponse.success(collected ? "收藏成功" : "已取消收藏", Map.of("collected", collected));
    }
}

package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器
import com.example.java12.dto.*;  // 全部 DTO
import com.example.java12.model.Moderator;  // 版主关联实体
import com.example.java12.model.OperateLog;  // 操作日志实体
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.OperateLogService;  // 操作日志服务
import com.example.java12.service.PlateService;  // 板块服务
import com.example.java12.service.PostService;  // 帖子服务
import com.example.java12.service.UserService;  // 用户服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.validation.Valid;  // 参数校验
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.PageRequest;  // 分页请求
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.web.bind.annotation.*;  // Web 注解

import java.util.List;  // 列表

/**
 * 后台管理控制器
 * <p>
 * 提供管理员专属接口：板块 CRUD、用户封禁/解封、版主分配/回收、操作日志查询、站点统计。
 * 所有接口需 ADMIN 角色（拦截器已校验 /api/admin/** 路径权限）。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** 板块服务 */
    private final PlateService plateService;
    /** 用户服务 */
    private final UserService userService;
    /** 帖子服务（用于站点统计） */
    private final PostService postService;
    /** 操作日志服务 */
    private final OperateLogService operateLogService;

    /**
     * 构造器注入
     */
    public AdminController(PlateService plateService, UserService userService,
                           PostService postService, OperateLogService operateLogService) {
        this.plateService = plateService;
        this.userService = userService;
        this.postService = postService;
        this.operateLogService = operateLogService;
    }

    // ===== 板块管理 =====

    /**
     * 创建板块
     *
     * @param req     板块请求（名称、描述、图标、排序）
     * @param request HTTP 请求
     * @return 新建板块
     */
    @PostMapping("/plates")
    public ApiResponse<PlateResponse> createPlate(@Valid @RequestBody PlateRequest req,
                                                   HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("板块创建成功", plateService.create(req, admin, request.getRemoteAddr()));
    }

    /**
     * 编辑板块
     *
     * @param id      板块 ID
     * @param req     板块请求
     * @param request HTTP 请求
     * @return 更新后的板块
     */
    @PutMapping("/plates/{id}")
    public ApiResponse<PlateResponse> updatePlate(@PathVariable Long id,
                                                   @Valid @RequestBody PlateRequest req,
                                                   HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("板块修改成功", plateService.update(id, req, admin, request.getRemoteAddr()));
    }

    /**
     * 删除板块
     * <p>
     * 板块下有帖子时拒绝删除，需先迁移或删除帖子。
     * </p>
     *
     * @param id      板块 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @DeleteMapping("/plates/{id}")
    public ApiResponse<Void> deletePlate(@PathVariable Long id, HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        plateService.delete(id, admin, request.getRemoteAddr());
        return ApiResponse.success("板块已删除", null);
    }

    // ===== 用户管理 =====

    /**
     * 分页查询全部用户
     *
     * @param page 页码（从0开始）
     * @param size 每页条数
     * @return 用户分页列表（不含密码字段）
     */
    @GetMapping("/users")
    public ApiResponse<Page<UserResponse>> findAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 将 User 实体转换为 UserResponse，避免暴露密码字段
        Page<UserResponse> userResponses = userService.findAll(pageable).map(UserResponse::new);
        return ApiResponse.success(userResponses);
    }

    /**
     * 封禁用户
     *
     * @param id      被封禁用户 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PutMapping("/users/{id}/ban")
    public ApiResponse<Void> banUser(@PathVariable Long id, HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        userService.banUser(id, admin, request.getRemoteAddr());
        return ApiResponse.success("用户已封禁", null);
    }

    /**
     * 解封用户
     *
     * @param id      被解封用户 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PutMapping("/users/{id}/unban")
    public ApiResponse<Void> unbanUser(@PathVariable Long id, HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        userService.unbanUser(id, admin, request.getRemoteAddr());
        return ApiResponse.success("用户已解封", null);
    }

    // ===== 版主管理 =====

    /**
     * 分配版主
     *
     * @param req     分配版主请求（用户ID + 板块ID）
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PostMapping("/moderators")
    public ApiResponse<Void> assignModerator(@Valid @RequestBody AssignModeratorRequest req,
                                              HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        userService.assignModerator(req, admin, request.getRemoteAddr());
        return ApiResponse.success("版主分配成功", null);
    }

    /**
     * 回收版主权限
     *
     * @param userId  版主用户 ID
     * @param plateId 板块 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @DeleteMapping("/moderators")
    public ApiResponse<Void> removeModerator(@RequestParam Long userId,
                                              @RequestParam Long plateId,
                                              HttpServletRequest request) {
        User admin = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        userService.removeModerator(userId, plateId, admin, request.getRemoteAddr());
        return ApiResponse.success("版主权限已回收", null);
    }

    /**
     * 查询指定板块的版主列表
     *
     * @param plateId 板块 ID
     * @return 版主关联列表
     */
    @GetMapping("/plates/{plateId}/moderators")
    public ApiResponse<List<Moderator>> findModeratorsByPlate(@PathVariable Long plateId) {
        return ApiResponse.success(userService.findModeratorsByPlateId(plateId));
    }

    // ===== 操作日志 =====

    /**
     * 分页查询操作日志（按时间倒序）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 操作日志分页列表
     */
    @GetMapping("/logs")
    public ApiResponse<Page<OperateLog>> findLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(operateLogService.findAll(pageable));
    }

    // ===== 站点统计 =====

    /**
     * 站点统计数据（后台仪表盘）
     *
     * @return 统计数据（用户数、帖子数、评论数、板块数、今日新帖）
     */
    @GetMapping("/stats")
    public ApiResponse<StatsResponse> getStats() {
        return ApiResponse.success(postService.getStats());
    }
}

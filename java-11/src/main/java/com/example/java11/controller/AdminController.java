package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.PostResponse;
import com.example.java11.dto.ReportResponse;
import com.example.java11.dto.SectionResponse;
import com.example.java11.dto.StatsResponse;
import com.example.java11.dto.UserResponse;
import com.example.java11.model.AdminUser;
import com.example.java11.model.Comment;
import com.example.java11.model.OperationLog;
import com.example.java11.model.SensitiveWord;
import com.example.java11.model.SystemConfig;
import com.example.java11.service.AdminService;
import com.example.java11.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员控制器
 * <p>
 * 统一封装管理后台全部接口，覆盖用户管理、帖子审核、内容管理、
 * 板块管理、数据统计、操作日志、系统配置与敏感词管理。
 * 写操作由 {@link LoginInterceptor} 强制校验管理员登录态，
 * 并在操作成功后自动记录操作日志。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** 管理员聚合服务 */
    private final AdminService adminService;

    /** 认证服务，用于读取当前管理员上下文 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param adminService 管理员聚合服务
     * @param authService  认证服务
     */
    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    // ==================== 用户管理 ====================

    /**
     * 获取全部用户列表
     *
     * @return 用户响应列表
     */
    @GetMapping("/users")
    public ApiResponse getAllUsers() {
        List<UserResponse> list = adminService.getAllUsers();
        return ApiResponse.success(list);
    }

    /**
     * 搜索用户
     *
     * @param keyword 搜索关键词（匹配用户名）
     * @return 匹配的用户响应列表
     */
    @GetMapping("/users/search")
    public ApiResponse searchUsers(@RequestParam String keyword) {
        List<UserResponse> list = adminService.searchUsers(keyword);
        return ApiResponse.success(list);
    }

    /**
     * 封禁用户
     *
     * @param id      用户 ID
     * @param session HTTP 会话，用于获取操作人
     * @param request HTTP 请求，用于获取 IP
     * @return 统一包装结果
     */
    @PutMapping("/users/{id}/ban")
    public ApiResponse banUser(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.banUser(id);
        logAction(session, request, "BAN_USER", "USER", id, "封禁用户ID:" + id);
        return ApiResponse.success("已封禁", null);
    }

    /**
     * 解封用户
     *
     * @param id      用户 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/users/{id}/unban")
    public ApiResponse unbanUser(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.unbanUser(id);
        logAction(session, request, "UNBAN_USER", "USER", id, "解封用户ID:" + id);
        return ApiResponse.success("已解封", null);
    }

    // ==================== 帖子审核 ====================

    /**
     * 获取待审核帖子列表
     *
     * @return 待审核帖子响应列表
     */
    @GetMapping("/posts/pending")
    public ApiResponse getPendingPosts() {
        List<PostResponse> list = adminService.getPendingPosts();
        return ApiResponse.success(list);
    }

    /**
     * 审核通过
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/posts/{id}/approve")
    public ApiResponse approvePost(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.approvePost(id);
        logAction(session, request, "APPROVE_POST", "POST", id, "审核通过帖子ID:" + id);
        return ApiResponse.success("已审核通过", null);
    }

    /**
     * 审核拒绝
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/posts/{id}/reject")
    public ApiResponse rejectPost(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.rejectPost(id);
        logAction(session, request, "REJECT_POST", "POST", id, "审核拒绝帖子ID:" + id);
        return ApiResponse.success("已拒绝", null);
    }

    /**
     * 删除帖子（逻辑删除）
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @DeleteMapping("/posts/{id}")
    public ApiResponse deletePost(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.deletePost(id);
        logAction(session, request, "DELETE_POST", "POST", id, "删除帖子ID:" + id);
        return ApiResponse.success("帖子已删除", null);
    }

    /**
     * 切换帖子置顶状态
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/posts/{id}/top")
    public ApiResponse toggleTop(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.toggleTop(id);
        logAction(session, request, "TOGGLE_TOP", "POST", id, "切换置顶状态,帖子ID:" + id);
        return ApiResponse.success("置顶状态已切换", null);
    }

    /**
     * 切换帖子加精状态
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/posts/{id}/essence")
    public ApiResponse toggleEssence(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.toggleEssence(id);
        logAction(session, request, "TOGGLE_ESSENCE", "POST", id, "切换加精状态,帖子ID:" + id);
        return ApiResponse.success("加精状态已切换", null);
    }

    // ==================== 内容管理 ====================

    /**
     * 获取全部评论（管理员视角，包含已删除）
     *
     * @return 评论实体列表
     */
    @GetMapping("/comments")
    public ApiResponse getAllComments() {
        List<Comment> list = adminService.getAllComments();
        return ApiResponse.success(list);
    }

    /**
     * 删除评论（逻辑删除）
     *
     * @param id      评论 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @DeleteMapping("/comments/{id}")
    public ApiResponse deleteComment(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.deleteComment(id);
        logAction(session, request, "DELETE_COMMENT", "COMMENT", id, "删除评论ID:" + id);
        return ApiResponse.success("评论已删除", null);
    }

    /**
     * 获取待处理举报列表
     *
     * @return 待处理举报响应列表
     */
    @GetMapping("/reports")
    public ApiResponse getPendingReports() {
        List<ReportResponse> list = adminService.getPendingReports();
        return ApiResponse.success(list);
    }

    /**
     * 处理举报（成立）
     *
     * @param id          举报 ID
     * @param handleRemark 处理备注
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/reports/{id}/resolve")
    public ApiResponse resolveReport(@PathVariable Long id,
                                     @RequestParam(required = false) String handleRemark,
                                     HttpSession session, HttpServletRequest request) {
        adminService.resolveReport(id, handleRemark);
        logAction(session, request, "RESOLVE_REPORT", "REPORT", id, "处理举报ID:" + id + ",备注:" + handleRemark);
        return ApiResponse.success("举报已处理", null);
    }

    /**
     * 驳回举报（不成立）
     *
     * @param id          举报 ID
     * @param handleRemark 处理备注
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/reports/{id}/reject")
    public ApiResponse rejectReport(@PathVariable Long id,
                                    @RequestParam(required = false) String handleRemark,
                                    HttpSession session, HttpServletRequest request) {
        adminService.rejectReport(id, handleRemark);
        logAction(session, request, "REJECT_REPORT", "REPORT", id, "驳回举报ID:" + id + ",备注:" + handleRemark);
        return ApiResponse.success("举报已驳回", null);
    }

    // ==================== 板块管理 ====================

    /**
     * 创建板块
     *
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 创建后的板块响应
     */
    @PostMapping("/sections")
    public ApiResponse createSection(@RequestParam String name,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String icon,
                                     HttpSession session, HttpServletRequest request) {
        SectionResponse response = adminService.createSection(name, description, icon);
        logAction(session, request, "CREATE_SECTION", "SECTION", null, "创建板块:" + name);
        return ApiResponse.success("板块创建成功", response);
    }

    /**
     * 更新板块
     *
     * @param id          板块 ID
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 更新后的板块响应
     */
    @PutMapping("/sections/{id}")
    public ApiResponse updateSection(@PathVariable Long id,
                                     @RequestParam String name,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) String icon,
                                     HttpSession session, HttpServletRequest request) {
        SectionResponse response = adminService.updateSection(id, name, description, icon);
        logAction(session, request, "UPDATE_SECTION", "SECTION", id, "更新板块ID:" + id + ",名称:" + name);
        return ApiResponse.success("板块更新成功", response);
    }

    /**
     * 删除板块
     *
     * @param id      板块 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @DeleteMapping("/sections/{id}")
    public ApiResponse deleteSection(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.deleteSection(id);
        logAction(session, request, "DELETE_SECTION", "SECTION", id, "删除板块ID:" + id);
        return ApiResponse.success("板块已删除", null);
    }

    /**
     * 设置版主
     *
     * @param id          板块 ID
     * @param moderatorId 版主（管理员）ID
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/sections/{id}/moderator")
    public ApiResponse setModerator(@PathVariable Long id, @RequestParam Long moderatorId,
                                    HttpSession session, HttpServletRequest request) {
        adminService.setModerator(id, moderatorId);
        logAction(session, request, "SET_MODERATOR", "SECTION", id, "设置板块ID:" + id + "版主:" + moderatorId);
        return ApiResponse.success("版主已设置", null);
    }

    // ==================== 数据统计 ====================

    /**
     * 获取站点统计数据
     *
     * @return 统计数据响应
     */
    @GetMapping("/stats")
    public ApiResponse getStats() {
        StatsResponse stats = adminService.getStats();
        return ApiResponse.success(stats);
    }

    // ==================== 操作日志 ====================

    /**
     * 获取操作日志列表
     *
     * @return 操作日志列表
     */
    @GetMapping("/logs")
    public ApiResponse getOperationLogs() {
        List<OperationLog> list = adminService.getOperationLogs();
        return ApiResponse.success(list);
    }

    // ==================== 系统配置 ====================

    /**
     * 获取全部系统配置
     *
     * @return 系统配置列表
     */
    @GetMapping("/configs")
    public ApiResponse getAllConfigs() {
        List<SystemConfig> list = adminService.getAllConfigs();
        return ApiResponse.success(list);
    }

    /**
     * 设置配置项（不存在则创建）
     *
     * @param key         配置键
     * @param value       配置值
     * @param description 配置说明
     * @param session     HTTP 会话
     * @param request     HTTP 请求
     * @return 统一包装结果
     */
    @PutMapping("/configs/{key}")
    public ApiResponse setConfig(@PathVariable String key,
                                 @RequestParam String value,
                                 @RequestParam(required = false) String description,
                                 HttpSession session, HttpServletRequest request) {
        adminService.setConfig(key, value, description);
        logAction(session, request, "SET_CONFIG", "CONFIG", null, "设置配置:" + key + "=" + value);
        return ApiResponse.success("配置已更新", null);
    }

    // ==================== 敏感词管理 ====================

    /**
     * 获取敏感词列表
     *
     * @return 敏感词列表
     */
    @GetMapping("/sensitive-words")
    public ApiResponse getSensitiveWords() {
        List<SensitiveWord> list = adminService.getSensitiveWords();
        return ApiResponse.success(list);
    }

    /**
     * 添加敏感词
     *
     * @param word     敏感词内容
     * @param category 分类
     * @param session  HTTP 会话
     * @param request  HTTP 请求
     * @return 包含创建后敏感词实体的统一包装结果
     */
    @PostMapping("/sensitive-words")
    public ApiResponse addSensitiveWord(@RequestParam String word,
                                        @RequestParam(required = false) String category,
                                        HttpSession session, HttpServletRequest request) {
        SensitiveWord sensitiveWord = adminService.addSensitiveWord(word, category);
        logAction(session, request, "ADD_SENSITIVE_WORD", "SENSITIVE_WORD", sensitiveWord.getId(), "添加敏感词:" + word);
        return ApiResponse.success("敏感词已添加", sensitiveWord);
    }

    /**
     * 删除敏感词
     *
     * @param id      敏感词 ID
     * @param session HTTP 会话
     * @param request HTTP 请求
     * @return 统一包装结果
     */
    @DeleteMapping("/sensitive-words/{id}")
    public ApiResponse deleteSensitiveWord(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        adminService.deleteSensitiveWord(id);
        logAction(session, request, "DELETE_SENSITIVE_WORD", "SENSITIVE_WORD", id, "删除敏感词ID:" + id);
        return ApiResponse.success("敏感词已删除", null);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 统一记录管理员操作日志
     * <p>
     * 从 session 获取当前管理员 ID，从 request 获取客户端 IP，
     * 构造 OperationLog 实体并持久化。日志记录失败不影响主业务流程。
     * </p>
     *
     * @param session    HTTP 会话，用于获取操作人
     * @param request    HTTP 请求，用于获取 IP
     * @param action     操作动作（如 BAN_USER、APPROVE_POST）
     * @param targetType 目标类型（如 USER、POST、COMMENT）
     * @param targetId   目标 ID，可为 null
     * @param detail     操作详情描述
     */
    private void logAction(HttpSession session, HttpServletRequest request,
                           String action, String targetType, Long targetId, String detail) {
        try {
            AdminUser admin = authService.getCurrentAdmin(session);
            OperationLog logEntry = new OperationLog();
            logEntry.setOperatorId(admin != null ? admin.getId() : null);
            logEntry.setOperatorType("ADMIN");
            logEntry.setAction(action);
            logEntry.setTargetType(targetType);
            logEntry.setTargetId(targetId);
            logEntry.setDetail(detail);
            logEntry.setIp(getClientIp(request));
            logEntry.setCreatedAt(LocalDateTime.now());
            adminService.log(logEntry);
        } catch (Exception e) {
            // 日志记录失败不影响主业务流程，仅打印错误
            System.err.println("操作日志记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户端真实 IP 地址
     * <p>
     * 优先从反向代理头 X-Forwarded-For 获取，不存在时取 X-Real-IP，
     * 最终回退到 request.getRemoteAddr()。
     * </p>
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");  // 优先取代理头
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");  // 次选真实 IP 头
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();  // 回退到远程地址
        }
        // X-Forwarded-For 可能含多个 IP，取第一个（即客户端真实 IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.PostResponse;
import com.example.java11.dto.ReportResponse;
import com.example.java11.dto.SectionResponse;
import com.example.java11.dto.StatsResponse;
import com.example.java11.dto.TagResponse;
import com.example.java11.dto.UserResponse;
import com.example.java11.model.Comment;
import com.example.java11.model.OperationLog;
import com.example.java11.model.PostStatus;
import com.example.java11.model.ReportStatus;
import com.example.java11.model.SensitiveWord;
import com.example.java11.model.SystemConfig;
import com.example.java11.model.Tag;
import com.example.java11.repository.CommentRepository;
import com.example.java11.repository.OperationLogRepository;
import com.example.java11.repository.PostRepository;
import com.example.java11.repository.ReportRepository;
import com.example.java11.repository.SensitiveWordRepository;
import com.example.java11.repository.SystemConfigRepository;
import com.example.java11.repository.TagRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务（聚合服务）
 * <p>
 * 统一封装管理后台所需的全部业务操作，包括用户管理、帖子审核、内容管理、
 * 板块管理、数据统计、操作日志、系统配置及敏感词管理。
 * 内部通过注入其他 Service 复用已有逻辑，同时直接访问 Repository 处理
 * AdminService 独有的查询（如统计、日志、配置等），避免代码重复。
 * </p>
 */
@Service
public class AdminService {

    /** 用户服务，委托处理用户管理操作 */
    private final UserService userService;

    /** 帖子服务，委托处理帖子审核操作 */
    private final PostService postService;

    /** 评论服务，委托处理评论删除操作 */
    private final CommentService commentService;

    /** 板块服务，委托处理板块管理操作 */
    private final SectionService sectionService;

    /** 举报服务，委托处理举报处理操作 */
    private final ReportService reportService;

    /** 用户数据访问层，用于统计用户总数与今日新增 */
    private final UserRepository userRepository;

    /** 帖子数据访问层，用于统计帖子总数、待审核数与今日新增 */
    private final PostRepository postRepository;

    /** 评论数据访问层，用于获取全部评论与统计评论总数 */
    private final CommentRepository commentRepository;

    /** 举报数据访问层，用于统计待处理举报数 */
    private final ReportRepository reportRepository;

    /** 标签数据访问层，用于获取热门标签 */
    private final TagRepository tagRepository;

    /** 操作日志数据访问层，用于记录与查询管理员操作 */
    private final OperationLogRepository operationLogRepository;

    /** 系统配置数据访问层，用于读写站点全局配置 */
    private final SystemConfigRepository systemConfigRepository;

    /** 敏感词数据访问层，用于管理敏感词库 */
    private final SensitiveWordRepository sensitiveWordRepository;

    /**
     * 构造器注入依赖
     *
     * @param userService             用户服务
     * @param postService             帖子服务
     * @param commentService          评论服务
     * @param sectionService          板块服务
     * @param reportService           举报服务
     * @param userRepository          用户数据访问层
     * @param postRepository          帖子数据访问层
     * @param commentRepository       评论数据访问层
     * @param reportRepository        举报数据访问层
     * @param tagRepository           标签数据访问层
     * @param operationLogRepository  操作日志数据访问层
     * @param systemConfigRepository  系统配置数据访问层
     * @param sensitiveWordRepository 敏感词数据访问层
     */
    public AdminService(UserService userService,
                        PostService postService,
                        CommentService commentService,
                        SectionService sectionService,
                        ReportService reportService,
                        UserRepository userRepository,
                        PostRepository postRepository,
                        CommentRepository commentRepository,
                        ReportRepository reportRepository,
                        TagRepository tagRepository,
                        OperationLogRepository operationLogRepository,
                        SystemConfigRepository systemConfigRepository,
                        SensitiveWordRepository sensitiveWordRepository) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.sectionService = sectionService;
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.tagRepository = tagRepository;
        this.operationLogRepository = operationLogRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.sensitiveWordRepository = sensitiveWordRepository;
    }

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户列表
     *
     * @return 用户响应列表
     */
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 搜索用户
     *
     * @param keyword 搜索关键词（匹配用户名）
     * @return 匹配的用户响应列表
     */
    public List<UserResponse> searchUsers(String keyword) {
        return userService.searchUsers(keyword);
    }

    /**
     * 封禁用户
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void banUser(Long userId) {
        userService.banUser(userId);
    }

    /**
     * 解封用户
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void unbanUser(Long userId) {
        userService.unbanUser(userId);
    }

    // ==================== 帖子审核 ====================

    /**
     * 获取待审核帖子列表
     *
     * @return 待审核帖子响应列表
     */
    public List<PostResponse> getPendingPosts() {
        return postService.getPendingPosts();
    }

    /**
     * 审核通过
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void approvePost(Long postId) {
        postService.approvePost(postId);
    }

    /**
     * 审核拒绝
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void rejectPost(Long postId) {
        postService.rejectPost(postId);
    }

    /**
     * 删除帖子（逻辑删除）
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void deletePost(Long postId) {
        postService.deletePost(postId);
    }

    /**
     * 切换帖子置顶状态
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void toggleTop(Long postId) {
        postService.toggleTop(postId);
    }

    /**
     * 切换帖子加精状态
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void toggleEssence(Long postId) {
        postService.toggleEssence(postId);
    }

    // ==================== 内容管理 ====================

    /**
     * 获取全部评论列表（管理员视角）
     * <p>
     * 返回所有状态的评论，包括已删除的，供管理员审查。
     * </p>
     *
     * @return 全部评论实体列表
     */
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    /**
     * 删除评论（逻辑删除）
     *
     * @param commentId 评论 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        commentService.deleteComment(commentId);
    }

    /**
     * 获取待处理举报列表
     *
     * @return 待处理举报响应列表
     */
    public List<ReportResponse> getPendingReports() {
        return reportService.getPendingReports();
    }

    /**
     * 处理举报（成立）
     *
     * @param reportId    举报 ID
     * @param handleRemark 处理备注
     */
    @Transactional
    public void resolveReport(Long reportId, String handleRemark) {
        reportService.resolveReport(reportId, handleRemark);
    }

    /**
     * 驳回举报（不成立）
     *
     * @param reportId    举报 ID
     * @param handleRemark 处理备注
     */
    @Transactional
    public void rejectReport(Long reportId, String handleRemark) {
        reportService.rejectReport(reportId, handleRemark);
    }

    // ==================== 板块管理 ====================

    /**
     * 创建板块
     *
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @return 创建后的板块响应 DTO
     */
    @Transactional
    public SectionResponse createSection(String name, String description, String icon) {
        return sectionService.createSection(name, description, icon);
    }

    /**
     * 更新板块
     *
     * @param id          板块 ID
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @return 更新后的板块响应 DTO
     */
    @Transactional
    public SectionResponse updateSection(Long id, String name, String description, String icon) {
        return sectionService.updateSection(id, name, description, icon);
    }

    /**
     * 删除板块
     *
     * @param id 板块 ID
     */
    @Transactional
    public void deleteSection(Long id) {
        sectionService.deleteSection(id);
    }

    /**
     * 设置版主
     *
     * @param id          板块 ID
     * @param moderatorId 版主（管理员）ID
     */
    @Transactional
    public void setModerator(Long id, Long moderatorId) {
        sectionService.setModerator(id, moderatorId);
    }

    // ==================== 数据统计 ====================

    /**
     * 获取站点统计数据
     * <p>
     * 汇总用户、帖子、评论总数，待审核帖子与待处理举报数，
     * 今日新增用户与帖子数，以及热门帖子与热门标签列表。
     * 主要用于管理后台首页数据看板。
     * </p>
     *
     * @return 统计数据响应 DTO
     */
    public StatsResponse getStats() {
        StatsResponse stats = new StatsResponse();

        // 总数统计
        stats.setTotalUsers(userRepository.count());
        stats.setTotalPosts(postRepository.count());
        stats.setTotalComments(commentRepository.count());

        // 待审核帖子数
        stats.setPendingPosts((long) postRepository
                .findByStatusOrderByCreatedAtDesc(PostStatus.PENDING).size());

        // 待处理举报数
        stats.setPendingReports((long) reportRepository
                .findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING).size());

        // 今日新增统计（以当天 0 点为分界）
        LocalDateTime startOfDay = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        stats.setTodayNewUsers(userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(startOfDay))
                .count());
        stats.setTodayNewPosts(postRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startOfDay))
                .count());

        // 热门帖子与热门标签
        stats.setHotPosts(postService.getHotPosts(null));
        stats.setHotTags(tagRepository.findTop10ByOrderByUsageCountDesc().stream()
                .map(this::toTagResponse)
                .collect(Collectors.toList()));

        return stats;
    }

    // ==================== 操作日志 ====================

    /**
     * 记录操作日志
     *
     * @param logEntry 操作日志实体（包含操作人、动作、目标等信息）
     */
    @Transactional
    public void log(OperationLog logEntry) {
        if (logEntry == null) {
            throw new IllegalArgumentException("操作日志不能为空");
        }
        operationLogRepository.save(logEntry);
    }

    /**
     * 获取全部操作日志（按创建时间倒序）
     *
     * @return 操作日志列表
     */
    public List<OperationLog> getOperationLogs() {
        return operationLogRepository.findAllByOrderByCreatedAtDesc();
    }

    // ==================== 系统配置 ====================

    /**
     * 按配置键获取配置值
     *
     * @param key 配置键
     * @return 配置值，不存在时返回 null
     */
    public String getConfig(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    /**
     * 设置配置项（不存在则创建）
     *
     * @param key         配置键
     * @param value       配置值
     * @param description 配置说明，为 null 时不更新说明
     */
    @Transactional
    public void setConfig(String key, String value, String description) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseGet(() -> new SystemConfig(key, value));
        config.setConfigValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        systemConfigRepository.save(config);
    }

    /**
     * 获取全部系统配置
     *
     * @return 系统配置列表
     */
    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }

    // ==================== 敏感词管理 ====================

    /**
     * 获取全部敏感词（按创建时间倒序）
     *
     * @return 敏感词列表
     */
    public List<SensitiveWord> getSensitiveWords() {
        return sensitiveWordRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 新增敏感词
     *
     * @param word     敏感词内容
     * @param category 分类（如政治、色情、广告等），可为 null
     * @return 创建后的敏感词实体
     */
    @Transactional
    public SensitiveWord addSensitiveWord(String word, String category) {
        // 校验敏感词不为空
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        // 校验敏感词是否已存在
        if (sensitiveWordRepository.findByWord(word.trim()).isPresent()) {
            throw new RuntimeException("敏感词已存在");
        }
        SensitiveWord sensitiveWord = new SensitiveWord(word.trim());
        sensitiveWord.setCategory(category);
        return sensitiveWordRepository.save(sensitiveWord);
    }

    /**
     * 删除敏感词
     *
     * @param id 敏感词 ID
     */
    @Transactional
    public void deleteSensitiveWord(Long id) {
        if (!sensitiveWordRepository.existsById(id)) {
            throw new RuntimeException("敏感词不存在");
        }
        sensitiveWordRepository.deleteById(id);
    }

    /**
     * 检测内容是否包含敏感词
     *
     * @param content 待检测内容
     * @return true 包含敏感词，false 不包含
     */
    public boolean containsSensitiveWord(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        List<SensitiveWord> words = sensitiveWordRepository.findAll();
        for (SensitiveWord sw : words) {
            if (content.contains(sw.getWord())) {
                return true;
            }
        }
        return false;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 标签实体转 DTO
     *
     * @param tag 标签实体
     * @return 标签响应 DTO，实体为 null 时返回 null
     */
    private TagResponse toTagResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setUsageCount(tag.getUsageCount());
        response.setCreatedAt(tag.getCreatedAt());
        return response;
    }
}

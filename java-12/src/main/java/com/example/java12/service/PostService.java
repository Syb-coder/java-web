package com.example.java12.service;  // 服务层包

import com.example.java12.dto.PostRequest;  // 帖子请求 DTO
import com.example.java12.dto.PostResponse;  // 帖子响应 DTO
import com.example.java12.dto.StatsResponse;  // 统计响应 DTO
import com.example.java12.model.*;  // 全部实体
import com.example.java12.repository.*;  // 全部数据访问层
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.PageRequest;  // 分页请求
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.data.domain.Sort;  // 排序参数
import org.springframework.stereotype.Service;  // Service 注解
import org.springframework.transaction.annotation.Transactional;  // 事务注解

import java.time.LocalDateTime;  // 时间类型
import java.util.List;  // 列表
import java.util.stream.Collectors;  // 流式收集

/**
 * 帖子服务
 * <p>
 * 核心业务服务，负责帖子的发布、查询、编辑、删除、点赞、收藏、搜索等操作。
 * </p>
 */
@Service
public class PostService {

    /** 帖子数据访问层 */
    private final PostRepository postRepository;
    /** 用户数据访问层 */
    private final UserRepository userRepository;
    /** 板块数据访问层 */
    private final PlateRepository plateRepository;
    /** 点赞数据访问层 */
    private final PostLikeRepository postLikeRepository;
    /** 收藏数据访问层 */
    private final CollectRepository collectRepository;
    /** 版主关联数据访问层（用于版主删帖权限校验） */
    private final ModeratorRepository moderatorRepository;
    /** 评论数据访问层（用于统计总评论数） */
    private final CommentRepository commentRepository;
    /** 操作日志服务 */
    private final OperateLogService operateLogService;

    /**
     * 构造器注入
     */
    public PostService(PostRepository postRepository, UserRepository userRepository,
                       PlateRepository plateRepository, PostLikeRepository postLikeRepository,
                       CollectRepository collectRepository, ModeratorRepository moderatorRepository,
                       CommentRepository commentRepository, OperateLogService operateLogService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.plateRepository = plateRepository;
        this.postLikeRepository = postLikeRepository;
        this.collectRepository = collectRepository;
        this.moderatorRepository = moderatorRepository;
        this.commentRepository = commentRepository;
        this.operateLogService = operateLogService;
    }

    /**
     * 查询最新帖子（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页条数
     * @return 帖子分页列表
     */
    public Page<PostResponse> findLatest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> posts = postRepository.findByIsDeletedFalseOrderByCreateTimeDesc(pageable);
        return posts.map(this::toResponse);
    }

    /**
     * 查询热门帖子（按点赞数+评论数加权排序）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 帖子分页列表
     */
    public Page<PostResponse> findHot(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByIsDeletedFalseOrderByLikeCountDescCommentCountDescCreateTimeDesc(pageable);
        return posts.map(this::toResponse);
    }

    /**
     * 查询指定板块的帖子
     *
     * @param plateId 板块 ID
     * @param page    页码
     * @param size    每页条数
     * @return 帖子分页列表
     */
    public Page<PostResponse> findByPlate(Long plateId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> posts = postRepository.findByPlateIdAndIsDeletedFalseOrderByCreateTimeDesc(plateId, pageable);
        return posts.map(this::toResponse);
    }

    /**
     * 查询帖子详情
     * <p>
     * 填充作者昵称、头像、板块名称。如果用户已登录，填充是否已点赞/已收藏。
     * 每次访问增加浏览量。
     * </p>
     *
     * @param id          帖子 ID
     * @param currentUser 当前登录用户（可为 null）
     * @return 帖子响应
     */
    @Transactional
    public PostResponse findById(Long id, User currentUser) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        // 构造响应
        PostResponse resp = toResponse(post);
        // 填充当前用户的点赞/收藏状态
        if (currentUser != null) {
            resp.setLiked(postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), id));
            resp.setCollected(collectRepository.existsByUserIdAndPostId(currentUser.getId(), id));
        } else {
            resp.setLiked(false);
            resp.setCollected(false);
        }
        return resp;
    }

    /**
     * 发布帖子
     *
     * @param req  帖子请求
     * @param user 作者
     * @return 帖子响应
     */
    @Transactional
    public PostResponse create(PostRequest req, User user) {
        // 校验板块存在
        Plate plate = plateRepository.findById(req.getPlateId())
                .orElseThrow(() -> new RuntimeException("板块不存在"));
        // 创建帖子
        Post post = new Post(user.getId(), req.getPlateId(), req.getTitle(), req.getContent());
        postRepository.save(post);
        // 更新用户发帖数和板块帖子数
        user.setPostCount(user.getPostCount() + 1);
        userRepository.save(user);
        plate.setPostCount(plate.getPostCount() + 1);
        plateRepository.save(plate);
        // 构造响应
        PostResponse resp = toResponse(post);
        resp.setLiked(false);
        resp.setCollected(false);
        return resp;
    }

    /**
     * 编辑帖子（仅作者可编辑）
     *
     * @param id   帖子 ID
     * @param req  帖子请求
     * @param user 操作者
     * @return 更新后的帖子响应
     */
    @Transactional
    public PostResponse update(Long id, PostRequest req, User user) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        // 权限校验：仅作者可编辑
        if (!post.getUserId().equals(user.getId())) {
            throw new RuntimeException("无权编辑他人帖子");
        }
        // 板块变更时更新计数
        if (!post.getPlateId().equals(req.getPlateId())) {
            plateRepository.findById(post.getPlateId()).ifPresent(p -> {
                p.setPostCount(Math.max(0, p.getPostCount() - 1));
                plateRepository.save(p);
            });
            Plate newPlate = plateRepository.findById(req.getPlateId())
                    .orElseThrow(() -> new RuntimeException("板块不存在"));
            newPlate.setPostCount(newPlate.getPostCount() + 1);
            plateRepository.save(newPlate);
        }
        post.setPlateId(req.getPlateId());
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        postRepository.save(post);
        return toResponse(post);
    }

    /**
     * 删除帖子（软删除）
     * <p>
     * 作者可删除自己的帖子；版主可删除所管板块的帖子；管理员可删除任意帖子。
     * </p>
     *
     * @param id   帖子 ID
     * @param user 操作者
     * @param ip   操作 IP
     */
    @Transactional
    public void delete(Long id, User user, String ip) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        // 权限校验：作者、管理员或该板块版主可删除
        boolean isAuthor = post.getUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isModerator = moderatorRepository.existsByUserIdAndPlateId(user.getId(), post.getPlateId());
        if (!isAuthor && !isAdmin && !isModerator) {
            throw new RuntimeException("无权删除此帖子");
        }
        // 软删除
        post.setIsDeleted(true);
        postRepository.save(post);
        // 更新计数
        userRepository.findById(post.getUserId()).ifPresent(u -> {
            u.setPostCount(Math.max(0, u.getPostCount() - 1));
            userRepository.save(u);
        });
        plateRepository.findById(post.getPlateId()).ifPresent(p -> {
            p.setPostCount(Math.max(0, p.getPostCount() - 1));
            plateRepository.save(p);
        });
        // 记录操作日志（非作者删除时记录）
        if (!isAuthor) {
            operateLogService.log(user.getId(), user.getNickname(), "删除帖子",
                    "帖子ID:" + id, ip);
        }
    }

    /**
     * 点赞/取消点赞（幂等切换）
     *
     * @param postId 帖子 ID
     * @param user   操作用户
     * @return true 表示已点赞，false 表示已取消
     */
    @Transactional
    public boolean toggleLike(Long postId, User user) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        if (postLikeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            // 已点赞 → 取消
            postLikeRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            return false;
        } else {
            // 未点赞 → 点赞
            postLikeRepository.save(new PostLike(user.getId(), postId));
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true;
        }
    }

    /**
     * 收藏/取消收藏（幂等切换）
     *
     * @param postId 帖子 ID
     * @param user   操作用户
     * @return true 表示已收藏，false 表示已取消
     */
    @Transactional
    public boolean toggleCollect(Long postId, User user) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        if (collectRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            collectRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.setCollectCount(Math.max(0, post.getCollectCount() - 1));
            postRepository.save(post);
            return false;
        } else {
            collectRepository.save(new Collect(user.getId(), postId));
            post.setCollectCount(post.getCollectCount() + 1);
            postRepository.save(post);
            return true;
        }
    }

    /**
     * 搜索帖子
     * <p>
     * 支持关键词模糊搜索、板块筛选、时间范围筛选、热度/时间排序。
     * </p>
     *
     * @param keyword   关键词（可为 null）
     * @param plateId   板块 ID（可为 null）
     * @param timeRange 时间范围：today/week/month/all
     * @param sortBy    排序方式：hot/time
     * @param page      页码
     * @param size      每页条数
     * @return 帖子分页列表
     */
    public Page<PostResponse> search(String keyword, Long plateId, String timeRange, String sortBy, int page, int size) {
        // 计算时间范围起始时间
        LocalDateTime startTime = parseStartTime(timeRange);
        // 构建排序
        Sort sort = "hot".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "commentCount"))
                : Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        // 执行搜索
        Page<Post> posts = postRepository.searchPosts(keyword, plateId, startTime, pageable);
        return posts.map(this::toResponse);
    }

    /**
     * 查询置顶帖子
     *
     * @return 置顶帖子列表
     */
    public List<PostResponse> findTopPosts() {
        return postRepository.findByIsTopTrueAndIsDeletedFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 设置/取消置顶（版主/管理员操作）
     *
     * @param postId 帖子 ID
     * @param admin  操作管理员/版主
     * @param ip     操作 IP
     */
    @Transactional
    public void toggleTop(Long postId, User admin, String ip) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        // 权限校验：管理员可操作任意帖子，版主仅能操作所管板块的帖子
        if (admin.getRole() != Role.ADMIN) {
            if (!moderatorRepository.existsByUserIdAndPlateId(admin.getId(), post.getPlateId())) {
                throw new RuntimeException("无权操作此板块的帖子");
            }
        }
        post.setIsTop(!post.getIsTop());
        postRepository.save(post);
        operateLogService.log(admin.getId(), admin.getNickname(),
                post.getIsTop() ? "置顶帖子" : "取消置顶", "帖子ID:" + postId, ip);
    }

    /**
     * 查询用户的帖子（我的发帖）
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页条数
     * @return 帖子分页列表
     */
    public Page<PostResponse> findByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> posts = postRepository.findByUserIdAndIsDeletedFalseOrderByCreateTimeDesc(userId, pageable);
        return posts.map(this::toResponse);
    }

    /**
     * 查询用户收藏的帖子（我的收藏）
     *
     * @param userId 用户 ID
     * @return 帖子响应列表
     */
    public List<PostResponse> findCollectsByUserId(Long userId) {
        List<Collect> collects = collectRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return collects.stream()
                .map(collect -> postRepository.findByIdAndIsDeletedFalse(collect.getPostId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> toResponse(opt.get()))
                .collect(Collectors.toList());
    }

    /**
     * 统计数据（首页/后台仪表盘）
     * <p>
     * 聚合用户数、帖子数、评论数、板块数和今日新帖数。
     * </p>
     *
     * @return 统计响应
     */
    public StatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();
        long totalPlates = plateRepository.count();
        // 今日新帖数：筛选创建时间为今天的帖子
        long todayPosts = postRepository.findAll().stream()
                .filter(p -> p.getCreateTime() != null && p.getCreateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count();
        return new StatsResponse(totalUsers, totalPosts, totalComments, totalPlates, todayPosts);
    }

    // ===== 私有辅助方法 =====

    /**
     * 将帖子实体转换为响应 DTO（填充作者和板块信息）
     *
     * @param post 帖子实体
     * @return 帖子响应
     */
    private PostResponse toResponse(Post post) {
        PostResponse resp = new PostResponse(post);
        // 填充作者信息
        userRepository.findById(post.getUserId()).ifPresent(user -> {
            resp.setAuthorName(user.getNickname());
            resp.setAuthorAvatar(user.getAvatar());
        });
        // 填充板块名称
        plateRepository.findById(post.getPlateId()).ifPresent(plate -> {
            resp.setPlateName(plate.getName());
        });
        return resp;
    }

    /**
     * 根据时间范围参数解析起始时间
     *
     * @param timeRange 时间范围：today/week/month/all
     * @return 起始时间（all 返回 null）
     */
    private LocalDateTime parseStartTime(String timeRange) {
        if (timeRange == null || "all".equalsIgnoreCase(timeRange)) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (timeRange.toLowerCase()) {
            case "today" -> now.toLocalDate().atStartOfDay();  // 今天0点
            case "week" -> now.minusDays(7);  // 最近7天
            case "month" -> now.minusDays(30);  // 最近30天
            default -> null;
        };
    }
}

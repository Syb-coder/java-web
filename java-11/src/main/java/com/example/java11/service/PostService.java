package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.PostRequest;
import com.example.java11.dto.PostResponse;
import com.example.java11.model.Post;
import com.example.java11.model.PostStatus;
import com.example.java11.model.PostTag;
import com.example.java11.model.Section;
import com.example.java11.model.SensitiveWord;
import com.example.java11.model.Tag;
import com.example.java11.model.TargetType;
import com.example.java11.model.User;
import com.example.java11.repository.FavoriteRepository;
import com.example.java11.repository.PostLikeRepository;
import com.example.java11.repository.PostRepository;
import com.example.java11.repository.PostTagRepository;
import com.example.java11.repository.SectionRepository;
import com.example.java11.repository.SensitiveWordRepository;
import com.example.java11.repository.SystemConfigRepository;
import com.example.java11.repository.TagRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 帖子服务
 * <p>
 * 负责帖子的发表、查询、搜索、审核与管理。
 * 支持敏感词检测、自动审核配置、标签关联及帖子置顶/加精等运营功能。
 * </p>
 */
@Service
public class PostService {

    /** 系统配置键：帖子自动审核通过开关 */
    private static final String CONFIG_AUTO_APPROVE = "post.auto_approve";

    /** 帖子数据访问层 */
    private final PostRepository postRepository;

    /** 板块数据访问层 */
    private final SectionRepository sectionRepository;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 标签数据访问层 */
    private final TagRepository tagRepository;

    /** 帖子-标签关联数据访问层 */
    private final PostTagRepository postTagRepository;

    /** 敏感词数据访问层，用于内容审核 */
    private final SensitiveWordRepository sensitiveWordRepository;

    /** 点赞数据访问层，用于查询当前用户是否已点赞 */
    private final PostLikeRepository postLikeRepository;

    /** 收藏数据访问层，用于查询当前用户是否已收藏 */
    private final FavoriteRepository favoriteRepository;

    /** 系统配置数据访问层，用于读取自动审核配置 */
    private final SystemConfigRepository systemConfigRepository;

    /**
     * 构造器注入依赖
     *
     * @param postRepository        帖子数据访问层
     * @param sectionRepository     板块数据访问层
     * @param userRepository        用户数据访问层
     * @param tagRepository         标签数据访问层
     * @param postTagRepository     帖子-标签关联数据访问层
     * @param sensitiveWordRepository 敏感词数据访问层
     * @param postLikeRepository    点赞数据访问层
     * @param favoriteRepository    收藏数据访问层
     * @param systemConfigRepository 系统配置数据访问层
     */
    public PostService(PostRepository postRepository,
                       SectionRepository sectionRepository,
                       UserRepository userRepository,
                       TagRepository tagRepository,
                       PostTagRepository postTagRepository,
                       SensitiveWordRepository sensitiveWordRepository,
                       PostLikeRepository postLikeRepository,
                       FavoriteRepository favoriteRepository,
                       SystemConfigRepository systemConfigRepository) {
        this.postRepository = postRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.sensitiveWordRepository = sensitiveWordRepository;
        this.postLikeRepository = postLikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * 发帖
     * <p>
     * 创建帖子实体，根据系统配置决定初始状态（自动审核通过或待审核），
     * 保存标签关联，更新板块帖子数与用户发帖数。
     * 若内容包含敏感词，强制设为待审核状态。
     * </p>
     *
     * @param userId 发帖用户 ID
     * @param req    发帖请求 DTO
     * @return 帖子响应 DTO
     */
    @Transactional
    public PostResponse createPost(Long userId, PostRequest req) {
        // 校验用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        // 校验板块是否存在
        Optional<Section> sectionOptional = sectionRepository.findById(req.getSectionId());
        if (sectionOptional.isEmpty()) {
            throw new RuntimeException("板块不存在");
        }

        // 创建帖子实体
        Post post = new Post(req.getTitle(), req.getContent(), userId, req.getSectionId());

        // 检测敏感词：若包含敏感词则强制待审核
        boolean hasSensitiveWord = containsSensitiveWord(req.getTitle() + " " + req.getContent());

        // 根据系统配置与敏感词检测结果决定帖子状态
        if (!hasSensitiveWord && isAutoApproveEnabled()) {
            post.setStatus(PostStatus.APPROVED);
        }
        Post saved = postRepository.save(post);

        // 保存标签关联
        savePostTags(saved.getId(), req.getTags());

        // 更新板块帖子数
        Section section = sectionOptional.get();
        section.setPostCount(section.getPostCount() + 1);
        sectionRepository.save(section);

        // 更新用户发帖数
        userRepository.findById(userId).ifPresent(user -> {
            user.setPostCount(user.getPostCount() + 1);
            userRepository.save(user);
        });

        return toResponse(saved, userId);
    }

    /**
     * 获取帖子详情（增加浏览量）
     *
     * @param id            帖子 ID
     * @param currentUserId 当前用户 ID，可为 null
     * @return 帖子响应 DTO
     */
    @Transactional
    public PostResponse getPost(Long id, Long currentUserId) {
        Optional<Post> optional = postRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return toResponse(post, currentUserId);
    }

    /**
     * 按板块获取帖子列表
     * <p>
     * 仅返回已审核通过的帖子，置顶优先，再按创建时间倒序。
     * </p>
     *
     * @param sectionId     板块 ID
     * @param currentUserId 当前用户 ID，可为 null
     * @return 帖子响应列表
     */
    public List<PostResponse> getPostsBySection(Long sectionId, Long currentUserId) {
        List<Post> posts = postRepository
                .findBySectionIdAndStatusOrderByIsTopDescCreatedAtDesc(sectionId, PostStatus.APPROVED);
        return posts.stream()
                .map(post -> toResponse(post, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 获取热门帖子
     *
     * @param currentUserId 当前用户 ID，可为 null
     * @return 热门帖子响应列表（前 10）
     */
    public List<PostResponse> getHotPosts(Long currentUserId) {
        List<Post> posts = postRepository.findTop10ByStatusOrderByViewCountDesc(PostStatus.APPROVED);
        return posts.stream()
                .map(post -> toResponse(post, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 搜索帖子
     * <p>
     * 按标题或正文关键词搜索，仅返回已审核通过的帖子。
     * </p>
     *
     * @param keyword       搜索关键词
     * @param currentUserId 当前用户 ID，可为 null
     * @return 匹配的帖子响应列表
     */
    public List<PostResponse> searchPosts(String keyword, Long currentUserId) {
        List<Post> posts = postRepository
                .findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword, keyword);
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.APPROVED)
                .map(post -> toResponse(post, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户发帖历史
     *
     * @param userId        用户 ID
     * @param currentUserId 当前用户 ID，可为 null
     * @return 用户发帖响应列表
     */
    public List<PostResponse> getPostsByUser(Long userId, Long currentUserId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(post -> toResponse(post, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 获取待审核帖子列表
     *
     * @return 待审核帖子响应列表
     */
    public List<PostResponse> getPendingPosts() {
        List<Post> posts = postRepository.findByStatusOrderByCreatedAtDesc(PostStatus.PENDING);
        return posts.stream()
                .map(post -> toResponse(post, null))
                .collect(Collectors.toList());
    }

    /**
     * 审核通过
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void approvePost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);
    }

    /**
     * 审核拒绝
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void rejectPost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);
    }

    /**
     * 删除帖子（逻辑删除）
     * <p>
     * 将帖子状态设为 DELETED，同时递减板块帖子数与用户发帖数。
     * </p>
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void deletePost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        // 递减板块帖子数
        sectionRepository.findById(post.getSectionId()).ifPresent(section -> {
            section.setPostCount(Math.max(0, section.getPostCount() - 1));
            sectionRepository.save(section);
        });

        // 递减用户发帖数
        userRepository.findById(post.getUserId()).ifPresent(user -> {
            user.setPostCount(Math.max(0, user.getPostCount() - 1));
            userRepository.save(user);
        });
    }

    /**
     * 切换帖子置顶状态
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void toggleTop(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        post.setIsTop(!post.getIsTop());
        postRepository.save(post);
    }

    /**
     * 切换帖子加精状态
     *
     * @param postId 帖子 ID
     */
    @Transactional
    public void toggleEssence(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }
        Post post = optional.get();
        post.setIsEssence(!post.getIsEssence());
        postRepository.save(post);
    }

    /**
     * 管理员获取全部帖子
     *
     * @return 全部帖子响应列表
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> toResponse(post, null))
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查系统是否启用了帖子自动审核通过
     *
     * @return true 表示自动审核通过已启用
     */
    private boolean isAutoApproveEnabled() {
        return systemConfigRepository.findByConfigKey(CONFIG_AUTO_APPROVE)
                .map(config -> "true".equalsIgnoreCase(config.getConfigValue()))
                .orElse(false);
    }

    /**
     * 检测内容是否包含敏感词
     *
     * @param content 待检测内容
     * @return true 包含敏感词，false 不包含
     */
    private boolean containsSensitiveWord(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.findAll();
        for (SensitiveWord word : sensitiveWords) {
            if (content.contains(word.getWord())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存帖子标签关联
     * <p>
     * 标签不存在时自动创建，已存在则递增使用次数。
     * </p>
     *
     * @param postId   帖子 ID
     * @param tagNames 标签名列表
     */
    private void savePostTags(Long postId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }
        for (String tagName : tagNames) {
            String trimmedName = tagName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }
            Tag tag = tagRepository.findByName(trimmedName)
                    .orElseGet(() -> tagRepository.save(new Tag(trimmedName)));
            tag.setUsageCount(tag.getUsageCount() + 1);
            tagRepository.save(tag);
            PostTag postTag = new PostTag(postId, tag.getId());
            postTagRepository.save(postTag);
        }
    }

    /**
     * 获取帖子的标签名列表
     *
     * @param postId 帖子 ID
     * @return 标签名列表
     */
    private List<String> getTagNamesByPost(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        List<String> tagNames = new ArrayList<>();
        for (PostTag postTag : postTags) {
            tagRepository.findById(postTag.getTagId())
                    .ifPresent(tag -> tagNames.add(tag.getName()));
        }
        return tagNames;
    }

    /**
     * 实体转 DTO
     *
     * @param post          帖子实体
     * @param currentUserId 当前用户 ID（用于查询点赞/收藏状态），可为 null
     * @return 帖子响应 DTO，实体为 null 时返回 null
     */
    private PostResponse toResponse(Post post, Long currentUserId) {
        if (post == null) {
            return null;
        }
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setUserId(post.getUserId());
        response.setSectionId(post.getSectionId());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setFavoriteCount(post.getFavoriteCount());
        response.setStatus(post.getStatus() != null ? post.getStatus().name() : null);
        response.setIsTop(post.getIsTop());
        response.setIsEssence(post.getIsEssence());
        response.setTags(getTagNamesByPost(post.getId()));
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdateTime(post.getUpdateTime());

        // 查询发帖人用户名和头像
        if (post.getUserId() != null) {
            Optional<User> user = userRepository.findById(post.getUserId());
            user.ifPresent(u -> {
                response.setUsername(u.getNickname() != null ? u.getNickname() : u.getUsername());
                response.setUserAvatar(u.getAvatar());
            });
        }

        // 查询板块名称
        if (post.getSectionId() != null) {
            sectionRepository.findById(post.getSectionId())
                    .ifPresent(section -> response.setSectionName(section.getName()));
        }

        // 查询当前用户是否已点赞/收藏
        if (currentUserId != null) {
            boolean liked = postLikeRepository
                    .findByUserIdAndTargetTypeAndTargetId(currentUserId, TargetType.POST, post.getId())
                    .isPresent();
            response.setIsLiked(liked);
            boolean favorited = favoriteRepository.findByUserIdAndPostId(currentUserId, post.getId()).isPresent();
            response.setIsFavorited(favorited);
        } else {
            response.setIsLiked(false);
            response.setIsFavorited(false);
        }
        return response;
    }
}

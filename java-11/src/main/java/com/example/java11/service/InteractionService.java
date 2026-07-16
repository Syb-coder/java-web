package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.model.Comment;
import com.example.java11.model.Favorite;
import com.example.java11.model.Follow;
import com.example.java11.model.Post;
import com.example.java11.model.PostLike;
import com.example.java11.model.TargetType;
import com.example.java11.repository.CommentRepository;
import com.example.java11.repository.FavoriteRepository;
import com.example.java11.repository.FollowRepository;
import com.example.java11.repository.PostLikeRepository;
import com.example.java11.repository.PostRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 互动服务
 * <p>
 * 统一管理用户的点赞、收藏与关注三类互动行为。
 * 每类行为均支持切换状态（toggle），自动维护关联计数与通知推送。
 * </p>
 */
@Service
public class InteractionService {

    /** 点赞数据访问层 */
    private final PostLikeRepository postLikeRepository;

    /** 收藏数据访问层 */
    private final FavoriteRepository favoriteRepository;

    /** 关注数据访问层 */
    private final FollowRepository followRepository;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 帖子数据访问层 */
    private final PostRepository postRepository;

    /** 评论数据访问层，用于评论点赞时更新计数 */
    private final CommentRepository commentRepository;

    /** 通知服务，用于发送互动通知 */
    private final NotificationService notificationService;

    /**
     * 构造器注入依赖
     *
     * @param postLikeRepository   点赞数据访问层
     * @param favoriteRepository   收藏数据访问层
     * @param followRepository     关注数据访问层
     * @param userRepository       用户数据访问层
     * @param postRepository       帖子数据访问层
     * @param commentRepository    评论数据访问层
     * @param notificationService  通知服务
     */
    public InteractionService(PostLikeRepository postLikeRepository,
                              FavoriteRepository favoriteRepository,
                              FollowRepository followRepository,
                              UserRepository userRepository,
                              PostRepository postRepository,
                              CommentRepository commentRepository,
                              NotificationService notificationService) {
        this.postLikeRepository = postLikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    // ==================== 点赞相关 ====================

    /**
     * 切换点赞状态
     * <p>
     * 已点赞则取消（删除记录、减少计数），未点赞则添加（创建记录、增加计数、发送通知）。
     * </p>
     *
     * @param userId     点赞用户 ID
     * @param targetType 目标类型（POST 或 COMMENT）
     * @param targetId   目标 ID
     * @return true 表示已点赞（添加），false 表示已取消点赞
     */
    @Transactional
    public boolean toggleLike(Long userId, String targetType, Long targetId) {
        TargetType type = parseTargetType(targetType);
        Optional<PostLike> existing = postLikeRepository.findByUserIdAndTargetTypeAndTargetId(userId, type, targetId);

        if (existing.isPresent()) {
            // 已点赞，取消点赞
            postLikeRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, type, targetId);
            decrementLikeCount(type, targetId);
            return false;
        }

        // 未点赞，添加点赞
        PostLike like = new PostLike(userId, type, targetId);
        postLikeRepository.save(like);
        incrementLikeCount(type, targetId);

        // 发送点赞通知给目标所有者
        Long targetOwnerId = getTargetOwnerId(type, targetId);
        if (targetOwnerId != null && !targetOwnerId.equals(userId)) {
            notificationService.sendNotification(targetOwnerId, "LIKE", "有人赞了你的内容", targetId, userId);
        }
        return true;
    }

    /**
     * 检查是否已点赞
     *
     * @param userId     用户 ID
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @return true 已点赞，false 未点赞
     */
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        TargetType type = parseTargetType(targetType);
        return postLikeRepository.findByUserIdAndTargetTypeAndTargetId(userId, type, targetId).isPresent();
    }

    /**
     * 获取目标的点赞数
     *
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @return 点赞数量
     */
    public long getLikeCount(String targetType, Long targetId) {
        TargetType type = parseTargetType(targetType);
        return postLikeRepository.countByTargetTypeAndTargetId(type, targetId);
    }

    // ==================== 收藏相关 ====================

    /**
     * 切换收藏状态
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return true 表示已收藏（添加），false 表示已取消收藏
     */
    @Transactional
    public boolean toggleFavorite(Long userId, Long postId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            // 已收藏，取消收藏
            favoriteRepository.deleteByUserIdAndPostId(userId, postId);
            decrementPostFavoriteCount(postId);
            return false;
        }

        // 未收藏，添加收藏
        Favorite favorite = new Favorite(userId, postId);
        favoriteRepository.save(favorite);
        incrementPostFavoriteCount(postId);
        return true;
    }

    /**
     * 检查是否已收藏
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return true 已收藏，false 未收藏
     */
    public boolean isFavorited(Long userId, Long postId) {
        return favoriteRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户 ID
     * @return 收藏记录列表
     */
    public List<Favorite> getFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 按分组获取用户收藏
     *
     * @param userId    用户 ID
     * @param groupName 分组名
     * @return 收藏记录列表
     */
    public List<Favorite> getFavoritesByGroup(Long userId, String groupName) {
        return favoriteRepository.findByUserIdAndGroupName(userId, groupName);
    }

    /**
     * 获取用户收藏分组列表
     *
     * @param userId 用户 ID
     * @return 分组名列表（去重）
     */
    public List<String> getFavoriteGroups(Long userId) {
        return favoriteRepository.findDistinctGroupNameByUserId(userId);
    }

    // ==================== 关注相关 ====================

    /**
     * 切换关注状态
     * <p>
     * 已关注则取消（删除记录、减少关注/粉丝计数），未关注则添加（创建记录、增加计数）。
     * </p>
     *
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     * @return true 表示已关注（添加），false 表示已取消关注
     */
    @Transactional
    public boolean toggleFollow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("不能关注自己");
        }

        Optional<Follow> existing = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);

        if (existing.isPresent()) {
            // 已关注，取消关注
            followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
            updateUserFollowingCount(followerId, -1);
            updateUserFollowerCount(followeeId, -1);
            return false;
        }

        // 未关注，添加关注
        Follow follow = new Follow(followerId, followeeId);
        followRepository.save(follow);
        updateUserFollowingCount(followerId, 1);
        updateUserFollowerCount(followeeId, 1);
        return true;
    }

    /**
     * 检查是否已关注
     *
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     * @return true 已关注，false 未关注
     */
    public boolean isFollowing(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).isPresent();
    }

    /**
     * 获取关注列表
     *
     * @param userId 用户 ID
     * @return 关注记录列表
     */
    public List<Follow> getFollowingList(Long userId) {
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取粉丝列表
     *
     * @param userId 用户 ID
     * @return 粉丝记录列表
     */
    public List<Follow> getFollowerList(Long userId) {
        return followRepository.findByFolloweeIdOrderByCreatedAtDesc(userId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 解析目标类型字符串为枚举
     *
     * @param targetType 目标类型字符串
     * @return 目标类型枚举
     */
    private TargetType parseTargetType(String targetType) {
        try {
            return TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }
    }

    /**
     * 增加目标的点赞计数
     *
     * @param type     目标类型
     * @param targetId 目标 ID
     */
    private void incrementLikeCount(TargetType type, Long targetId) {
        if (type == TargetType.POST) {
            postRepository.findById(targetId).ifPresent(post -> {
                post.setLikeCount(post.getLikeCount() + 1);
                postRepository.save(post);
            });
        } else if (type == TargetType.COMMENT) {
            commentRepository.findById(targetId).ifPresent(comment -> {
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentRepository.save(comment);
            });
        }
    }

    /**
     * 减少目标的点赞计数
     *
     * @param type     目标类型
     * @param targetId 目标 ID
     */
    private void decrementLikeCount(TargetType type, Long targetId) {
        if (type == TargetType.POST) {
            postRepository.findById(targetId).ifPresent(post -> {
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                postRepository.save(post);
            });
        } else if (type == TargetType.COMMENT) {
            commentRepository.findById(targetId).ifPresent(comment -> {
                comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
                commentRepository.save(comment);
            });
        }
    }

    /**
     * 获取目标所有者 ID（用于发送通知）
     *
     * @param type     目标类型
     * @param targetId 目标 ID
     * @return 所有者用户 ID，找不到时返回 null
     */
    private Long getTargetOwnerId(TargetType type, Long targetId) {
        if (type == TargetType.POST) {
            return postRepository.findById(targetId).map(Post::getUserId).orElse(null);
        } else if (type == TargetType.COMMENT) {
            return commentRepository.findById(targetId).map(Comment::getUserId).orElse(null);
        }
        return null;
    }

    /**
     * 增加帖子收藏数
     *
     * @param postId 帖子 ID
     */
    private void incrementPostFavoriteCount(Long postId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.setFavoriteCount(post.getFavoriteCount() + 1);
            postRepository.save(post);
        });
    }

    /**
     * 减少帖子收藏数
     *
     * @param postId 帖子 ID
     */
    private void decrementPostFavoriteCount(Long postId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
            postRepository.save(post);
        });
    }

    /**
     * 更新用户关注数
     *
     * @param userId 用户 ID
     * @param delta  增量（+1 或 -1）
     */
    private void updateUserFollowingCount(Long userId, int delta) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFollowingCount(user.getFollowingCount() + delta);
            userRepository.save(user);
        });
    }

    /**
     * 更新用户粉丝数
     *
     * @param userId 用户 ID
     * @param delta  增量（+1 或 -1）
     */
    private void updateUserFollowerCount(Long userId, int delta) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFollowerCount(user.getFollowerCount() + delta);
            userRepository.save(user);
        });
    }
}

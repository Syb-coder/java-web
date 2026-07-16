package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.CommentRequest;
import com.example.java11.dto.CommentResponse;
import com.example.java11.model.Comment;
import com.example.java11.model.CommentStatus;
import com.example.java11.model.Post;
import com.example.java11.model.TargetType;
import com.example.java11.model.User;
import com.example.java11.repository.CommentRepository;
import com.example.java11.repository.PostLikeRepository;
import com.example.java11.repository.PostRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 评论服务
 * <p>
 * 负责评论的发表、查询、删除及楼层号管理。
 * 支持楼中楼回复，回复时自动发送通知给父评论作者。
 * </p>
 */
@Service
public class CommentService {

    /** 评论数据访问层 */
    private final CommentRepository commentRepository;

    /** 帖子数据访问层，用于更新帖子评论数 */
    private final PostRepository postRepository;

    /** 用户数据访问层，用于查询评论人信息 */
    private final UserRepository userRepository;

    /** 点赞数据访问层，用于查询当前用户是否已点赞评论 */
    private final PostLikeRepository postLikeRepository;

    /** 通知服务，用于发送回复通知 */
    private final NotificationService notificationService;

    /**
     * 构造器注入依赖
     *
     * @param commentRepository   评论数据访问层
     * @param postRepository      帖子数据访问层
     * @param userRepository      用户数据访问层
     * @param postLikeRepository  点赞数据访问层
     * @param notificationService 通知服务
     */
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          PostLikeRepository postLikeRepository,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.notificationService = notificationService;
    }

    /**
     * 发表评论
     * <p>
     * 计算楼层号（当前最大楼层 + 1），创建评论实体，
     * 更新帖子评论数，若为回复则发送通知给父评论作者。
     * </p>
     *
     * @param userId 评论用户 ID
     * @param req    评论请求 DTO
     * @return 评论响应 DTO
     */
    @Transactional
    public CommentResponse createComment(Long userId, CommentRequest req) {
        // 校验帖子是否存在
        Optional<Post> postOptional = postRepository.findById(req.getPostId());
        if (postOptional.isEmpty()) {
            throw new RuntimeException("帖子不存在");
        }

        // 计算楼层号：当前最大楼层 + 1，无评论时为 1
        Integer maxFloor = commentRepository.findMaxFloorByPostId(req.getPostId());
        int floor = (maxFloor == null) ? 1 : maxFloor + 1;

        // 创建评论
        Comment comment = new Comment(req.getContent(), userId, req.getPostId(), req.getParentId(), floor);
        Comment saved = commentRepository.save(comment);

        // 更新帖子评论数
        Post post = postOptional.get();
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        // 如果是回复（parentId 不为 null），发送回复通知
        if (req.getParentId() != null) {
            Optional<Comment> parentComment = commentRepository.findById(req.getParentId());
            if (parentComment.isPresent()) {
                Long parentUserId = parentComment.get().getUserId();
                // 不给自己发通知
                if (!parentUserId.equals(userId)) {
                    notificationService.sendNotification(
                            parentUserId, "REPLY", "有人回复了你的评论", saved.getId(), userId);
                }
            }
        }

        // 解析评论内容中的 @用户名 提及，给被提及的用户发送通知
        parseAndNotifyMentions(req.getContent(), userId, saved.getId(), req.getPostId());

        return toResponse(saved, userId);
    }

    /**
     * 解析评论内容中的 @用户名 提及，并给被提及用户发送通知
     * <p>
     * 使用正则匹配 @ 后的合法用户名（字母、数字、下划线，3-20 字符），
     * 查询用户是否存在，存在且非自己时发送 MENTION 类型通知。
     * 同一评论中多次提及同一用户只发一次通知。
     * </p>
     *
     * @param content      评论内容
     * @param senderId     发送者用户 ID
     * @param commentId    评论 ID
     * @param postId       帖子 ID
     */
    private void parseAndNotifyMentions(String content, Long senderId, Long commentId, Long postId) {
        if (content == null || content.isEmpty()) {
            return;
        }
        // 查询发送者用户名，用于构建友好的通知内容
        Optional<User> sender = userRepository.findById(senderId);
        String senderName = sender.map(User::getNickname)
                .orElse(sender.map(User::getUsername).orElse("用户"));
        // 正则匹配 @用户名（用户名规则：3-20 位字母/数字/下划线）
        Pattern pattern = Pattern.compile("@(\\w{3,20})");
        Matcher matcher = pattern.matcher(content);
        Set<Long> notifiedUserIds = new HashSet<>();  // 记录已通知的用户，避免重复
        while (matcher.find()) {
            String username = matcher.group(1);
            Optional<User> mentionedUser = userRepository.findByUsername(username);
            if (mentionedUser.isPresent()) {
                Long mentionedUserId = mentionedUser.get().getId();
                // 不给自己发提及通知，不重复通知同一用户
                if (!mentionedUserId.equals(senderId) && !notifiedUserIds.contains(mentionedUserId)) {
                    notifiedUserIds.add(mentionedUserId);
                    notificationService.sendNotification(
                            mentionedUserId, "MENTION",
                            senderName + " 在帖子中提及了你", commentId, senderId);
                }
            }
        }
    }

    /**
     * 获取帖子评论列表
     * <p>
     * 仅返回正常状态的评论，按楼层号升序排列。
     * </p>
     *
     * @param postId        帖子 ID
     * @param currentUserId 当前用户 ID（用于查询点赞状态），可为 null
     * @return 评论响应列表
     */
    public List<CommentResponse> getCommentsByPost(Long postId, Long currentUserId) {
        List<Comment> comments = commentRepository.findByPostIdAndStatusOrderByFloorAsc(postId, CommentStatus.NORMAL);
        return comments.stream()
                .map(comment -> toResponse(comment, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户评论历史
     *
     * @param userId 用户 ID
     * @return 评论响应列表
     */
    public List<CommentResponse> getCommentsByUser(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return comments.stream()
                .map(comment -> toResponse(comment, null))
                .collect(Collectors.toList());
    }

    /**
     * 删除评论（逻辑删除，状态改为 DELETED）
     *
     * @param commentId 评论 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isEmpty()) {
            throw new RuntimeException("评论不存在");
        }
        Comment comment = optional.get();
        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

        // 更新帖子评论数（减 1）
        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        });
    }

    /**
     * 实体转 DTO
     *
     * @param comment       评论实体
     * @param currentUserId 当前用户 ID（用于查询点赞状态），可为 null
     * @return 评论响应 DTO，实体为 null 时返回 null
     */
    private CommentResponse toResponse(Comment comment, Long currentUserId) {
        if (comment == null) {
            return null;
        }
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getUserId());
        response.setPostId(comment.getPostId());
        response.setParentId(comment.getParentId());
        response.setFloor(comment.getFloor());
        response.setLikeCount(comment.getLikeCount());
        response.setStatus(comment.getStatus() != null ? comment.getStatus().name() : null);
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdateTime(comment.getUpdateTime());

        // 查询评论人用户名和头像
        if (comment.getUserId() != null) {
            Optional<User> user = userRepository.findById(comment.getUserId());
            user.ifPresent(u -> {
                response.setUsername(u.getNickname() != null ? u.getNickname() : u.getUsername());
                response.setUserAvatar(u.getAvatar());
            });
        }

        // 查询当前用户是否已点赞该评论
        if (currentUserId != null) {
            boolean liked = postLikeRepository
                    .findByUserIdAndTargetTypeAndTargetId(currentUserId, TargetType.COMMENT, comment.getId())
                    .isPresent();
            response.setIsLiked(liked);
        } else {
            response.setIsLiked(false);
        }
        return response;
    }
}

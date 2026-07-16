package com.example.java12.service;  // 服务层包

import com.example.java12.dto.CommentRequest;  // 评论请求 DTO
import com.example.java12.dto.CommentResponse;  // 评论响应 DTO
import com.example.java12.model.Comment;  // 评论实体
import com.example.java12.model.Post;  // 帖子实体
import com.example.java12.model.Role;  // 角色枚举
import com.example.java12.model.User;  // 用户实体
import com.example.java12.repository.CommentRepository;  // 评论数据访问层
import com.example.java12.repository.ModeratorRepository;  // 版主数据访问层
import com.example.java12.repository.PostRepository;  // 帖子数据访问层
import com.example.java12.repository.UserRepository;  // 用户数据访问层
import org.springframework.stereotype.Service;  // Service 注解
import org.springframework.transaction.annotation.Transactional;  // 事务注解

import java.util.List;  // 列表
import java.util.stream.Collectors;  // 流式收集

/**
 * 评论服务
 * <p>
 * 负责评论的发表、查询、删除与屏蔽。
 * 评论支持嵌套一层回复（通过 parentId 关联）。
 * </p>
 */
@Service
public class CommentService {

    /** 评论数据访问层 */
    private final CommentRepository commentRepository;

    /** 帖子数据访问层（发表评论时更新帖子评论数） */
    private final PostRepository postRepository;

    /** 用户数据访问层（获取评论者信息） */
    private final UserRepository userRepository;

    /** 版主关联数据访问层（用于版主屏蔽评论权限校验） */
    private final ModeratorRepository moderatorRepository;

    /** 操作日志服务 */
    private final OperateLogService operateLogService;

    /**
     * 构造器注入
     */
    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
                          UserRepository userRepository, ModeratorRepository moderatorRepository,
                          OperateLogService operateLogService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.moderatorRepository = moderatorRepository;
        this.operateLogService = operateLogService;
    }

    /**
     * 查询帖子的评论列表（按时间升序）
     * <p>
     * 填充评论者昵称和头像。已删除评论内容显示为"该评论已被删除"。
     * </p>
     *
     * @param postId 帖子 ID
     * @return 评论响应列表
     */
    public List<CommentResponse> findByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreateTimeAsc(postId);
        return comments.stream().map(comment -> {
            CommentResponse resp = new CommentResponse(comment);
            // 填充评论者信息
            userRepository.findById(comment.getUserId()).ifPresent(user -> {
                resp.setAuthorName(user.getNickname());
                resp.setAuthorAvatar(user.getAvatar());
            });
            // 已屏蔽评论隐藏内容
            if (comment.getIsHidden()) {
                resp.setContent("[该评论已被屏蔽]");
            }
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 发表评论
     * <p>
     * 校验帖子存在性，创建评论，更新帖子评论数。
     * </p>
     *
     * @param req   评论请求
     * @param user  评论者
     * @return 评论响应
     */
    @Transactional
    public CommentResponse create(CommentRequest req, User user) {
        // 校验帖子存在且未删除
        Post post = postRepository.findByIdAndIsDeletedFalse(req.getPostId())
                .orElseThrow(() -> new RuntimeException("帖子不存在或已被删除"));
        // 创建评论
        Comment comment = new Comment(req.getPostId(), user.getId(), req.getParentId(), req.getContent());
        commentRepository.save(comment);
        // 更新帖子评论数
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        // 构造响应
        CommentResponse resp = new CommentResponse(comment);
        resp.setAuthorName(user.getNickname());
        resp.setAuthorAvatar(user.getAvatar());
        return resp;
    }

    /**
     * 删除评论（软删除）
     * <p>
     * 仅评论者本人、所管板块版主或管理员可删除。删除后内容显示"该评论已被删除"。
     * </p>
     *
     * @param commentId 评论 ID
     * @param user      操作者
     */
    @Transactional
    public void delete(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        // 权限校验：作者、管理员或所管板块版主可删除
        boolean isAuthor = comment.getUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isModerator = false;
        if (!isAuthor && !isAdmin) {
            // 检查是否是所管板块的版主
            Post post = postRepository.findById(comment.getPostId()).orElse(null);
            if (post != null) {
                isModerator = moderatorRepository.existsByUserIdAndPlateId(user.getId(), post.getPlateId());
            }
        }
        if (!isAuthor && !isAdmin && !isModerator) {
            throw new RuntimeException("无权删除此评论");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        // 更新帖子评论数
        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        });
    }

    /**
     * 屏蔽评论（版主/管理员操作）
     * <p>
     * 管理员可屏蔽任意评论，版主仅能屏蔽所管板块下帖子的评论。
     * </p>
     *
     * @param commentId 评论 ID
     * @param admin     操作管理员/版主
     * @param ip        操作 IP
     */
    @Transactional
    public void hide(Long commentId, User admin, String ip) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        // 权限校验：管理员可操作任意评论，版主仅能操作所管板块的帖子评论
        if (admin.getRole() != Role.ADMIN) {
            Post post = postRepository.findById(comment.getPostId())
                    .orElseThrow(() -> new RuntimeException("关联帖子不存在"));
            if (!moderatorRepository.existsByUserIdAndPlateId(admin.getId(), post.getPlateId())) {
                throw new RuntimeException("无权操作此板块的评论");
            }
        }
        comment.setIsHidden(true);
        commentRepository.save(comment);
        operateLogService.log(admin.getId(), admin.getNickname(), "屏蔽评论",
                "评论ID:" + commentId, ip);
    }

    /**
     * 查询用户的评论列表（我的评论）
     *
     * @param userId 用户 ID
     * @return 评论响应列表
     */
    public List<CommentResponse> findByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdAndIsDeletedFalseOrderByCreateTimeDesc(userId);
        return comments.stream().map(comment -> {
            CommentResponse resp = new CommentResponse(comment);
            resp.setAuthorName(null);
            return resp;
        }).collect(Collectors.toList());
    }
}

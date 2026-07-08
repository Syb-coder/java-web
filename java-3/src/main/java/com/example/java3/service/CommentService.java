package com.example.java3.service;

import com.example.java3.dto.CommentRequest;
import com.example.java3.dto.CommentResponse;
import com.example.java3.model.Comment;
import com.example.java3.model.User;
import com.example.java3.repository.CommentRepository;
import com.example.java3.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    /**
     * 发表评论
     *
     * @param productId 商品 ID
     * @param userId    评论者 ID
     * @param req       评论请求
     * @return 评论响应
     */
    public CommentResponse create(Long productId, Long userId, CommentRequest req) {
        Comment c = new Comment(productId, userId, req.getContent(), req.getParentId());
        commentRepository.save(c);
        User u = userRepository.findById(userId).orElse(null);
        return toResponse(c, u);
    }

    /**
     * 查询商品评论列表
     *
     * @param productId 商品 ID
     * @return 评论响应列表
     */
    public List<CommentResponse> listByProduct(Long productId) {
        return commentRepository.findByProductIdOrderByCreatedAtAsc(productId).stream()
                .map(c -> {
                    User u = userRepository.findById(c.getUserId()).orElse(null);
                    return toResponse(c, u);
                })
                .toList();
    }

    /**
     * 实体转响应
     *
     * @param c 评论实体
     * @param u 评论者
     * @return 响应
     */
    private CommentResponse toResponse(Comment c, User u) {
        String username = u != null ? u.getNickname() : "未知用户";
        String avatar = u != null ? u.getAvatar() : null;
        return new CommentResponse(c, username, avatar);
    }
}

// 声明当前类所在包路径
package com.example.java3.service;

// 导入评论请求 DTO
import com.example.java3.dto.CommentRequest;
// 导入评论响应 DTO
import com.example.java3.dto.CommentResponse;
// 导入评论实体模型
import com.example.java3.model.Comment;
// 导入用户实体模型
import com.example.java3.model.User;
// 导入评论仓储接口（Spring Data JPA）
import com.example.java3.repository.CommentRepository;
// 导入用户仓储接口
import com.example.java3.repository.UserRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 评论服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class CommentService {

    // 评论仓储，处理评论表 CRUD
    private final CommentRepository commentRepository;
    // 用户仓储，用于补充评论者昵称、头像
    private final UserRepository userRepository;

    // 构造方法注入两个仓储 Bean
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
        // 构造评论实体，包含商品 ID、用户 ID、内容、父评论 ID（用于楼中楼回复）
        Comment c = new Comment(productId, userId, req.getContent(), req.getParentId());
        // 持久化到数据库
        commentRepository.save(c);
        // 查询评论者用户信息，用于展示昵称和头像
        User u = userRepository.findById(userId).orElse(null);
        // 转换为响应 DTO
        return toResponse(c, u);
    }

    /**
     * 查询商品评论列表
     *
     * @param productId 商品 ID
     * @return 评论响应列表
     */
    public List<CommentResponse> listByProduct(Long productId) {
        // 按商品 ID 查询评论，按创建时间升序排列（旧评论在前）
        return commentRepository.findByProductIdOrderByCreatedAtAsc(productId).stream()
                // 对每条评论查询其作者信息
                .map(c -> {
                    // 查询评论者
                    User u = userRepository.findById(c.getUserId()).orElse(null);
                    // 转换为响应 DTO
                    return toResponse(c, u);
                })
                // 收集为 List
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
        // 用户存在时取昵称，否则使用占位文本
        String username = u != null ? u.getNickname() : "未知用户";
        // 用户存在时取头像，否则为 null
        String avatar = u != null ? u.getAvatar() : null;
        // 构造响应 DTO
        return new CommentResponse(c, username, avatar);
    }
}

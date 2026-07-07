// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.CommentRequest;
import com.example.java2.dto.CommentResponse;
import com.example.java2.model.Comment;
import com.example.java2.model.User;
import com.example.java2.repository.CommentRepository;
import com.example.java2.repository.UserRepository;

// 导入 Spring 分页与注解
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 评论业务服务
 * <p>
 * 负责评论的发表、查询、删除（含后台审核删除）。
 * </p>
 */
@Service
public class CommentService {

    // final 修饰：依赖在构造后不可变，确保线程安全发布与不可变约束
    private final CommentRepository repository;
    private final UserRepository userRepository;

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：构造注入可声明 final、便于单元测试 mock、启动期即可发现循环依赖
     */
    public CommentService(CommentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /**
     * 发表评论
     *
     * @param articleId 文章 ID
     * @param userId    用户 ID
     * @param req       评论请求
     * @return 评论响应
     */
    public CommentResponse publish(Long articleId, Long userId, CommentRequest req) {
        Comment comment = new Comment(articleId, userId, req.content());
        repository.save(comment);
        // 查询用户用于填充响应中的昵称/头像；orElse(null) 容忍极端情况下用户被删除的场景
        User user = userRepository.findById(userId).orElse(null);
        return CommentResponse.from(comment, user);
    }

    /**
     * 按文章查询评论（前台文章详情页用）
     *
     * @param articleId 文章 ID
     * @param page      页码
     * @param size      每页条数
     * @return 评论分页
     */
    public Page<CommentResponse> listByArticle(Long articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 按创建时间倒序展示：最新评论优先，符合阅读习惯
        // N+1 查询说明：每条评论都查一次用户表，数据量小可接受；若评论量大需改用 IN 批量查询用户
        return repository.findByArticleIdOrderByCreateTimeDesc(articleId, pageable)
                .map(c -> CommentResponse.from(c, userRepository.findById(c.getUserId()).orElse(null)));
    }

    /**
     * 查询全部评论（后台审核用）
     *
     * @param page 页码
     * @param size 每页条数
     * @return 评论分页
     */
    public Page<CommentResponse> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByOrderByCreateTimeDesc(pageable)
                .map(c -> CommentResponse.from(c, userRepository.findById(c.getUserId()).orElse(null)));
    }

    /**
     * 删除评论（后台审核或用户自查删除）
     *
     * @param id 评论 ID
     * @throws IllegalArgumentException 评论不存在
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            // 抛 IllegalArgumentException：评论 ID 不存在属"参数指向的对象无效"，参数层面错误
            throw new IllegalArgumentException("评论不存在");
        }
        repository.deleteById(id);
    }

    /**
     * 统计评论总数（仪表板用）
     */
    public long count() {
        return repository.count();
    }
}

// 声明当前类所在包路径
package com.example.java3.service;

// 导入反馈回复请求 DTO
import com.example.java3.dto.FeedbackReplyRequest;
// 导入反馈请求 DTO
import com.example.java3.dto.FeedbackRequest;
// 导入反馈实体模型
import com.example.java3.model.Feedback;
// 导入反馈仓储接口（Spring Data JPA）
import com.example.java3.repository.FeedbackRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 意见反馈服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class FeedbackService {

    // 反馈仓储，处理反馈表 CRUD
    private final FeedbackRepository feedbackRepository;

    // 构造方法注入反馈仓储 Bean
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * 提交反馈
     *
     * @param userId 用户 ID
     * @param req    反馈请求
     * @return 反馈实体
     */
    public Feedback create(Long userId, FeedbackRequest req) {
        // 构造反馈实体，包含用户 ID、标题、内容
        Feedback f = new Feedback(userId, req.getTitle(), req.getContent());
        // 持久化并返回
        return feedbackRepository.save(f);
    }

    /**
     * 查询用户的反馈
     *
     * @param userId 用户 ID
     * @return 反馈列表
     */
    public List<Feedback> findByUser(Long userId) {
        // 按用户 ID 查询反馈，按创建时间降序（最新反馈在前）
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 查询全部反馈（后台）
     *
     * @return 反馈列表
     */
    public List<Feedback> findAll() {
        // 查询全部反馈，按创建时间降序
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 管理员回复反馈
     *
     * @param id  反馈 ID
     * @param req 回复请求
     */
    public void reply(Long id, FeedbackReplyRequest req) {
        // 根据 ID 查询反馈
        Feedback f = feedbackRepository.findById(id)
                // 反馈不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("反馈不存在"));
        // 设置管理员回复内容
        f.setReply(req.getReply());
        // 标记为已处理，便于后台统计未处理数量
        f.setHandled(true);
        // 持久化
        feedbackRepository.save(f);
    }
}

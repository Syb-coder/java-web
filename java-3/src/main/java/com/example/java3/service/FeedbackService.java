package com.example.java3.service;

import com.example.java3.dto.FeedbackReplyRequest;
import com.example.java3.dto.FeedbackRequest;
import com.example.java3.model.Feedback;
import com.example.java3.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 意见反馈服务
 */
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

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
        Feedback f = new Feedback(userId, req.getTitle(), req.getContent());
        return feedbackRepository.save(f);
    }

    /**
     * 查询用户的反馈
     *
     * @param userId 用户 ID
     * @return 反馈列表
     */
    public List<Feedback> findByUser(Long userId) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 查询全部反馈（后台）
     *
     * @return 反馈列表
     */
    public List<Feedback> findAll() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 管理员回复反馈
     *
     * @param id  反馈 ID
     * @param req 回复请求
     */
    public void reply(Long id, FeedbackReplyRequest req) {
        Feedback f = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("反馈不存在"));
        f.setReply(req.getReply());
        f.setHandled(true);
        feedbackRepository.save(f);
    }
}

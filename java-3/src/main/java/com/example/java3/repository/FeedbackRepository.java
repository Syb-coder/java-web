package com.example.java3.repository;

import com.example.java3.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 意见反馈仓储
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * 查询全部反馈，按时间倒序
     *
     * @return 反馈列表
     */
    List<Feedback> findAllByOrderByCreatedAtDesc();

    /**
     * 查询用户提交的反馈
     *
     * @param userId 用户 ID
     * @return 反馈列表
     */
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
}

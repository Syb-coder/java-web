package com.example.java3.repository;

import com.example.java3.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 交易评价仓储
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 按订单查询评价
     *
     * @param orderId 订单 ID
     * @return 评价 Optional
     */
    Optional<Review> findByOrderId(Long orderId);

    /**
     * 查询被评价者收到的全部评价
     *
     * @param revieweeId 被评价者 ID
     * @return 评价列表
     */
    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);
}

package com.example.java3.service;

import com.example.java3.dto.ReviewRequest;
import com.example.java3.model.Order;
import com.example.java3.model.OrderStatus;
import com.example.java3.model.Review;
import com.example.java3.repository.OrderRepository;
import com.example.java3.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易评价服务
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 发表评价
     *
     * @param userId 当前用户 ID
     * @param req    评价请求
     * @return 评价实体
     */
    public Review create(Long userId, ReviewRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 仅买家可评价
        if (!order.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可评价");
        }
        // 订单需已完成
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("订单未完成，不可评价");
        }
        // 一单只能评价一次
        if (reviewRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalArgumentException("该订单已评价");
        }
        Review r = new Review(order.getId(), userId, order.getSellerId(),
                req.getRating(), req.getContent());
        return reviewRepository.save(r);
    }

    /**
     * 查询卖家收到的评价
     *
     * @param sellerId 卖家 ID
     * @return 评价列表
     */
    public List<Review> findBySeller(Long sellerId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(sellerId);
    }
}

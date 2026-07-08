// 声明当前类所在包路径
package com.example.java3.service;

// 导入评价请求 DTO
import com.example.java3.dto.ReviewRequest;
// 导入订单实体模型
import com.example.java3.model.Order;
// 导入订单状态枚举
import com.example.java3.model.OrderStatus;
// 导入评价实体模型
import com.example.java3.model.Review;
// 导入订单仓储接口（Spring Data JPA）
import com.example.java3.repository.OrderRepository;
// 导入评价仓储接口
import com.example.java3.repository.ReviewRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 交易评价服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class ReviewService {

    // 评价仓储，处理评价表 CRUD
    private final ReviewRepository reviewRepository;
    // 订单仓储，用于校验订单状态和买家身份
    private final OrderRepository orderRepository;

    // 构造方法注入两个仓储 Bean
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
        // 根据 ID 查询订单
        Order order = orderRepository.findById(req.getOrderId())
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 仅买家可评价，限制评价主体
        if (!order.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可评价");
        }
        // 订单需已完成，未完成订单不可评价
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("订单未完成，不可评价");
        }
        // 一单只能评价一次，避免重复评价刷分
        if (reviewRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalArgumentException("该订单已评价");
        }
        // 构造评价实体，包含订单 ID、评价者 ID、被评价者 ID（卖家）、评分、内容
        Review r = new Review(order.getId(), userId, order.getSellerId(),
                req.getRating(), req.getContent());
        // 持久化并返回
        return reviewRepository.save(r);
    }

    /**
     * 查询卖家收到的评价
     *
     * @param sellerId 卖家 ID
     * @return 评价列表
     */
    public List<Review> findBySeller(Long sellerId) {
        // 按被评价者 ID 查询评价，按创建时间降序（最新评价在前）
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(sellerId);
    }
}

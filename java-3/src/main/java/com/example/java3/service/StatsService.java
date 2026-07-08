package com.example.java3.service;

import com.example.java3.dto.StatsResponse;
import com.example.java3.model.OrderStatus;
import com.example.java3.model.ProductAuditStatus;
import com.example.java3.model.UserStatus;
import com.example.java3.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务
 * <p>
 * 用于数据可视化：平台整体指标、热门品类、月度交易量等。
 * </p>
 */
@Service
public class StatsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FeedbackRepository feedbackRepository;
    private final ProductCategoryRepository categoryRepository;

    public StatsService(UserRepository userRepository,
                       ProductRepository productRepository,
                       OrderRepository orderRepository,
                       FeedbackRepository feedbackRepository,
                       ProductCategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.feedbackRepository = feedbackRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 平台整体统计
     *
     * @return 统计响应
     */
    public StatsResponse overview() {
        StatsResponse s = new StatsResponse();
        s.setUserCount(userRepository.count());
        s.setBannedUserCount(userRepository.countByStatus(UserStatus.BANNED));
        s.setProductCount(productRepository.count());
        s.setPendingProductCount(productRepository.countByAuditStatus(ProductAuditStatus.PENDING));
        s.setApprovedProductCount(productRepository.countByAuditStatus(ProductAuditStatus.APPROVED));
        s.setOrderCount(orderRepository.count());
        s.setCompletedOrderCount(orderRepository.countByStatus(OrderStatus.COMPLETED));
        s.setPendingFeedbackCount(feedbackRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(f -> !Boolean.TRUE.equals(f.getHandled()))
                .count());
        return s;
    }

    /**
     * 热门品类统计（按已通过且未售出的商品数量排序）
     *
     * @return list of {categoryName, count}
     */
    public List<Map<String, Object>> hotCategories() {
        List<Map<String, Object>> result = new ArrayList<>();
        categoryRepository.findAllByOrderBySortOrderAsc().forEach(c -> {
            long count = productRepository.countByCategoryIdAndAuditStatusAndSold(
                    c.getId(), ProductAuditStatus.APPROVED, false);
            Map<String, Object> item = new HashMap<>();
            item.put("categoryName", c.getName());
            item.put("count", count);
            result.add(item);
        });
        // 按数量降序
        result.sort((a, b) -> Long.compare((long) b.get("count"), (long) a.get("count")));
        return result;
    }

    /**
     * 月度交易量统计（按订单创建月份聚合）
     *
     * @return list of {month, count}
     */
    public List<Map<String, Object>> monthlyOrders() {
        List<Map<String, Object>> result = new ArrayList<>();
        orderRepository.findAllByOrderByCreatedAtDesc().forEach(o -> {
            // 按 yyyy-MM 聚合
            String month = o.getCreatedAt().getYear() + "-" +
                    String.format("%02d", o.getCreatedAt().getMonthValue());
            // 查找是否已存在该月份
            Map<String, Object> found = null;
            for (Map<String, Object> item : result) {
                if (month.equals(item.get("month"))) {
                    found = item;
                    break;
                }
            }
            if (found == null) {
                Map<String, Object> item = new HashMap<>();
                item.put("month", month);
                item.put("count", 1L);
                result.add(item);
            } else {
                found.put("count", (long) found.get("count") + 1);
            }
        });
        // 按月份升序
        result.sort((a, b) -> String.valueOf(a.get("month")).compareTo(String.valueOf(b.get("month"))));
        return result;
    }
}

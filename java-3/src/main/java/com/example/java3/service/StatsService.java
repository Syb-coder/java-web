// 声明当前类所在包路径
package com.example.java3.service;

// 导入统计响应 DTO
import com.example.java3.dto.StatsResponse;
// 导入订单状态枚举
import com.example.java3.model.OrderStatus;
// 导入商品审核状态枚举
import com.example.java3.model.ProductAuditStatus;
// 导入用户状态枚举
import com.example.java3.model.UserStatus;
// 导入 repository 包下全部仓储接口
import com.example.java3.repository.*;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 ArrayList
import java.util.ArrayList;
// 导入 HashMap
import java.util.HashMap;
// 导入 List 集合
import java.util.List;
// 导入 Map 接口
import java.util.Map;

/**
 * 数据统计服务
 * <p>
 * 用于数据可视化：平台整体指标、热门品类、月度交易量等。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class StatsService {

    // 用户仓储，用于统计用户总数和封禁数
    private final UserRepository userRepository;
    // 商品仓储，用于统计商品数量
    private final ProductRepository productRepository;
    // 订单仓储，用于统计订单数量
    private final OrderRepository orderRepository;
    // 反馈仓储，用于统计未处理反馈数
    private final FeedbackRepository feedbackRepository;
    // 分类仓储，用于统计热门品类
    private final ProductCategoryRepository categoryRepository;

    // 构造方法注入五个仓储 Bean
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
        // 构造统计响应对象
        StatsResponse s = new StatsResponse();
        // 用户总数
        s.setUserCount(userRepository.count());
        // 封禁用户数
        s.setBannedUserCount(userRepository.countByStatus(UserStatus.BANNED));
        // 商品总数
        s.setProductCount(productRepository.count());
        // 待审核商品数
        s.setPendingProductCount(productRepository.countByAuditStatus(ProductAuditStatus.PENDING));
        // 已通过审核商品数
        s.setApprovedProductCount(productRepository.countByAuditStatus(ProductAuditStatus.APPROVED));
        // 订单总数
        s.setOrderCount(orderRepository.count());
        // 已完成订单数
        s.setCompletedOrderCount(orderRepository.countByStatus(OrderStatus.COMPLETED));
        // 未处理反馈数：先查询全部反馈，再过滤出 handled 不为 true 的，统计数量
        s.setPendingFeedbackCount(feedbackRepository.findAllByOrderByCreatedAtDesc().stream()
                // 过滤未处理反馈
                .filter(f -> !Boolean.TRUE.equals(f.getHandled()))
                // 计数
                .count());
        // 返回统计结果
        return s;
    }

    /**
     * 热门品类统计（按已通过且未售出的商品数量排序）
     *
     * @return list of {categoryName, count}
     */
    public List<Map<String, Object>> hotCategories() {
        // 构造结果列表
        List<Map<String, Object>> result = new ArrayList<>();
        // 遍历全部分类
        categoryRepository.findAllByOrderBySortOrderAsc().forEach(c -> {
            // 统计该分类下已通过审核且未售出的商品数量
            long count = productRepository.countByCategoryIdAndAuditStatusAndSold(
                    c.getId(), ProductAuditStatus.APPROVED, false);
            // 构造结果项 Map
            Map<String, Object> item = new HashMap<>();
            // 放入分类名称
            item.put("categoryName", c.getName());
            // 放入商品数量
            item.put("count", count);
            // 加入结果列表
            result.add(item);
        });
        // 按数量降序排序，热门品类展示在前
        result.sort((a, b) -> Long.compare((long) b.get("count"), (long) a.get("count")));
        // 返回排序后的结果
        return result;
    }

    /**
     * 月度交易量统计（按订单创建月份聚合）
     *
     * @return list of {month, count}
     */
    public List<Map<String, Object>> monthlyOrders() {
        // 构造结果列表
        List<Map<String, Object>> result = new ArrayList<>();
        // 遍历全部订单（按创建时间降序）
        orderRepository.findAllByOrderByCreatedAtDesc().forEach(o -> {
            // 按 yyyy-MM 聚合，月份补零保证两位数
            String month = o.getCreatedAt().getYear() + "-" +
                    String.format("%02d", o.getCreatedAt().getMonthValue());
            // 查找是否已存在该月份的统计项
            Map<String, Object> found = null;
            for (Map<String, Object> item : result) {
                // 月份匹配则标记为已找到
                if (month.equals(item.get("month"))) {
                    found = item;
                    break;
                }
            }
            // 不存在则新增月份统计项
            if (found == null) {
                Map<String, Object> item = new HashMap<>();
                // 放入月份
                item.put("month", month);
                // 初始计数为 1
                item.put("count", 1L);
                // 加入结果列表
                result.add(item);
            } else {
                // 已存在则计数加 1
                found.put("count", (long) found.get("count") + 1);
            }
        });
        // 按月份升序排序，便于图表按时间轴展示
        result.sort((a, b) -> String.valueOf(a.get("month")).compareTo(String.valueOf(b.get("month"))));
        // 返回聚合结果
        return result;
    }
}

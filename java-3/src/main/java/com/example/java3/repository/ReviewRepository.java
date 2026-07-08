// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Review 实体类，对应交易评价表
import com.example.java3.model.Review;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;
// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 交易评价仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 按订单查询评价
     *
     * @param orderId 订单 ID
     * @return 评价 Optional
     */
    // 按订单 ID 查询对应的交易评价，一个订单至多对应一条评价
    // 返回 Optional 防止空指针，调用方可据此判断订单是否已评价
    Optional<Review> findByOrderId(Long orderId);

    /**
     * 查询被评价者收到的全部评价
     *
     * @param revieweeId 被评价者 ID
     * @return 评价列表
     */
    // 按 revieweeId（被评价者 ID）查询其收到的全部评价，按 createdAt 降序排列
    // 用于展示用户的信誉评价历史
    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);
}

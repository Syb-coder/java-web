// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Order 实体类，对应订单表
import com.example.java3.model.Order;
// 导入 OrderStatus 枚举类，定义订单的各种状态（待支付、已完成等）
import com.example.java3.model.OrderStatus;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;
// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 订单仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 按订单号查询
     *
     * @param orderNo 订单号
     * @return 订单 Optional
     */
    // 按业务订单号 orderNo 查询订单，订单号是用户可见的业务主键（不同于数据库主键 ID）
    // 返回 Optional 防止空指针，订单号唯一，至多返回一条
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 查询买家全部订单（按时间倒序）
     *
     * @param buyerId 买家 ID
     * @return 订单列表
     */
    // 按买家 ID 查询其全部购买订单，按 createdAt 降序排列，便于优先查看最新订单
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    /**
     * 查询卖家全部订单（按时间倒序）
     *
     * @param sellerId 卖家 ID
     * @return 订单列表
     */
    // 按卖家 ID 查询其全部销售订单，按 createdAt 降序排列
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    /**
     * 查询用户相关订单（买家或卖家）
     *
     * @param buyerId  买家 ID
     * @param sellerId 卖家 ID
     * @return 订单列表
     */
    // 查询用户作为买家或卖家参与的订单，Or 语义：满足任一条件即返回
    // 通常 buyerId 和 sellerId 传入相同用户 ID，便于用户查看全部相关订单
    List<Order> findByBuyerIdOrSellerIdOrderByCreatedAtDesc(Long buyerId, Long sellerId);

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 数量
     */
    // 按 OrderStatus 枚举值统计订单数量，用于后台仪表盘展示各状态订单数
    long countByStatus(OrderStatus status);

    /**
     * 查询全部订单（按时间倒序）
     *
     * @return 订单列表
     */
    // 查询全部订单，按 createdAt 降序排列，用于后台管理端订单列表展示
    List<Order> findAllByOrderByCreatedAtDesc();

    /**
     * 统计订单总数
     *
     * @return 数量
     */
    // 统计订单总数量，JpaRepository 继承而来的方法，用于后台统计概览
    long count();
}

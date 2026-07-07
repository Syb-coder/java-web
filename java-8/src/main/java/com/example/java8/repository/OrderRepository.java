// 声明包路径：repository 层封装数据访问
package com.example.java8.repository;

// 导入订单实体：包含订单状态、总价、关联的面料/款式/用户等信息
import com.example.java8.model.Order;
// 导入 Spring Data JPA 基础接口：自动提供基础 CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 List：用于返回多条记录的结果集
import java.util.List;

/**
 * 订单仓储
 *
 * <p>提供订单的持久化访问与按用户筛选、按时间倒序查询能力。
 * 订单是业务核心实体，关联用户、面料、款式、量体数据，
 * 状态机为：PENDING → MEASURING → CUTTING → SEWING → FITTING → COMPLETED。</p>
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 按用户 ID 查询订单（按下单时间倒序）
     *
     * <p>用于前台"我的订单"页面，按时间倒序展示最近订单优先。
     * 方法名中的 OrderByCreateTimeDesc 部分会被框架解析为
     * {@code ORDER BY create_time DESC}。</p>
     *
     * <p>框架翻译为：
     * {@code SELECT * FROM orders WHERE user_id = ? ORDER BY create_time DESC}</p>
     *
     * @param userId 用户 ID
     * @return 该用户的所有订单（最近的在最前）
     */
    // 方法名约定：findBy + 字段 + OrderBy + 排序字段 + Desc/Asc，框架据此生成排序查询
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 查询全部订单（按下单时间倒序）
     *
     * <p>用于后台管理仪表盘，展示所有用户的订单，最新订单优先。
     * 与 JpaRepository 自带的 findAll() 区别在于支持自定义排序。</p>
     *
     * <p>框架翻译为：
     * {@code SELECT * FROM orders ORDER BY create_time DESC}</p>
     *
     * @return 所有订单（最近的在最前）
     */
    // 方法名约定：findAllBy + OrderBy + 排序字段 + Desc，框架生成全表排序查询
    List<Order> findAllByOrderByCreateTimeDesc();
}

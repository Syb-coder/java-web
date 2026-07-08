// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Appointment 实体类，对应自提预约表
import com.example.java3.model.Appointment;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 自提预约仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    // 按订单 ID 查询预约信息，用于买家或卖家查看某订单对应的自提预约
    // 返回 Optional 防止空指针，一个订单至多对应一个预约
    java.util.Optional<Appointment> findByOrderId(Long orderId);

    /**
     * 查询用户相关预约（作为买家或卖家）
     *
     * @param buyerId  买家 ID
     * @param sellerId 卖家 ID
     * @return 预约列表
     */
    // 查询用户作为买家或卖家参与的全部预约，按 appointmentTime 倒序排列
    // 方法名 Or 语义：满足 buyerId 或 sellerId 任一条件即返回，便于用户查看自己的全部预约
    List<Appointment> findByBuyerIdOrSellerIdOrderByAppointmentTimeDesc(Long buyerId, Long sellerId);
}

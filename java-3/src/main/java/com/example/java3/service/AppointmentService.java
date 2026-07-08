// 声明当前类所在包路径
package com.example.java3.service;

// 导入预约请求 DTO
import com.example.java3.dto.AppointmentRequest;
// 导入预约实体模型
import com.example.java3.model.Appointment;
// 导入订单实体模型
import com.example.java3.model.Order;
// 导入预约仓储接口（Spring Data JPA）
import com.example.java3.repository.AppointmentRepository;
// 导入订单仓储接口
import com.example.java3.repository.OrderRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 自提预约服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class AppointmentService {

    // 预约仓储，处理预约表 CRUD
    private final AppointmentRepository appointmentRepository;
    // 订单仓储，用于校验订单存在性和买家身份
    private final OrderRepository orderRepository;

    // 构造方法注入两个仓储 Bean
    public AppointmentService(AppointmentRepository appointmentRepository,
                              OrderRepository orderRepository) {
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 创建预约
     *
     * @param userId 当前用户 ID（买家）
     * @param req    预约请求
     * @return 预约实体
     */
    public Appointment create(Long userId, AppointmentRequest req) {
        // 根据 ID 查询订单
        Order order = orderRepository.findById(req.getOrderId())
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 校验当前用户为订单买家，避免卖家或第三方发起预约
        if (!order.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可发起预约");
        }
        // 构造预约实体，包含订单 ID、买家 ID、卖家 ID、预约时间、地点、备注
        Appointment a = new Appointment(order.getId(), order.getBuyerId(), order.getSellerId(),
                req.getAppointmentTime(), req.getLocation(), req.getRemark());
        // 持久化并返回
        return appointmentRepository.save(a);
    }

    /**
     * 查询用户相关预约
     *
     * @param userId 用户 ID
     * @return 预约列表
     */
    public List<Appointment> findByUser(Long userId) {
        // 查询用户作为买家或卖家的所有预约，按预约时间降序（最近在前）
        return appointmentRepository.findByBuyerIdOrSellerIdOrderByAppointmentTimeDesc(userId, userId);
    }

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    public java.util.Optional<Appointment> findByOrder(Long orderId) {
        // 按订单 ID 查询预约，返回 Optional 便于调用方处理不存在的情况
        return appointmentRepository.findByOrderId(orderId);
    }
}

package com.example.java3.service;

import com.example.java3.dto.AppointmentRequest;
import com.example.java3.model.Appointment;
import com.example.java3.model.Order;
import com.example.java3.repository.AppointmentRepository;
import com.example.java3.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自提预约服务
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;

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
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 校验当前用户为订单买家
        if (!order.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可发起预约");
        }
        Appointment a = new Appointment(order.getId(), order.getBuyerId(), order.getSellerId(),
                req.getAppointmentTime(), req.getLocation(), req.getRemark());
        return appointmentRepository.save(a);
    }

    /**
     * 查询用户相关预约
     *
     * @param userId 用户 ID
     * @return 预约列表
     */
    public List<Appointment> findByUser(Long userId) {
        return appointmentRepository.findByBuyerIdOrSellerIdOrderByAppointmentTimeDesc(userId, userId);
    }

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    public java.util.Optional<Appointment> findByOrder(Long orderId) {
        return appointmentRepository.findByOrderId(orderId);
    }
}

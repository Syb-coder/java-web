package com.example.java3.repository;

import com.example.java3.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自提预约仓储
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    java.util.Optional<Appointment> findByOrderId(Long orderId);

    /**
     * 查询用户相关预约（作为买家或卖家）
     *
     * @param buyerId  买家 ID
     * @param sellerId 卖家 ID
     * @return 预约列表
     */
    List<Appointment> findByBuyerIdOrSellerIdOrderByAppointmentTimeDesc(Long buyerId, Long sellerId);
}

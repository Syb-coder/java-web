package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 自提预约请求 DTO
 */
public class AppointmentRequest {

    /** 关联订单 ID */
    @NotNull(message = "订单不能为空")
    private Long orderId;

    /** 预约时间（ISO 格式：yyyy-MM-ddTHH:mm:ss） */
    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    /** 自提地点 */
    @NotBlank(message = "地点不能为空")
    @Size(max = 200, message = "地点长度不能超过 200")
    private String location;

    /** 备注 */
    private String remark;

    // ===== Getter / Setter =====

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

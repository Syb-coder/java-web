// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 NotNull 校验注解，确保对象非 null
import jakarta.validation.constraints.NotNull;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

// 导入 LocalDateTime，Java 8 时间 API，表示不带时区的日期时间
import java.time.LocalDateTime;

/**
 * 自提预约请求 DTO
 * <p>
 * 买家下单后与卖家约定线下自提的时间、地点等信息。
 * </p>
 */
public class AppointmentRequest {

    // 关联订单 ID 字段，必填，预约必须绑定具体订单
    /** 关联订单 ID */
    @NotNull(message = "订单不能为空")
    private Long orderId;

    // 预约时间字段，必填，前端以 ISO 格式提交，Spring 自动反序列化为 LocalDateTime
    /** 预约时间（ISO 格式：yyyy-MM-ddTHH:mm:ss） */
    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    // 自提地点字段，必填，限制最长 200 字符
    /** 自提地点 */
    @NotBlank(message = "地点不能为空")
    // Size：限定地点字符串最长 200 字符
    @Size(max = 200, message = "地点长度不能超过 200")
    private String location;

    // 备注字段，可选，补充自提相关说明
    /** 备注 */
    private String remark;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取订单 ID
    public Long getOrderId() {
        return orderId;
    }

    // 设置订单 ID
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 获取预约时间
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    // 设置预约时间
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    // 获取自提地点
    public String getLocation() {
        return location;
    }

    // 设置自提地点
    public void setLocation(String location) {
        this.location = location;
    }

    // 获取备注
    public String getRemark() {
        return remark;
    }

    // 设置备注
    public void setRemark(String remark) {
        this.remark = remark;
    }
}

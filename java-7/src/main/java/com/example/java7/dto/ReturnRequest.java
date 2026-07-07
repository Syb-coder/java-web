package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

import jakarta.validation.constraints.NotNull; // 引入 @NotNull 校验注解，校验非 null

import java.time.LocalDate; // 引入 LocalDate，作为归还日期字段类型

/**
 * 还书请求 DTO
 * <p>
 * 职责：封装还书操作入参，仅需借阅记录 ID 与实际归还日期。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>recordId 为必填，用于定位待归还的借阅记录</li>
 *   <li>returnDate 由前端传入，便于补录历史还书日期</li>
 *   <li>remark 选填，可记录图书损坏等异常情况</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class ReturnRequest { // 还书请求 DTO 类定义

    /** 借阅记录 ID，必填 */
    @NotNull(message = "借阅记录 ID 不能为空") // 校验注解：recordId 非 null，防止空值查询
    private Long recordId; // 借阅记录 ID 字段，关联 BorrowRecord 实体主键

    /** 实际归还日期，必填，默认由前端传当天日期 */
    @NotNull(message = "归还日期不能为空") // 校验注解：归还日期非 null
    private LocalDate returnDate; // 实际归还日期字段，记录图书归还时间

    /** 备注，选填（如记录图书损坏情况） */
    private String remark; // 备注字段，可记录图书损坏等异常情况

    // ===== Getter / Setter =====

    public Long getRecordId() { // Getter 方法：返回借阅记录 ID
        return recordId; // 返回 recordId 字段值
    }

    public void setRecordId(Long recordId) { // Setter 方法：设置借阅记录 ID
        this.recordId = recordId; // 将入参赋值给实例字段 recordId
    }

    public LocalDate getReturnDate() { // Getter 方法：返回实际归还日期
        return returnDate; // 返回 returnDate 字段值
    }

    public void setReturnDate(LocalDate returnDate) { // Setter 方法：设置实际归还日期
        this.returnDate = returnDate; // 将入参赋值给实例字段 returnDate
    }

    public String getRemark() { // Getter 方法：返回备注
        return remark; // 返回 remark 字段值
    }

    public void setRemark(String remark) { // Setter 方法：设置备注
        this.remark = remark; // 将入参赋值给实例字段 remark
    }
}

package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

import jakarta.validation.constraints.Future; // 引入 @Future 校验注解，校验日期为未来时间
import jakarta.validation.constraints.Min; // 引入 @Min 校验注解，校验数值最小值
import jakarta.validation.constraints.NotBlank; // 引入 @NotBlank 校验注解，校验字符串非空白
import jakarta.validation.constraints.NotNull; // 引入 @NotNull 校验注解，校验非 null

import java.time.LocalDate; // 引入 LocalDate，作为日期字段类型（不含时分秒）

/**
 * 借书请求 DTO
 * <p>
 * 职责：封装借书操作入参，校验图书 ID、借阅人姓名、借出日期、应还日期合法性。
 * 应还日期必须晚于借出日期，由 @Future 保证未来时间约束（编辑场景需放宽）。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>bookId 使用 @NotNull 校验，防止空指针引发查询异常</li>
 *   <li>dueDate 使用 @Future 强制约束为未来日期，避免录入已过期应还日期</li>
 *   <li>borrowDays 为辅助字段，后端可基于 borrowDate 与 dueDate 自行计算</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class BorrowRequest { // 借书请求 DTO 类定义

    /** 借阅图书 ID，必填 */
    @NotNull(message = "图书 ID 不能为空") // 校验注解：bookId 非 null，防止空值查询
    private Long bookId; // 图书 ID 字段，关联 Book 实体主键

    /** 借阅人姓名，必填 */
    @NotBlank(message = "借阅人姓名不能为空") // 校验注解：姓名非空白字符串
    private String borrowerName; // 借阅人姓名字段，记录借阅者身份

    /** 借出日期，必填 */
    @NotNull(message = "借出日期不能为空") // 校验注解：借出日期非 null
    private LocalDate borrowDate; // 借出日期字段，记录图书借出时间

    /** 应还日期，必填且必须晚于今天 */
    @NotNull(message = "应还日期不能为空") // 校验注解：应还日期非 null
    @Future(message = "应还日期必须是未来日期") // 校验注解：应还日期必须严格晚于当前日期，防止录入过去日期
    private LocalDate dueDate; // 应还日期字段，记录图书预期归还时间

    /** 借阅天数（前端辅助字段，后端可直接用 dueDate 计算） */
    @Min(value = 1, message = "借阅天数至少为 1 天") // 校验注解：借阅天数最小值为 1，防止录入 0 或负数
    private Integer borrowDays; // 借阅天数字段，前端辅助字段，可为 null

    /** 备注，选填 */
    private String remark; // 备注字段，存储借阅附加信息

    // ===== Getter / Setter =====

    public Long getBookId() { // Getter 方法：返回图书 ID
        return bookId; // 返回 bookId 字段值
    }

    public void setBookId(Long bookId) { // Setter 方法：设置图书 ID
        this.bookId = bookId; // 将入参赋值给实例字段 bookId
    }

    public String getBorrowerName() { // Getter 方法：返回借阅人姓名
        return borrowerName; // 返回 borrowerName 字段值
    }

    public void setBorrowerName(String borrowerName) { // Setter 方法：设置借阅人姓名
        this.borrowerName = borrowerName; // 将入参赋值给实例字段 borrowerName
    }

    public LocalDate getBorrowDate() { // Getter 方法：返回借出日期
        return borrowDate; // 返回 borrowDate 字段值
    }

    public void setBorrowDate(LocalDate borrowDate) { // Setter 方法：设置借出日期
        this.borrowDate = borrowDate; // 将入参赋值给实例字段 borrowDate
    }

    public LocalDate getDueDate() { // Getter 方法：返回应还日期
        return dueDate; // 返回 dueDate 字段值
    }

    public void setDueDate(LocalDate dueDate) { // Setter 方法：设置应还日期
        this.dueDate = dueDate; // 将入参赋值给实例字段 dueDate
    }

    public Integer getBorrowDays() { // Getter 方法：返回借阅天数
        return borrowDays; // 返回 borrowDays 字段值
    }

    public void setBorrowDays(Integer borrowDays) { // Setter 方法：设置借阅天数
        this.borrowDays = borrowDays; // 将入参赋值给实例字段 borrowDays
    }

    public String getRemark() { // Getter 方法：返回备注
        return remark; // 返回 remark 字段值
    }

    public void setRemark(String remark) { // Setter 方法：设置备注
        this.remark = remark; // 将入参赋值给实例字段 remark
    }
}

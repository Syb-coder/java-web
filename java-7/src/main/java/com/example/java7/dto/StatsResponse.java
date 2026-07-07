package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

/**
 * 图书借阅统计 DTO
 * <p>
 * 职责：聚合首页统计数据，避免前端发起多次请求。
 * 包含图书总数、可借数量、借出数量、逾期数量等核心指标。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>使用 long 类型承载统计值，避免大数量场景下 int 溢出</li>
 *   <li>所有字段由 Service 层聚合查询后统一填充，单次接口返回全部指标</li>
 *   <li>无校验注解，因为该 DTO 为响应对象，不接收前端入参</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class StatsResponse { // 统计响应 DTO 类定义

    /** 图书种类总数 */
    private long totalBooks; // 图书种类数字段，对应 Book 表记录数

    /** 图书副本总数 */
    private long totalCopies; // 图书副本总数字段，所有图书 totalCopies 之和

    /** 当前可借副本总数 */
    private long availableCopies; // 可借副本总数字段，所有图书 availableCopies 之和

    /** 当前借出中记录数 */
    private long borrowedCount; // 借出中记录数字段，status 为 BORROWED 的记录数

    /** 已归还记录数 */
    private long returnedCount; // 已归还记录数字段，status 为 RETURNED 的记录数

    /** 逾期未还记录数 */
    private long overdueCount; // 逾期记录数字段，status 为 OVERDUE 的记录数

    // ===== Getter / Setter =====

    public long getTotalBooks() { // Getter 方法：返回图书种类总数
        return totalBooks; // 返回 totalBooks 字段值
    }

    public void setTotalBooks(long totalBooks) { // Setter 方法：设置图书种类总数
        this.totalBooks = totalBooks; // 将入参赋值给实例字段 totalBooks
    }

    public long getTotalCopies() { // Getter 方法：返回图书副本总数
        return totalCopies; // 返回 totalCopies 字段值
    }

    public void setTotalCopies(long totalCopies) { // Setter 方法：设置图书副本总数
        this.totalCopies = totalCopies; // 将入参赋值给实例字段 totalCopies
    }

    public long getAvailableCopies() { // Getter 方法：返回可借副本总数
        return availableCopies; // 返回 availableCopies 字段值
    }

    public void setAvailableCopies(long availableCopies) { // Setter 方法：设置可借副本总数
        this.availableCopies = availableCopies; // 将入参赋值给实例字段 availableCopies
    }

    public long getBorrowedCount() { // Getter 方法：返回借出中记录数
        return borrowedCount; // 返回 borrowedCount 字段值
    }

    public void setBorrowedCount(long borrowedCount) { // Setter 方法：设置借出中记录数
        this.borrowedCount = borrowedCount; // 将入参赋值给实例字段 borrowedCount
    }

    public long getReturnedCount() { // Getter 方法：返回已归还记录数
        return returnedCount; // 返回 returnedCount 字段值
    }

    public void setReturnedCount(long returnedCount) { // Setter 方法：设置已归还记录数
        this.returnedCount = returnedCount; // 将入参赋值给实例字段 returnedCount
    }

    public long getOverdueCount() { // Getter 方法：返回逾期记录数
        return overdueCount; // 返回 overdueCount 字段值
    }

    public void setOverdueCount(long overdueCount) { // Setter 方法：设置逾期记录数
        this.overdueCount = overdueCount; // 将入参赋值给实例字段 overdueCount
    }
}

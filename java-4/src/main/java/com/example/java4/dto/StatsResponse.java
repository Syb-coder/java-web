// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

import java.util.List;
import java.util.Map;

/**
 * 数据统计响应 DTO
 * <p>
 * 后台管理端首页仪表盘展示的汇总数据，包括馆藏统计、借阅统计、读者统计、热门图书等。
 * </p>
 */
public class StatsResponse {

    /** 馆藏图书总数（按种类计） */
    private long totalBooks;

    /** 馆藏副本总数（按册数计） */
    private long totalCopies;

    /** 可借副本总数 */
    private long availableCopies;

    /** 注册读者总数 */
    private long totalReaders;

    /** 学生读者数 */
    private long studentCount;

    /** 教师读者数 */
    private long teacherCount;

    /** 借阅中记录数 */
    private long borrowingCount;

    /** 已归还记录数 */
    private long returnedCount;

    /** 已逾期记录数 */
    private long overdueCount;

    /** 借阅记录总数 */
    private long totalBorrowRecords;

    /** 分类统计（分类名称 -> 图书数量） */
    private List<Map<String, Object>> categoryStats;

    /** 热门借阅图书 Top5（书名 -> 借阅次数） */
    private List<Map<String, Object>> hotBooks;

    /** 无参构造方法 */
    public StatsResponse() {
    }

    // ===== Getter / Setter =====
    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
    public long getTotalCopies() { return totalCopies; }
    public void setTotalCopies(long totalCopies) { this.totalCopies = totalCopies; }
    public long getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(long availableCopies) { this.availableCopies = availableCopies; }
    public long getTotalReaders() { return totalReaders; }
    public void setTotalReaders(long totalReaders) { this.totalReaders = totalReaders; }
    public long getStudentCount() { return studentCount; }
    public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
    public long getTeacherCount() { return teacherCount; }
    public void setTeacherCount(long teacherCount) { this.teacherCount = teacherCount; }
    public long getBorrowingCount() { return borrowingCount; }
    public void setBorrowingCount(long borrowingCount) { this.borrowingCount = borrowingCount; }
    public long getReturnedCount() { return returnedCount; }
    public void setReturnedCount(long returnedCount) { this.returnedCount = returnedCount; }
    public long getOverdueCount() { return overdueCount; }
    public void setOverdueCount(long overdueCount) { this.overdueCount = overdueCount; }
    public long getTotalBorrowRecords() { return totalBorrowRecords; }
    public void setTotalBorrowRecords(long totalBorrowRecords) { this.totalBorrowRecords = totalBorrowRecords; }
    public List<Map<String, Object>> getCategoryStats() { return categoryStats; }
    public void setCategoryStats(List<Map<String, Object>> categoryStats) { this.categoryStats = categoryStats; }
    public List<Map<String, Object>> getHotBooks() { return hotBooks; }
    public void setHotBooks(List<Map<String, Object>> hotBooks) { this.hotBooks = hotBooks; }
}

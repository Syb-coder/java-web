package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

import com.example.java7.model.BorrowRecord; // 引入 BorrowRecord 实体类，用于构造响应对象
import com.example.java7.model.BorrowStatus; // 引入借阅状态枚举，作为 status 字段类型

import java.time.LocalDate; // 引入 LocalDate，作为日期字段类型

/**
 * 借阅记录响应 DTO
 * <p>
 * 职责：将 BorrowRecord 实体转换为前端展示对象，
 * 同时携带图书基本信息（书名、作者），避免前端二次查询。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>冗余 bookTitle、bookAuthor 字段，避免前端根据 bookId 再次查询图书信息</li>
 *   <li>overdue 字段后端计算后下发，前端无需重复实现逾期判定逻辑</li>
 *   <li>statusDisplayName 提供状态中文显示名，前端直接展示无需枚举字典</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class BorrowRecordResponse { // 借阅记录响应 DTO 类定义

    /** 记录 ID */
    private Long id; // 借阅记录唯一标识，对应数据库主键

    /** 图书 ID */
    private Long bookId; // 图书 ID 字段，关联 Book 实体

    /** 书名（冗余字段，便于前端展示） */
    private String bookTitle; // 书名字段，从关联 Book 实体获取，避免前端二次查询

    /** 作者（冗余字段） */
    private String bookAuthor; // 作者字段，从关联 Book 实体获取

    /** 借阅人姓名 */
    private String borrowerName; // 借阅人姓名字段，记录借阅者

    /** 借出日期 */
    private LocalDate borrowDate; // 借出日期字段，记录图书借出时间

    /** 应还日期 */
    private LocalDate dueDate; // 应还日期字段，记录预期归还时间

    /** 实际归还日期 */
    private LocalDate returnDate; // 实际归还日期字段，未归还时为 null

    /** 借阅状态 */
    private BorrowStatus status; // 借阅状态字段，枚举类型（BORROWED/RETURNED/OVERDUE）

    /** 状态中文显示名 */
    private String statusDisplayName; // 状态中文显示名，冗余字段便于前端直接展示

    /** 备注 */
    private String remark; // 备注字段，存储借阅附加信息

    /** 是否已逾期（前端高亮显示） */
    private boolean overdue; // 逾期标记字段，true 表示已逾期，前端用于高亮显示

    /**
     * 从实体构造响应对象
     * <p>
     * 通过构造函数完成实体到 DTO 的字段映射，并计算 overdue 逾期标记。
     * 逾期判定逻辑：状态为 BORROWED 且当前日期超过应还日期。
     * </p>
     *
     * @param record 借阅记录实体
     */
    public BorrowRecordResponse(BorrowRecord record) { // 构造函数：接收 BorrowRecord 实体进行字段映射
        this.id = record.getId(); // 映射记录 ID
        this.bookId = record.getBook().getId(); // 通过关联实体 Book 获取图书 ID
        this.bookTitle = record.getBook().getTitle(); // 通过关联实体 Book 获取书名
        this.bookAuthor = record.getBook().getAuthor(); // 通过关联实体 Book 获取作者
        this.borrowerName = record.getBorrowerName(); // 映射借阅人姓名
        this.borrowDate = record.getBorrowDate(); // 映射借出日期
        this.dueDate = record.getDueDate(); // 映射应还日期
        this.returnDate = record.getReturnDate(); // 映射实际归还日期
        this.status = record.getStatus(); // 映射借阅状态
        this.statusDisplayName = record.getStatus() != null // 三元表达式：状态非 null 时获取显示名，否则设为 null，避免 NPE
                ? record.getStatus().getDisplayName() : null; // 调用枚举的 getDisplayName 方法获取中文显示名
        this.remark = record.getRemark(); // 映射备注
        // 逾期判定：状态为 BORROWED 且当前日期超过应还日期
        this.overdue = record.getStatus() == BorrowStatus.BORROWED // 条件一：状态必须为 BORROWED（已归还的不算逾期）
                && record.getDueDate() != null // 条件二：应还日期非 null，防止 NPE
                && LocalDate.now().isAfter(record.getDueDate()); // 条件三：当前日期晚于应还日期，三者同时满足才标记逾期
    }

    // ===== Getter / Setter =====

    public Long getId() { // Getter 方法：返回记录 ID
        return id; // 返回 id 字段值
    }

    public void setId(Long id) { // Setter 方法：设置记录 ID
        this.id = id; // 将入参赋值给实例字段 id
    }

    public Long getBookId() { // Getter 方法：返回图书 ID
        return bookId; // 返回 bookId 字段值
    }

    public void setBookId(Long bookId) { // Setter 方法：设置图书 ID
        this.bookId = bookId; // 将入参赋值给实例字段 bookId
    }

    public String getBookTitle() { // Getter 方法：返回书名
        return bookTitle; // 返回 bookTitle 字段值
    }

    public void setBookTitle(String bookTitle) { // Setter 方法：设置书名
        this.bookTitle = bookTitle; // 将入参赋值给实例字段 bookTitle
    }

    public String getBookAuthor() { // Getter 方法：返回作者
        return bookAuthor; // 返回 bookAuthor 字段值
    }

    public void setBookAuthor(String bookAuthor) { // Setter 方法：设置作者
        this.bookAuthor = bookAuthor; // 将入参赋值给实例字段 bookAuthor
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

    public LocalDate getReturnDate() { // Getter 方法：返回实际归还日期
        return returnDate; // 返回 returnDate 字段值
    }

    public void setReturnDate(LocalDate returnDate) { // Setter 方法：设置实际归还日期
        this.returnDate = returnDate; // 将入参赋值给实例字段 returnDate
    }

    public BorrowStatus getStatus() { // Getter 方法：返回借阅状态
        return status; // 返回 status 字段值
    }

    public void setStatus(BorrowStatus status) { // Setter 方法：设置借阅状态
        this.status = status; // 将入参赋值给实例字段 status
    }

    public String getStatusDisplayName() { // Getter 方法：返回状态中文显示名
        return statusDisplayName; // 返回 statusDisplayName 字段值
    }

    public void setStatusDisplayName(String statusDisplayName) { // Setter 方法：设置状态中文显示名
        this.statusDisplayName = statusDisplayName; // 将入参赋值给实例字段 statusDisplayName
    }

    public String getRemark() { // Getter 方法：返回备注
        return remark; // 返回 remark 字段值
    }

    public void setRemark(String remark) { // Setter 方法：设置备注
        this.remark = remark; // 将入参赋值给实例字段 remark
    }

    public boolean isOverdue() { // Getter 方法：返回逾期标记（boolean 类型 Getter 使用 is 前缀）
        return overdue; // 返回 overdue 字段值
    }

    public void setOverdue(boolean overdue) { // Setter 方法：设置逾期标记
        this.overdue = overdue; // 将入参赋值给实例字段 overdue
    }
}

// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入时间类型
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借阅记录响应 DTO
 * <p>
 * 返回给前端的借阅记录展示数据，包含图书信息与借阅状态。
 * </p>
 */
public class BorrowRecordResponse {

    /** 借阅记录 ID */
    private Long id;

    /** 图书 ID */
    private Long bookId;

    /** 书名 */
    private String bookTitle;

    /** 作者 */
    private String bookAuthor;

    /** 分类名称 */
    private String categoryName;

    /** 图书封面 */
    private String coverImage;

    /** 读者 ID */
    private Long readerId;

    /** 读者姓名 */
    private String readerName;

    /** 学号/工号 */
    private String readerNo;

    /** 借出日期 */
    private LocalDate borrowDate;

    /** 应还日期 */
    private LocalDate dueDate;

    /** 实际归还日期 */
    private LocalDate returnDate;

    /** 借阅状态：BORROWING/RETURNED/OVERDUE */
    private String status;

    /** 续借次数 */
    private Integer renewCount;

    /** 罚款金额 */
    private Double fine;

    /** 备注 */
    private String remark;

    /** 是否可续借（已续借 1 次或已逾期则不可续借） */
    private boolean renewable;

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法 */
    public BorrowRecordResponse() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Long getReaderId() { return readerId; }
    public void setReaderId(Long readerId) { this.readerId = readerId; }
    public String getReaderName() { return readerName; }
    public void setReaderName(String readerName) { this.readerName = readerName; }
    public String getReaderNo() { return readerNo; }
    public void setReaderNo(String readerNo) { this.readerNo = readerNo; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getRenewCount() { return renewCount; }
    public void setRenewCount(Integer renewCount) { this.renewCount = renewCount; }
    public Double getFine() { return fine; }
    public void setFine(Double fine) { this.fine = fine; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public boolean isRenewable() { return renewable; }
    public void setRenewable(boolean renewable) { this.renewable = renewable; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

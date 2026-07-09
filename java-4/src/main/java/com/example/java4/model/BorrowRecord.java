// 声明包路径，存放 JPA 实体类
package com.example.java4.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

// 导入日期类型（仅日期，不含时间）
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借阅记录实体
 * <p>
 * 记录每本图书的借出、归还历史。设计要点：
 * <ul>
 *   <li>与 Book、Reader 建立多对一关系；</li>
 *   <li>borrowDate、dueDate、returnDate 三字段共同支撑逾期判断；</li>
 *   <li>renewCount 记录续借次数，限制最多续借 1 次；</li>
 *   <li>fine 字段记录超期罚款金额，归还时计算。</li>
 * </ul>
 * </p>
 * <p>
 * 扩展说明：
 * - 使用 @ManyToOne 而非 @OneToMany 双向关联，避免反序列化死循环与性能开销；
 * - 借阅日期使用 LocalDate 而非 LocalDateTime，因借阅按天计费，无需时分秒；
 * - @Version 乐观锁防止两人同时归还同一记录导致状态错乱。
 * </p>
 */
@Entity // 声明本类为 JPA 实体
@Table(name = "borrow_records") // 指定映射表名为 borrow_records
public class BorrowRecord {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 关联图书，多对一关系 */
    @ManyToOne(fetch = FetchType.EAGER) // 多对一：多条借阅记录可关联同一本图书；EAGER 立即加载图书信息便于展示
    @JoinColumn(name = "book_id", nullable = false) // 指定外键列为 book_id，非空
    private Book book;

    /** 关联读者，多对一关系 */
    @ManyToOne(fetch = FetchType.EAGER) // 多对一：多条借阅记录可关联同一位读者；EAGER 立即加载读者信息便于展示
    @JoinColumn(name = "reader_id", nullable = false) // 指定外键列为 reader_id，非空
    private Reader reader;

    /** 借出日期 */
    @Column(nullable = false) // 非空
    private LocalDate borrowDate;

    /** 应还日期，用于逾期判定 */
    @Column(nullable = false) // 非空，借阅时由服务层根据读者类型借期计算填入
    private LocalDate dueDate;

    /** 实际归还日期，归还前为 null */
    private LocalDate returnDate;

    /** 借阅状态 */
    @Enumerated(EnumType.STRING) // 枚举以字符串持久化，便于阅读与排查
    @Column(nullable = false, length = 20) // 非空、长度 20
    private BorrowStatus status;

    /** 续借次数，初始为 0，续借时 +1 */
    @Column(nullable = false) // 非空，初始为 0
    private Integer renewCount;

    /** 超期罚款金额（元），归还时计算，未归还时为 0 */
    @Column(nullable = false) // 非空，未归还时为 0
    private Double fine;

    /** 备注，如借阅用途、续借说明等 */
    @Column(length = 500) // 长度 500，允许为空
    private String remark;

    /** 最后修改时间，更新时由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /** 乐观锁版本号 */
    @Version // 声明乐观锁版本字段
    private Integer version;

    /** 更新前自动设置最后修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public BorrowRecord() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }
    public Integer getRenewCount() { return renewCount; }
    public void setRenewCount(Integer renewCount) { this.renewCount = renewCount; }
    public Double getFine() { return fine; }
    public void setFine(Double fine) { this.fine = fine; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}

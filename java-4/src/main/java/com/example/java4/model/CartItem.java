// 声明包路径，存放 JPA 实体类
package com.example.java4.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 借阅车项实体
 * <p>
 * 读者在用户端将图书"加入借阅车"后产生的记录，类似于电商购物车。
 * 提交借阅时，借阅车中的项会被转换为借阅记录（{@link BorrowRecord}），并清除对应的借阅车项。
 * </p>
 * <p>
 * 设计要点：
 * 1. 通过 (reader_id, book_id) 唯一约束防止同一读者重复加入同一图书；
 * 2. 关联 Book 和 Reader 均使用 EAGER 加载，因借阅车展示时必须显示图书信息；
 * 3. 借阅车是临时态，借阅成功后即删除，不保留历史。
 * </p>
 */
@Entity // 声明本类为 JPA 实体
@Table(name = "cart_items", uniqueConstraints = {
        // 唯一约束：同一读者不能重复加入同一本图书到借阅车
        @UniqueConstraint(columnNames = {"reader_id", "book_id"})
})
public class CartItem {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 关联读者，多对一关系 */
    @ManyToOne(fetch = FetchType.EAGER) // 多对一：一个读者的借阅车可有多个项
    @JoinColumn(name = "reader_id", nullable = false) // 指定外键列为 reader_id，非空
    private Reader reader;

    /** 关联图书，多对一关系 */
    @ManyToOne(fetch = FetchType.EAGER) // 多对一：借阅车项关联一本图书
    @JoinColumn(name = "book_id", nullable = false) // 指定外键列为 book_id，非空
    private Book book;

    /** 加入借阅车的时间 */
    @Column(nullable = false, updatable = false) // 非空、不可更新
    private LocalDateTime createTime;

    /** 无参构造方法，JPA 规范要求 */
    public CartItem() {
    }

    /**
     * 业务构造方法
     *
     * @param reader 关联读者
     * @param book   关联图书
     */
    public CartItem(Reader reader, Book book) {
        this.reader = reader;
        this.book = book;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

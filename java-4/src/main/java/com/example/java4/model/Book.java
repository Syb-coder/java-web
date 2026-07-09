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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 图书实体
 * <p>
 * 描述馆藏图书的元数据与库存状态。
 * 设计要点：
 * <ul>
 *   <li>description 字段使用 length=1000，避免 VARCHAR(255) 默认限制截断简介；</li>
 *   <li>availableCopies 与 totalCopies 分离，支持多副本馆藏；</li>
 *   <li>@Version 字段实现乐观锁，防止并发借阅导致超借；</li>
 *   <li>关联 BookCategory 实现分类管理。</li>
 * </ul>
 * </p>
 */
@Entity // 声明本类为 JPA 实体
@Table(name = "books") // 指定映射表名为 books
public class Book {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 书名 */
    @Column(nullable = false, length = 200) // 非空、长度 200
    private String title;

    /** 作者 */
    @Column(nullable = false, length = 100) // 非空、长度 100
    private String author;

    /** ISBN 国际标准书号，可为空 */
    @Column(length = 20) // 长度 20，允许为空
    private String isbn;

    /** 所属分类，多对一关系 */
    @ManyToOne(fetch = FetchType.EAGER) // 多对一：多本图书属于同一分类；EAGER 立即加载，避免懒加载异常
    @JoinColumn(name = "category_id") // 指定外键列为 category_id
    private BookCategory category;

    /** 出版社 */
    @Column(length = 100) // 长度 100，允许为空
    private String publisher;

    /** 出版年份 */
    private Integer publishYear;

    /** 图书简介，length 扩展至 1000 以容纳较长描述 */
    @Column(length = 1000) // 长度 1000，避免简介被默认 VARCHAR(255) 截断
    private String description;

    /** 图书封面图片 URL */
    @Column(length = 500) // 长度 500，存储图片 URL
    private String coverImage;

    /** 总馆藏数量（副本数） */
    @Column(nullable = false) // 非空
    private Integer totalCopies;

    /** 可借数量，借出时减一，归还时加一 */
    @Column(nullable = false) // 非空，业务层据此判断是否可借
    private Integer availableCopies;

    /** 存放位置（如书架编号"A区3排"） */
    @Column(length = 50) // 长度 50，允许为空
    private String location;

    /** 记录创建时间，由服务层填充，不可更新 */
    @Column(nullable = false, updatable = false) // 非空、不可更新
    private LocalDateTime createTime;

    /** 最后修改时间，更新时由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /** 乐观锁版本号，由 JPA 自动维护，防止并发修改 */
    @Version // 声明乐观锁版本字段
    private Integer version;

    /** 更新前自动设置最后修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public Book() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public BookCategory getCategory() { return category; }
    public void setCategory(BookCategory category) { this.category = category; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public Integer getPublishYear() { return publishYear; }
    public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}

package com.example.java7.model; // 声明包路径为 com.example.java7.model，存放 JPA 实体类

import jakarta.persistence.Column; // 引入 JPA 列映射注解，用于定义字段与数据库列的映射关系
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为数据库实体
import jakarta.persistence.EnumType; // 引入 JPA 枚举类型枚举，指定枚举的持久化方式
import jakarta.persistence.Enumerated; // 引入 JPA 枚举注解，标记枚举字段的存储方式
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举
import jakarta.persistence.Id; // 引入 JPA 主键注解
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table; // 引入 JPA 表映射注解
import jakarta.persistence.Version; // 引入 JPA 乐观锁版本字段注解

import java.time.LocalDateTime; // 引入 Java 8 日期时间类，用于记录创建时间（含时分秒）

/**
 * 图书实体类
 * <p>
 * 职责：映射 books 表，描述个人藏书的元数据与库存状态。
 * 设计要点：
 * 1. description 字段使用 length=1000，避免 VARCHAR(255) 默认限制截断简介内容；
 * 2. availableCopies 与 totalCopies 分离，支持多副本藏书场景；
 * 3. @Version 字段实现乐观锁，防止并发借阅导致超借。
 * </p>
 * <p>
 * 扩展说明：
 * - 实体类采用 JPA 规范，与 Spring Data JPA 无缝集成；
 * - 字段命名采用驼峰命名，JPA 默认按驼峰转下划线策略映射列名；
 * - 乐观锁通过 version 字段实现，更新时自动校验版本号，避免脏写。
 * </p>
 */
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射
@Table(name = "books") // 指定映射的数据库表名为 books
public class Book { // 定义公共实体类 Book

    /** 主键 ID，自增策略 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增（如 MySQL AUTO_INCREMENT）
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT

    /** 书名 */
    @Column(nullable = false, length = 200) // 映射列非空，长度限制 200 字符
    private String title; // 书名字段

    /** 作者 */
    @Column(nullable = false, length = 100) // 映射列非空，长度限制 100 字符
    private String author; // 作者字段

    /** ISBN 国际标准书号，可为空（部分老书无 ISBN） */
    @Column(length = 20) // 映射列长度 20，允许为空
    private String isbn; // ISBN 字段，部分老书可能无 ISBN 故允许 null

    /** 分类，使用枚举字符串存储，保证可读性与可扩展性 */
    @Enumerated(EnumType.STRING) // 指定枚举以字符串形式持久化，便于阅读与排查
    @Column(nullable = false, length = 20) // 映射列非空，长度 20 足以容纳枚举名
    private BookCategory category; // 分类字段，使用 BookCategory 枚举类型保证类型安全

    /** 出版社 */
    @Column(length = 100) // 映射列长度 100，允许为空
    private String publisher; // 出版社字段

    /** 出版年份 */
    private Integer publishYear; // 出版年份数字，使用 Integer 而非 int 以允许 null（年份未知时）

    /** 图书简介，length 扩展至 1000 以容纳较长描述 */
    @Column(length = 1000) // 映射列长度扩展至 1000，避免简介被截断
    private String description; // 图书简介字段

    /** 总馆藏数量（副本数） */
    @Column(nullable = false) // 映射列非空
    private Integer totalCopies; // 总副本数量，表示馆藏该书的副本总数

    /** 可借数量，借出时减一，归还时加一 */
    @Column(nullable = false) // 映射列非空
    private Integer availableCopies; // 当前可借副本数量，借出减一归还加一

    /** 存放位置（如书架编号） */
    @Column(length = 50) // 映射列长度 50，允许为空
    private String location; // 存放位置字段，如书架编号

    /** 记录创建时间，由服务层填充，不可更新 */
    @Column(nullable = false, updatable = false) // 映射列非空且不可更新（updatable=false）
    private LocalDateTime createTime; // 创建时间字段，由服务层在持久化前填充

    private LocalDateTime updateTime;

    /** 乐观锁版本号，由 JPA 自动维护，防止并发修改 */
    @Version // 声明乐观锁版本字段，Hibernate 在更新时自动校验并递增该值
    private Integer version; // 版本号字段，并发更新冲突时抛出 OptimisticLockException

    /**
     * 无参构造方法，JPA 规范要求
     * <p>
     * JPA/Hibernate 实体类必须提供无参构造方法，用于反射创建实例。
     * 该方法访问级别需为 protected 或 public，不可为 private。
     * </p>
     */
    public Book() { // 无参构造方法，供 JPA 反射调用
    }

    /**
     * 业务构造方法：创建新书时使用，默认可借数量等于总数量
     * <p>
     * 该构造方法封装了新书创建的核心字段赋值逻辑，简化业务层调用。
     * 未传入的字段（如 isbn、publisher 等）保持 null，由业务层按需补充。
     * </p>
     *
     * @param title           书名
     * @param author          作者
     * @param category        分类
     * @param totalCopies     总数量
     * @param availableCopies 可借数量
     */
    public Book(String title, String author, BookCategory category, // 构造方法参数：书名、作者、分类
                Integer totalCopies, Integer availableCopies) { // 构造方法参数：总数量、可借数量
        this.title = title; // 赋值书名
        this.author = author; // 赋值作者
        this.category = category; // 赋值分类
        this.totalCopies = totalCopies; // 赋值总副本数量
        this.availableCopies = availableCopies; // 赋值可借副本数量
    }

    // ===== Getter / Setter =====
    // 保留完整 getter/setter 以便 JPA 反射注入与前端序列化

    /**
     * 获取主键 ID
     *
     * @return 主键 ID
     */
    public Long getId() { // 主键 ID 的 getter 方法，供查询时获取标识
        return id; // 返回主键 ID
    }

    /**
     * 设置主键 ID
     *
     * @param id 主键 ID
     */
    public void setId(Long id) { // 主键 ID 的 setter 方法，通常由 JPA 自动填充
        this.id = id; // 赋值主键 ID
    }

    /**
     * 获取书名
     *
     * @return 书名
     */
    public String getTitle() { // 书名的 getter 方法，供前端展示与查询使用
        return title; // 返回书名
    }

    /**
     * 设置书名
     *
     * @param title 书名
     */
    public void setTitle(String title) { // 书名的 setter 方法，供创建或修改时赋值
        this.title = title; // 赋值书名
    }

    /**
     * 获取作者
     *
     * @return 作者
     */
    public String getAuthor() { // 作者的 getter 方法
        return author; // 返回作者
    }

    /**
     * 设置作者
     *
     * @param author 作者
     */
    public void setAuthor(String author) { // 作者的 setter 方法
        this.author = author; // 赋值作者
    }

    /**
     * 获取 ISBN 国际标准书号
     *
     * @return ISBN
     */
    public String getIsbn() { // ISBN 的 getter 方法
        return isbn; // 返回 ISBN
    }

    /**
     * 设置 ISBN 国际标准书号
     *
     * @param isbn ISBN
     */
    public void setIsbn(String isbn) { // ISBN 的 setter 方法
        this.isbn = isbn; // 赋值 ISBN
    }

    /**
     * 获取图书分类
     *
     * @return 分类
     */
    public BookCategory getCategory() { // 分类的 getter 方法，返回枚举类型
        return category; // 返回分类
    }

    /**
     * 设置图书分类
     *
     * @param category 分类
     */
    public void setCategory(BookCategory category) { // 分类的 setter 方法
        this.category = category; // 赋值分类
    }

    /**
     * 获取出版社
     *
     * @return 出版社
     */
    public String getPublisher() { // 出版社的 getter 方法
        return publisher; // 返回出版社
    }

    /**
     * 设置出版社
     *
     * @param publisher 出版社
     */
    public void setPublisher(String publisher) { // 出版社的 setter 方法
        this.publisher = publisher; // 赋值出版社
    }

    /**
     * 获取出版年份
     *
     * @return 出版年份
     */
    public Integer getPublishYear() { // 出版年份的 getter 方法
        return publishYear; // 返回出版年份
    }

    /**
     * 设置出版年份
     *
     * @param publishYear 出版年份
     */
    public void setPublishYear(Integer publishYear) { // 出版年份的 setter 方法
        this.publishYear = publishYear; // 赋值出版年份
    }

    /**
     * 获取图书简介
     *
     * @return 图书简介
     */
    public String getDescription() { // 图书简介的 getter 方法
        return description; // 返回图书简介
    }

    /**
     * 设置图书简介
     *
     * @param description 图书简介
     */
    public void setDescription(String description) { // 图书简介的 setter 方法
        this.description = description; // 赋值图书简介
    }

    /**
     * 获取总馆藏数量
     *
     * @return 总数量
     */
    public Integer getTotalCopies() { // 总数量的 getter 方法
        return totalCopies; // 返回总副本数量
    }

    /**
     * 设置总馆藏数量
     *
     * @param totalCopies 总数量
     */
    public void setTotalCopies(Integer totalCopies) { // 总数量的 setter 方法
        this.totalCopies = totalCopies; // 赋值总副本数量
    }

    /**
     * 获取可借数量
     *
     * @return 可借数量
     */
    public Integer getAvailableCopies() { // 可借数量的 getter 方法，业务层据此判断是否可借
        return availableCopies; // 返回可借副本数量
    }

    /**
     * 设置可借数量
     *
     * @param availableCopies 可借数量
     */
    public void setAvailableCopies(Integer availableCopies) { // 可借数量的 setter 方法，借出归还时由业务层调用
        this.availableCopies = availableCopies; // 赋值可借副本数量
    }

    /**
     * 获取存放位置
     *
     * @return 存放位置
     */
    public String getLocation() { // 存放位置的 getter 方法
        return location; // 返回存放位置
    }

    /**
     * 设置存放位置
     *
     * @param location 存放位置
     */
    public void setLocation(String location) { // 存放位置的 setter 方法
        this.location = location; // 赋值存放位置
    }

    /**
     * 获取记录创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() { // 创建时间的 getter 方法
        return createTime; // 返回创建时间
    }

    /**
     * 设置记录创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) { // 创建时间的 setter 方法，由服务层填充
        this.createTime = createTime; // 赋值创建时间
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取乐观锁版本号
     *
     * @return 版本号
     */
    public Integer getVersion() { // 版本号的 getter 方法
        return version; // 返回乐观锁版本号
    }

    /**
     * 设置乐观锁版本号
     *
     * @param version 版本号
     */
    public void setVersion(Integer version) { // 版本号的 setter 方法，通常由 JPA 自动维护
        this.version = version; // 赋值乐观锁版本号
    }
}

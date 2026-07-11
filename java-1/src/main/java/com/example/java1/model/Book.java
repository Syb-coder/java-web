// 声明包路径，存放 JPA 实体类
package com.example.java1.model; // 声明包路径为 com.example.java1.model，集中存放实体类

// 导入 JPA 注解
import jakarta.persistence.Column; // 引入 JPA 列映射注解，定义字段与数据库列的约束
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为可持久化数据库实体
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举，IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 引入 JPA 主键注解，标记字段为表主键
import jakarta.persistence.Table; // 引入 JPA 表映射注解，指定实体对应的表名
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version; // 引入 JPA 乐观锁版本字段注解，防止并发脏写

// 导入时间类型
import java.time.LocalDateTime; // 引入 Java 8 日期时间类，含时分秒，用于记录创建时间

/**
 * 图书实体
 * <p>
 * 描述馆藏图书的元数据与库存状态。
 * 设计要点：
 * <ul>
 *   <li>description 字段使用 length=1000，避免 VARCHAR(255) 默认限制截断简介；</li>
 *   <li>availableCopies 与 totalCopies 分离，支持多副本馆藏；</li>
 *   <li>@Version 字段实现乐观锁，防止并发借阅导致超借。</li>
 * </ul>
 * </p>
 * <p>
 * 扩展说明：
 * - 实体类采用 JPA 规范，与 Spring Data JPA 无缝集成；
 * - 字段命名采用驼峰命名，JPA 默认按驼峰转下划线策略映射列名；
 * - 乐观锁通过 version 字段实现，更新时自动校验版本号，避免脏写。
 * </p>
 */
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射并生成 DDL
@Table(name = "books") // 指定映射的数据库表名为 books，显式声明避免命名策略歧义
public class Book { // 定义公共实体类 Book，对应馆藏图书领域模型

    /** 主键 ID，自增 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增（H2/MySQL 均支持）
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT

    /** 书名 */
    @Column(nullable = false, length = 200) // 映射列非空，长度 200 足以容纳大多数书名
    private String title; // 书名字段，用于检索与展示

    /** 作者 */
    @Column(nullable = false, length = 100) // 映射列非空，长度 100 容纳作者姓名
    private String author; // 作者字段，作为检索条件之一

    /** ISBN 国际标准书号，可为空（部分老书无 ISBN） */
    @Column(length = 20) // 映射列长度 20，允许为空以兼容无 ISBN 的老书
    private String isbn; // ISBN 字段，部分老书可能无 ISBN 故允许 null

    /** 分类（中图法分类名，如"计算机/TP3"），字符串存储便于扩展 */
    @Column(length = 50) // 映射列长度 50，允许为空；用字符串而非枚举便于后续扩展分类体系
    private String category; // 分类字段，采用字符串存储以兼容中图法等多级分类

    /** 出版社 */
    @Column(length = 100) // 映射列长度 100，允许为空
    private String publisher; // 出版社字段，用于图书编目

    /** 出版年份 */
    private Integer publishYear; // 出版年份数字，使用 Integer 而非 int 以允许 null（年份未知时）

    /** 图书简介，length 扩展至 1000 以容纳较长描述 */
    @Column(length = 1000) // 映射列长度扩展至 1000，避免简介被默认 VARCHAR(255) 截断
    private String description; // 图书简介字段，供读者检索时了解内容

    /** 总馆藏数量（副本数） */
    @Column(nullable = false) // 映射列非空，馆藏必须有副本
    private Integer totalCopies; // 总副本数量，表示馆藏该书的副本总数

    /** 可借数量，借出时减一，归还时加一 */
    @Column(nullable = false) // 映射列非空，业务层据此判断是否可借
    private Integer availableCopies; // 当前可借副本数量，借出减一归还加一

    /** 存放位置（如书架编号"A区3排"） */
    @Column(length = 50) // 映射列长度 50，允许为空
    private String location; // 存放位置字段，便于线下取书

    /** 记录创建时间，由服务层填充，不可更新 */
    @Column(nullable = false, updatable = false) // 映射列非空且不可更新（updatable=false 保证创建时间不可篡改）
    private LocalDateTime createTime; // 创建时间字段，由服务层在持久化前填充

    /** 最后修改时间，数据更新前由 @PreUpdate 自动填充 */
    private LocalDateTime updateTime;

    /** 乐观锁版本号，由 JPA 自动维护，防止并发修改 */
    @Version // 声明乐观锁版本字段，Hibernate 在更新时自动校验并递增该值
    private Integer version; // 版本号字段，并发更新冲突时抛出 OptimisticLockException

    /** 每次更新前自动填充修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public Book() { // 无参构造方法，供 JPA/Hibernate 通过反射创建实例
    }

    /**
     * 业务构造方法：创建新书时使用，默认可借数量等于总数量
     * <p>
     * 该构造方法封装了新书创建的核心字段赋值逻辑，简化业务层调用。
     * 未传入的字段（如 isbn、publisher 等）保持 null，由业务层按需补充。
     * availableCopies 默认等于 totalCopies，因为新书入库时全部可借。
     * </p>
     *
     * @param title       书名
     * @param author      作者
     * @param category    分类
     * @param totalCopies 总数量
     */
    public Book(String title, String author, String category, Integer totalCopies) { // 业务构造方法，封装新书创建核心字段
        this.title = title; // 赋值书名
        this.author = author; // 赋值作者
        this.category = category; // 赋值分类
        this.totalCopies = totalCopies; // 赋值总副本数量
        this.availableCopies = totalCopies; // 新书入库默认全部可借，故可借数量等于总数量
    }

    // ===== Getter / Setter =====
    // 保留完整 getter/setter 以便 JPA 反射注入与前端序列化
    public Long getId() { return id; } // 主键 ID 的 getter，供查询与外部引用获取标识
    public void setId(Long id) { this.id = id; } // 主键 ID 的 setter，通常由 JPA 自动填充
    public String getTitle() { return title; } // 书名的 getter，供前端展示与查询使用
    public void setTitle(String title) { this.title = title; } // 书名的 setter，供创建或修改时赋值
    public String getAuthor() { return author; } // 作者的 getter，供检索与展示
    public void setAuthor(String author) { this.author = author; } // 作者的 setter，供编目修改时赋值
    public String getIsbn() { return isbn; } // ISBN 的 getter，供扫码与精确查询
    public void setIsbn(String isbn) { this.isbn = isbn; } // ISBN 的 setter，供编目时补充
    public String getCategory() { return category; } // 分类的 getter，供分类检索
    public void setCategory(String category) { this.category = category; } // 分类的 setter，供编目调整时赋值
    public String getPublisher() { return publisher; } // 出版社的 getter，供出版信息展示
    public void setPublisher(String publisher) { this.publisher = publisher; } // 出版社的 setter，供编目时赋值
    public Integer getPublishYear() { return publishYear; } // 出版年份的 getter，供年代检索
    public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; } // 出版年份的 setter，供编目时赋值
    public String getDescription() { return description; } // 图书简介的 getter，供详情页展示
    public void setDescription(String description) { this.description = description; } // 图书简介的 setter，供编目补充
    public Integer getTotalCopies() { return totalCopies; } // 总数量的 getter，供库存统计
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; } // 总数量的 setter，供入库调整时赋值
    public Integer getAvailableCopies() { return availableCopies; } // 可借数量的 getter，业务层据此判断是否可借
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; } // 可借数量的 setter，借出归还时由业务层调用
    public String getLocation() { return location; } // 存放位置的 getter，供线下取书指引
    public void setLocation(String location) { this.location = location; } // 存放位置的 setter，供排架调整时赋值
    public LocalDateTime getCreateTime() { return createTime; } // 创建时间的 getter，供审计展示
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; } // 创建时间的 setter，由服务层在持久化前填充
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getVersion() { return version; } // 版本号的 getter，供乐观锁调试展示
    public void setVersion(Integer version) { this.version = version; } // 版本号的 setter，通常由 JPA 自动维护
}

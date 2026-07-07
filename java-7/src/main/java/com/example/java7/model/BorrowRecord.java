package com.example.java7.model; // 声明包路径为 com.example.java7.model，存放 JPA 实体类

import jakarta.persistence.Column; // 引入 JPA 列映射注解，定义字段与数据库列的映射关系
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为数据库实体
import jakarta.persistence.EnumType; // 引入 JPA 枚举类型枚举，指定枚举的持久化方式
import jakarta.persistence.Enumerated; // 引入 JPA 枚举注解，标记枚举字段的存储方式
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举
import jakarta.persistence.Id; // 引入 JPA 主键注解
import jakarta.persistence.JoinColumn; // 引入 JPA 外键列注解，用于关联关系映射
import jakarta.persistence.ManyToOne; // 引入 JPA 多对一关系注解
import jakarta.persistence.Table; // 引入 JPA 表映射注解
import jakarta.persistence.Version; // 引入 JPA 乐观锁版本字段注解

import java.time.LocalDate; // 引入 Java 8 日期类，用于表示借阅、归还日期（仅含日期不含时间）

/**
 * 借阅记录实体类
 * <p>
 * 职责：映射 borrow_records 表，记录每本图书的借出、归还历史。
 * 设计要点：
 * 1. 与 Book 建立多对一关系，借阅记录归属某本图书；
 * 2. borrowerName 记录借阅人姓名（个人系统简化为字符串）；
 * 3. 借阅日期、应还日期、实际归还日期三字段共同支撑逾期判断；
 * 4. remark 字段 length=500，容纳借阅备注信息。
 * </p>
 * <p>
 * 扩展说明：
 * - 多对一关系默认采用 EAGER 加载策略，本系统借阅记录查询通常需要关联图书信息；
 * - returnDate 为 null 表示尚未归还，业务层据此判断借阅状态；
 * - 乐观锁 version 防止并发归还操作导致状态不一致。
 * </p>
 */
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射
@Table(name = "borrow_records") // 指定映射的数据库表名为 borrow_records
public class BorrowRecord { // 定义公共实体类 BorrowRecord

    /** 主键 ID，自增 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT

    /** 关联图书，多对一关系 */
    @ManyToOne // 声明与 Book 的多对一关系：多条借阅记录对应同一本图书
    @JoinColumn(name = "book_id", nullable = false) // 指定外键列名为 book_id，且不可为空
    private Book book; // 关联的图书实体，建立借阅记录与图书的归属关系

    /** 借阅人姓名（个人系统简化设计） */
    @Column(nullable = false, length = 50) // 映射列非空，长度 50
    private String borrowerName; // 借阅人姓名字段，个人系统简化为字符串不建独立实体

    /** 借出日期 */
    @Column(nullable = false) // 映射列非空
    private LocalDate borrowDate; // 借出日期字段，使用 LocalDate 仅含日期不含时间

    /** 应还日期，用于逾期判定 */
    @Column(nullable = false) // 映射列非空
    private LocalDate dueDate; // 应还日期字段，业务层据此与当前日期比较判断是否逾期

    /** 实际归还日期，归还前为 null */
    private LocalDate returnDate; // 实际归还日期字段，未归还时为 null，归还后填充

    /** 借阅状态 */
    @Enumerated(EnumType.STRING) // 指定枚举以字符串形式持久化，便于阅读与排查
    @Column(nullable = false, length = 20) // 映射列非空，长度 20 足以容纳枚举名
    private BorrowStatus status; // 借阅状态字段，使用 BorrowStatus 枚举类型

    /** 备注，如借阅用途、续借说明等 */
    @Column(length = 500) // 映射列长度 500，容纳较长备注
    private String remark; // 备注字段，允许为空

    /** 乐观锁版本号 */
    @Version // 声明乐观锁版本字段，Hibernate 在更新时自动校验并递增该值
    private Integer version; // 版本号字段，并发更新冲突时抛出 OptimisticLockException

    /**
     * 无参构造方法，JPA 规范要求
     * <p>
     * JPA/Hibernate 实体类必须提供无参构造方法，用于反射创建实例。
     * 该方法访问级别需为 protected 或 public，不可为 private。
     * </p>
     */
    public BorrowRecord() { // 无参构造方法，供 JPA 反射调用
    }

    // ===== Getter / Setter =====

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
     * 获取关联图书
     *
     * @return 关联的图书实体
     */
    public Book getBook() { // 关联图书的 getter 方法，返回关联的 Book 实体
        return book; // 返回关联的图书实体
    }

    /**
     * 设置关联图书
     *
     * @param book 关联的图书实体
     */
    public void setBook(Book book) { // 关联图书的 setter 方法，建立借阅记录与图书的关系
        this.book = book; // 赋值关联的图书实体
    }

    /**
     * 获取借阅人姓名
     *
     * @return 借阅人姓名
     */
    public String getBorrowerName() { // 借阅人姓名的 getter 方法
        return borrowerName; // 返回借阅人姓名
    }

    /**
     * 设置借阅人姓名
     *
     * @param borrowerName 借阅人姓名
     */
    public void setBorrowerName(String borrowerName) { // 借阅人姓名的 setter 方法
        this.borrowerName = borrowerName; // 赋值借阅人姓名
    }

    /**
     * 获取借出日期
     *
     * @return 借出日期
     */
    public LocalDate getBorrowDate() { // 借出日期的 getter 方法
        return borrowDate; // 返回借出日期
    }

    /**
     * 设置借出日期
     *
     * @param borrowDate 借出日期
     */
    public void setBorrowDate(LocalDate borrowDate) { // 借出日期的 setter 方法
        this.borrowDate = borrowDate; // 赋值借出日期
    }

    /**
     * 获取应还日期
     *
     * @return 应还日期
     */
    public LocalDate getDueDate() { // 应还日期的 getter 方法，业务层据此判断是否逾期
        return dueDate; // 返回应还日期
    }

    /**
     * 设置应还日期
     *
     * @param dueDate 应还日期
     */
    public void setDueDate(LocalDate dueDate) { // 应还日期的 setter 方法
        this.dueDate = dueDate; // 赋值应还日期
    }

    /**
     * 获取实际归还日期
     *
     * @return 实际归还日期，未归还时为 null
     */
    public LocalDate getReturnDate() { // 实际归还日期的 getter 方法，null 表示尚未归还
        return returnDate; // 返回实际归还日期
    }

    /**
     * 设置实际归还日期
     *
     * @param returnDate 实际归还日期
     */
    public void setReturnDate(LocalDate returnDate) { // 实际归还日期的 setter 方法，归还时由业务层调用
        this.returnDate = returnDate; // 赋值实际归还日期
    }

    /**
     * 获取借阅状态
     *
     * @return 借阅状态
     */
    public BorrowStatus getStatus() { // 借阅状态的 getter 方法，返回 BorrowStatus 枚举
        return status; // 返回借阅状态
    }

    /**
     * 设置借阅状态
     *
     * @param status 借阅状态
     */
    public void setStatus(BorrowStatus status) { // 借阅状态的 setter 方法，状态流转由业务层控制
        this.status = status; // 赋值借阅状态
    }

    /**
     * 获取备注
     *
     * @return 备注
     */
    public String getRemark() { // 备注的 getter 方法
        return remark; // 返回备注
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) { // 备注的 setter 方法
        this.remark = remark; // 赋值备注
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

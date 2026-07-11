// 声明包路径，存放 JPA 实体类
package com.example.java1.model; // 声明包路径为 com.example.java1.model，集中存放实体类

// 导入 JPA 注解
import jakarta.persistence.Column; // 引入 JPA 列映射注解，定义字段与数据库列的约束
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为可持久化数据库实体
import jakarta.persistence.EnumType; // 引入 JPA 枚举类型枚举，指定枚举的持久化方式（STRING/ORDINAL）
import jakarta.persistence.Enumerated; // 引入 JPA 枚举注解，标记枚举字段的存储方式
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举，IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 引入 JPA 主键注解，标记字段为表主键
import jakarta.persistence.JoinColumn; // 引入 JPA 外键列注解，指定关联关系对应的外键列名
import jakarta.persistence.ManyToOne; // 引入 JPA 多对一关系注解，表示多条借阅记录关联同一本图书/读者
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table; // 引入 JPA 表映射注解，指定实体对应的表名
import jakarta.persistence.Version; // 引入 JPA 乐观锁版本字段注解，防止并发脏写

// 导入日期类型（仅日期，不含时间）
import java.time.LocalDate; // 引入 Java 8 纯日期类，借阅业务只需精确到日，无需时分秒
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
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射
@Table(name = "borrow_records") // 指定映射的数据库表名为 borrow_records，复数命名体现一条记录代表一次借阅
public class BorrowRecord { // 定义公共实体类 BorrowRecord，对应借阅流水领域模型

    /** 主键 ID，自增 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT

    /** 关联图书，多对一关系 */
    @ManyToOne // 多对一关系：多条借阅记录可关联同一本图书
    @JoinColumn(name = "book_id", nullable = false) // 指定外键列为 book_id，非空保证每条记录必须有对应图书
    private Book book; // 关联的图书实体，由 JPA 懒加载，避免未使用时查询全部字段

    /** 关联读者，多对一关系 */
    @ManyToOne // 多对一关系：多条借阅记录可关联同一位读者
    @JoinColumn(name = "reader_id", nullable = false) // 指定外键列为 reader_id，非空保证每条记录必须有对应读者
    private Reader reader; // 关联的读者实体

    /** 借出日期 */
    @Column(nullable = false) // 映射列非空，借阅必须有起始日
    private LocalDate borrowDate; // 借出日期，作为借期计算的起点

    /** 应还日期，用于逾期判定 */
    @Column(nullable = false) // 映射列非空，借阅时由服务层根据 ReaderType 借期计算填入
    private LocalDate dueDate; // 应还日期，与 returnDate 比较判断是否逾期

    /** 实际归还日期，归还前为 null */
    private LocalDate returnDate; // 实际归还日期，归还前为 null，归还时填充用于计算罚款

    /** 借阅状态 */
    @Enumerated(EnumType.STRING) // 枚举以字符串形式持久化，便于阅读与排查；选用 STRING 避免 ORDINAL 顺序调整导致历史数据错乱
    @Column(nullable = false, length = 20) // 映射列非空，长度 20 足以容纳枚举名
    private BorrowStatus status; // 借阅状态字段，使用 BorrowStatus 枚举保证类型安全

    /** 续借次数，初始为 0，续借时 +1 */
    @Column(nullable = false) // 映射列非空，初始为 0
    private Integer renewCount; // 续借次数，业务层据此判断是否还能续借（最多 1 次）

    /** 超期罚款金额（元），归还时计算，未归还时为 0 */
    @Column(nullable = false) // 映射列非空，未归还时为 0 表示暂无罚款
    private Double fine; // 罚款金额，归还时按逾期天数与单价计算

    /** 备注，如借阅用途、续借说明等 */
    @Column(length = 500) // 映射列长度 500，允许为空
    private String remark; // 备注字段，供管理员人工补充说明

    /** 乐观锁版本号 */
    @Version // 声明乐观锁版本字段，Hibernate 在更新时自动校验并递增
    private Integer version; // 版本号字段，防止并发归还/续借导致状态错乱

    /** 最后修改时间，数据更新前由 @PreUpdate 自动填充 */
    private LocalDateTime updateTime;

    /** 每次更新前自动填充修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public BorrowRecord() { // 无参构造方法，供 JPA/Hibernate 通过反射创建实例
    }

    // ===== Getter / Setter =====
    // JPA 通过反射调用 Setter 完成实体属性注入，通过 Getter 读取字段值
    public Long getId() { return id; } // 主键 ID 的 getter，供查询获取标识
    public void setId(Long id) { this.id = id; } // 主键 ID 的 setter，通常由 JPA 自动填充
    public Book getBook() { return book; } // 关联图书的 getter，供业务层获取借阅的是哪本书
    public void setBook(Book book) { this.book = book; } // 关联图书的 setter，借阅时由业务层建立关联
    public Reader getReader() { return reader; } // 关联读者的 getter，供业务层获取借阅人
    public void setReader(Reader reader) { this.reader = reader; } // 关联读者的 setter，借阅时由业务层建立关联
    public LocalDate getBorrowDate() { return borrowDate; } // 借出日期的 getter，供逾期计算与流水展示
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; } // 借出日期的 setter，借阅时由业务层填入
    public LocalDate getDueDate() { return dueDate; } // 应还日期的 getter，供逾期判定与提醒
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; } // 应还日期的 setter，借阅或续借时由业务层计算填入
    public LocalDate getReturnDate() { return returnDate; } // 实际归还日期的 getter，null 表示尚未归还
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; } // 实际归还日期的 setter，归还时由业务层填入
    public BorrowStatus getStatus() { return status; } // 借阅状态的 getter，供界面展示与流程控制
    public void setStatus(BorrowStatus status) { this.status = status; } // 借阅状态的 setter，借出/归还/逾期时由业务层更新
    public Integer getRenewCount() { return renewCount; } // 续借次数的 getter，供判断是否还能续借
    public void setRenewCount(Integer renewCount) { this.renewCount = renewCount; } // 续借次数的 setter，续借时由业务层 +1
    public Double getFine() { return fine; } // 罚款金额的 getter，供结算与展示
    public void setFine(Double fine) { this.fine = fine; } // 罚款金额的 setter，归还时由业务层计算填入
    public String getRemark() { return remark; } // 备注的 getter，供管理员查看说明
    public void setRemark(String remark) { this.remark = remark; } // 备注的 setter，供管理员人工补充
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getVersion() { return version; } // 版本号的 getter，供乐观锁调试展示
    public void setVersion(Integer version) { this.version = version; } // 版本号的 setter，通常由 JPA 自动维护
}

// 声明包路径，存放 JPA 实体类
package com.example.java4.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 读者实体（师生用户）
 * <p>
 * 校园图书馆的借阅主体，分为学生与教师两类，借阅规则由 {@link ReaderType} 决定。
 * 密码使用 BCrypt 加密存储。一个读者可拥有多条借阅记录与借阅车项。
 * </p>
 * <p>
 * 设计要点：
 * 1. readerNo（学号/工号）作为登录账号且唯一，避免重复注册；
 * 2. currentBorrowCount 冗余存储当前借阅数，避免每次借阅都 COUNT 查询借阅表，提升性能；
 * 3. type 字段使用 STRING 持久化，避免未来枚举顺序调整导致历史数据错乱。
 * </p>
 */
@Entity // 声明本类为 JPA 实体
@Table(name = "readers") // 指定映射表名为 readers，复数命名体现一条记录代表一位读者
public class Reader {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 学号 / 工号（唯一，作为登录账号） */
    @Column(nullable = false, unique = true, length = 20) // 非空、唯一、长度 20
    private String readerNo;

    /** 登录密码（BCrypt 加密） */
    @Column(nullable = false, length = 100) // 非空、长度 100
    private String password;

    /** 读者姓名 */
    @Column(nullable = false, length = 50) // 非空、长度 50
    private String name;

    /** 读者类型：学生 / 教师，决定借阅上限与借期 */
    @Enumerated(EnumType.STRING) // 枚举以字符串持久化，便于阅读与排查
    @Column(nullable = false, length = 20) // 非空、长度 20
    private ReaderType type;

    /** 所属院系（如"计算机学院"），可为空 */
    @Column(length = 50)
    private String department;

    /** 联系电话，可为空 */
    @Column(length = 20)
    private String phone;

    /** 当前借阅中的图书数量（借出时 +1，归还时 -1） */
    @Column(nullable = false) // 非空，初始为 0
    private Integer currentBorrowCount;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 注册时间 */
    @Column(nullable = false, updatable = false) // 非空、不可更新
    private LocalDateTime createTime;

    /** 最后修改时间，更新时由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /** 更新前自动设置最后修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法：JPA 规范要求 */
    public Reader() {
    }

    /**
     * 业务构造方法：创建新读者时使用
     *
     * @param readerNo   学号/工号
     * @param password   已 BCrypt 加密的密码
     * @param name       姓名
     * @param type       读者类型
     * @param department 院系
     * @param phone      电话
     */
    public Reader(String readerNo, String password, String name, ReaderType type,
                  String department, String phone) {
        this.readerNo = readerNo;
        this.password = password;
        this.name = name;
        this.type = type;
        this.department = department;
        this.phone = phone;
        this.currentBorrowCount = 0; // 新读者初始借阅数为 0
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReaderNo() { return readerNo; }
    public void setReaderNo(String readerNo) { this.readerNo = readerNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ReaderType getType() { return type; }
    public void setType(ReaderType type) { this.type = type; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getCurrentBorrowCount() { return currentBorrowCount; }
    public void setCurrentBorrowCount(Integer currentBorrowCount) { this.currentBorrowCount = currentBorrowCount; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

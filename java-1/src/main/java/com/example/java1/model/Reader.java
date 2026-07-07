// 声明包路径，存放 JPA 实体类
package com.example.java1.model; // 声明包路径为 com.example.java1.model，集中存放实体类

// 导入 JPA 注解
import jakarta.persistence.Column; // 引入 JPA 列映射注解，定义字段与数据库列的约束
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为可持久化数据库实体
import jakarta.persistence.EnumType; // 引入 JPA 枚举类型枚举，指定枚举的持久化方式
import jakarta.persistence.Enumerated; // 引入 JPA 枚举注解，标记枚举字段的存储方式
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举，IDENTITY 表示数据库自增
import jakarta.persistence.Id; // 引入 JPA 主键注解，标记字段为表主键
import jakarta.persistence.Table; // 引入 JPA 表映射注解，指定实体对应的表名

// 导入时间类型
import java.time.LocalDateTime; // 引入 Java 8 日期时间类，含时分秒，用于记录登录与注册时间

/**
 * 读者实体
 * <p>
 * 校园图书馆的借阅主体，分为学生与教师两类，借阅规则由 {@link ReaderType} 决定。
 * 密码使用 BCrypt 加密存储。一个读者可拥有多条借阅记录（{@link BorrowRecord}）。
 * </p>
 * <p>
 * 设计要点：
 * 1. readerNo 作为登录账号且唯一，避免学号/工号重复注册；
 * 2. currentBorrowCount 冗余存储当前借阅数，避免每次借阅都 COUNT 查询借阅表，提升性能；
 * 3. type 字段使用 STRING 持久化，避免未来枚举顺序调整导致历史数据错乱。
 * </p>
 */
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射
@Table(name = "readers") // 指定映射的数据库表名为 readers，复数命名体现一条记录代表一位读者
public class Reader { // 定义公共实体类 Reader，对应读者领域模型

    /** 主键 ID，自增 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT

    /** 学号 / 工号（唯一，作为登录账号） */
    @Column(nullable = false, unique = true, length = 20) // 非空、唯一、长度 20；唯一约束防止重复注册
    private String readerNo; // 学号/工号字段，作为登录账号与业务主键

    /** 登录密码（BCrypt 加密） */
    @Column(nullable = false, length = 100) // 非空、长度 100，BCrypt 密文 60 字符预留扩展空间
    private String password; // 密码字段，仅存储 BCrypt 密文

    /** 读者姓名 */
    @Column(nullable = false, length = 50) // 非空、长度 50，姓名为必填项
    private String name; // 姓名字段，用于展示与检索

    /** 读者类型：学生 / 教师，决定借阅上限与借期 */
    // @Enumerated(EnumType.STRING) 表示枚举值以字符串形式存入数据库
    // 选用 STRING 而非默认的 ORDINAL，避免未来枚举顺序调整导致历史数据错乱
    @Enumerated(EnumType.STRING) // 枚举以字符串持久化，便于阅读与排查
    @Column(nullable = false, length = 20) // 非空、长度 20 足以容纳枚举名
    private ReaderType type; // 读者类型字段，决定借阅上限与借期

    /** 所属院系（如"计算机学院"），可为空 */
    @Column(length = 50) // 长度 50，允许为空以便读者后续完善
    private String department; // 院系字段，用于统计与展示

    /** 联系电话，可为空 */
    @Column(length = 20) // 长度 20，允许为空
    private String phone; // 电话字段，用于催还通知

    /** 当前借阅中的图书数量（借出时 +1，归还时 -1） */
    @Column(nullable = false) // 非空，初始为 0
    private Integer currentBorrowCount; // 当前借阅数，冗余字段避免频繁 COUNT 查询借阅表

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt; // 最近登录时间，用于安全审计与异常登录排查

    /** 注册时间 */
    private LocalDateTime createTime; // 注册时间，一次性写入用于追溯账号建立

    /** 无参构造方法：JPA 规范要求 */
    public Reader() { // 无参构造方法，供 JPA/Hibernate 通过反射创建实例
    }

    /**
     * 业务构造方法：创建新读者时使用
     * <p>currentBorrowCount 默认为 0，借阅后由业务层维护。</p>
     * <p>
     * 密码由调用方加密后传入，避免在实体层重复加密；
     * createTime 在构造时写入，保证时间一致性。
     * </p>
     *
     * @param readerNo 学号/工号
     * @param password 已 BCrypt 加密的密码
     * @param name     姓名
     * @param type     读者类型
     * @param department 院系
     * @param phone    电话
     */
    public Reader(String readerNo, String password, String name, ReaderType type, // 构造方法参数：账号、密码、姓名、类型
                  String department, String phone) { // 构造方法参数：院系、电话
        this.readerNo = readerNo; // 赋值学号/工号
        this.password = password; // 赋值已加密密码（调用方负责加密）
        this.name = name; // 赋值姓名
        this.type = type; // 赋值读者类型
        this.department = department; // 赋值院系
        this.phone = phone; // 赋值电话
        this.currentBorrowCount = 0; // 新读者初始借阅数为 0
        this.createTime = LocalDateTime.now(); // 注册时间在构造时写入
    }

    // ===== Getter / Setter =====
    // JPA 通过反射调用 Setter 完成实体属性注入，通过 Getter 读取字段值
    public Long getId() { return id; } // 主键 ID 的 getter，供查询获取标识
    public void setId(Long id) { this.id = id; } // 主键 ID 的 setter，通常由 JPA 自动填充
    public String getReaderNo() { return readerNo; } // 学号/工号的 getter，供登录校验与展示
    public void setReaderNo(String readerNo) { this.readerNo = readerNo; } // 学号/工号的 setter，供注册或修改时赋值
    public String getPassword() { return password; } // 密码的 getter，供 AuthService 进行 BCrypt 比对
    public void setPassword(String password) { this.password = password; } // 密码的 setter，赋值需保证传入的已是密文
    public String getName() { return name; } // 姓名的 getter，供展示与检索
    public void setName(String name) { this.name = name; } // 姓名的 setter，供修改个人信息时赋值
    public ReaderType getType() { return type; } // 读者类型的 getter，供业务层判断借阅规则
    public void setType(ReaderType type) { this.type = type; } // 读者类型的 setter，供类型调整时赋值
    public String getDepartment() { return department; } // 院系的 getter，供统计与展示
    public void setDepartment(String department) { this.department = department; } // 院系的 setter，供修改信息时赋值
    public String getPhone() { return phone; } // 电话的 getter，供催还通知
    public void setPhone(String phone) { this.phone = phone; } // 电话的 setter，供修改联系方式时赋值
    public Integer getCurrentBorrowCount() { return currentBorrowCount; } // 当前借阅数的 getter，供判断是否达到借阅上限
    public void setCurrentBorrowCount(Integer currentBorrowCount) { this.currentBorrowCount = currentBorrowCount; } // 当前借阅数的 setter，借出归还时由业务层维护
    public LocalDateTime getLastLoginAt() { return lastLoginAt; } // 最近登录时间的 getter，供安全审计展示
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; } // 最近登录时间的 setter，登录成功时由 AuthService 调用
    public LocalDateTime getCreateTime() { return createTime; } // 注册时间的 getter，供审计与排序
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; } // 注册时间的 setter，仅供持久化层或初始化使用
}

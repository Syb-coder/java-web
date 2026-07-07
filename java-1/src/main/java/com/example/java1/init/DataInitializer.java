// 声明包路径，归类为 init 初始化层，存放应用启动时的数据初始化器
package com.example.java1.init;

// 导入管理员用户实体类，对应数据库 admin_users 表
import com.example.java1.model.AdminUser;
// 导入图书实体类，对应数据库 books 表
import com.example.java1.model.Book;
// 导入借阅记录实体类，对应数据库 borrow_records 表
import com.example.java1.model.BorrowRecord;
// 导入借阅状态枚举（BORROWED / RETURNED / OVERDUE）
import com.example.java1.model.BorrowStatus;
// 导入读者实体类，对应数据库 readers 表
import com.example.java1.model.Reader;
// 导入读者类型枚举（STUDENT / TEACHER），决定借阅限额与借期
import com.example.java1.model.ReaderType;
// 导入管理员仓储，用于持久化管理员账号
import com.example.java1.repository.AdminUserRepository;
// 导入图书仓储，用于持久化图书数据
import com.example.java1.repository.BookRepository;
// 导入借阅记录仓储，用于持久化借阅记录
import com.example.java1.repository.BorrowRecordRepository;
// 导入读者仓储，用于持久化读者数据
import com.example.java1.repository.ReaderRepository;
// 导入认证服务，用于密码加密（BCrypt）
import com.example.java1.service.AuthService;
// 导入读者服务，用于读者密码加密
import com.example.java1.service.ReaderService;
// 导入 CommandLineRunner 接口，应用启动后执行一次 run 方法
import org.springframework.boot.CommandLineRunner;
// 导入 @Component 注解，标记为 Spring 组件以便被容器扫描
import org.springframework.stereotype.Component;

// 导入日期类（不含时分秒），用于借出/应还/归还日期
import java.time.LocalDate;
// 导入日期时间类（含时分秒），用于记录创建时间
import java.time.LocalDateTime;

/**
 * 数据初始化器
 * <p>
 * 应用启动时若数据库为空，则插入示例管理员、读者、图书与借阅记录，
 * 便于首次启动即可看到完整功能演示。设计为幂等：仅当核心表为空时才执行插入。
 * </p>
 */
@Component // 声明为 Spring 组件，由 IoC 容器扫描并管理，启动后自动调用 run 方法
public class DataInitializer implements CommandLineRunner { // 实现 CommandLineRunner 接口，run 方法在 Spring 上下文初始化完成后执行

    private final AdminUserRepository adminRepo; // 管理员仓储，final 保证依赖不可变
    private final ReaderRepository readerRepo; // 读者仓储
    private final BookRepository bookRepo; // 图书仓储
    private final BorrowRecordRepository recordRepo; // 借阅记录仓储
    private final AuthService authService; // 认证服务，用于 BCrypt 密码加密
    private final ReaderService readerService; // 读者服务，用于读者密码加密

    /**
     * 构造方法注入
     */
    // 构造方法注入优于字段注入，便于单元测试 mock 依赖，且 final 字段只能在构造方法中赋值
    public DataInitializer(AdminUserRepository adminRepo, ReaderRepository readerRepo,
                           BookRepository bookRepo, BorrowRecordRepository recordRepo,
                           AuthService authService, ReaderService readerService) {
        this.adminRepo = adminRepo; // 赋值管理员仓储
        this.readerRepo = readerRepo; // 赋值读者仓储
        this.bookRepo = bookRepo; // 赋值图书仓储
        this.recordRepo = recordRepo; // 赋值借阅记录仓储
        this.authService = authService; // 赋值认证服务
        this.readerService = readerService; // 赋值读者服务
    }

    @Override // 标记覆盖父接口 CommandLineRunner 的 run 方法
    public void run(String... args) { // 应用启动后自动执行，args 为命令行参数本系统未使用
        // 幂等判断：管理员表已有数据则跳过，避免重复启动重复插入污染数据
        if (adminRepo.count() > 0) {
            return; // 已有数据则直接返回，保证多次启动结果一致
        }

        // ===== 初始化管理员（密码 BCrypt 加密）=====
        // 默认账号 admin/admin123，密码经 BCrypt 加密存储，避免明文密码泄露风险
        AdminUser admin = new AdminUser("admin", authService.encodePassword("admin123"), "图书管理员");
        admin.setCreateTime(LocalDateTime.now()); // 记录创建时间，用于审计追踪
        adminRepo.save(admin); // 持久化管理员账号

        // ===== 初始化示例读者 =====
        // 读者密码统一为 123456（BCrypt 加密），演示环境简化便于登录测试
        String readerPwd = readerService.encodePassword("123456");
        // 2 名学生读者（S 开头学号），借阅限额 5 本、借期 30 天
        Reader r1 = createReader("S2024001", readerPwd, "张三", ReaderType.STUDENT, "计算机学院", "13800001111");
        Reader r2 = createReader("S2024002", readerPwd, "李四", ReaderType.STUDENT, "数学学院", "13800002222");
        // 2 名教师读者（T 开头工号），借阅限额 10 本、借期 60 天
        Reader r3 = createReader("T2021001", readerPwd, "王教授", ReaderType.TEACHER, "计算机学院", "13800003333");
        Reader r4 = createReader("T2018002", readerPwd, "刘教授", ReaderType.TEACHER, "物理学院", "13800004444");

        // ===== 初始化示例图书 =====
        // 图书按学科分类（计算机/数学/物理/历史/文学），覆盖校园常见馆藏类型
        // b1: Java 入门教材，5 副本供多学生同时借阅
        Book b1 = createBook("Java核心技术 卷I", "Cay S. Horstmann", "978-7-111-56789-0", "计算机/TP3",
                "机械工业出版社", 2020, "Java入门经典，涵盖基础语法与核心API", 5, "A区1排");
        // b2: JVM 进阶书，副本较少（3 本）因受众为中高级读者
        Book b2 = createBook("深入理解Java虚拟机", "周志明", "978-7-111-42190-5", "计算机/TP3",
                "机械工业出版社", 2020, "JVM原理深度解析，中高级Java必读", 3, "A区1排");
        Book b3 = createBook("数据结构与算法分析", "Mark Allen Weiss", "978-7-111-52839-7", "计算机/TP3",
                "机械工业出版社", 2019, "经典数据结构教材，C语言描述", 4, "A区2排");
        // b4: 高等数学为公共基础课教材，副本数最多（10 本）满足大班教学需求
        Book b4 = createBook("高等数学 第七版", "同济大学数学系", "978-7-04-039663-8", "数学/O13",
                "高等教育出版社", 2014, "高校通用高等数学教材", 10, "B区1排");
        Book b5 = createBook("线性代数", "同济大学数学系", "978-7-04-027738-0", "数学/O15",
                "高等教育出版社", 2014, "线性代数经典教材", 8, "B区1排");
        Book b6 = createBook("大学物理学", "张三慧", "978-7-302-12345-6", "物理/O4",
                "清华大学出版社", 2018, "工科物理通用教材", 6, "C区1排");
        Book b7 = createBook("中国近代史", "李侃", "978-7-109-12345-7", "历史/K25",
                "中华书局", 2020, "近代史纲要参考教材", 5, "D区1排");
        // b8: 红楼梦为文学经典，副本较多（7 本）满足课外阅读需求
        Book b8 = createBook("红楼梦", "曹雪芹", "978-7-02-00022-0", "文学/I242",
                "人民文学出版社", 2008, "中国古典四大名著之一", 7, "E区1排");
        Book b9 = createBook("Spring Boot实战", "Craig Walls", "978-7-115-42345-1", "计算机/TP3",
                "人民邮电出版社", 2018, "Spring Boot入门与进阶", 4, "A区2排");
        Book b10 = createBook("数据库系统概论", "王珊", "978-7-04-05123-3", "计算机/TP3",
                "高等教育出版社", 2014, "数据库经典教材", 5, "A区2排");

        // ===== 初始化示例借阅记录（覆盖各状态）=====
        // 正常借出中：b1 由张三借出 5 天，应还 25 天后，演示 BORROWED 状态
        createBorrowRecord(b1, r1, LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(25), null, BorrowStatus.BORROWED, 0, 0.0, "课程参考书");
        // 已归还（正常）：b4 由张三借出 40 天，提前 2 天归还，演示 RETURNED 状态
        createBorrowRecord(b4, r1, LocalDate.now().minusDays(40),
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(12),
                BorrowStatus.RETURNED, 0, 0.0, null);
        // 借出中即将到期：b2 由李四借出 28 天，2 天后到期，演示临期提醒场景
        createBorrowRecord(b2, r2, LocalDate.now().minusDays(28),
                LocalDate.now().plusDays(2), null, BorrowStatus.BORROWED, 0, 0.0, null);
        // 已归还但超期（罚款）：b3 由李四超期 5 天归还，罚款 2.5 元，演示 OVERDUE 状态与罚款逻辑
        createBorrowRecord(b3, r2, LocalDate.now().minusDays(60),
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(25),
                BorrowStatus.OVERDUE, 0, 2.5, "超期 5 天");
        // 教师借阅（长借期）：b6 由王教授借出 20 天，应还 40 天后，演示教师 60 天借期优势
        createBorrowRecord(b6, r3, LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(40), null, BorrowStatus.BORROWED, 0, 0.0, "教学参考");
        // 教师续借后借阅中：b9 由王教授借出 50 天，续借 1 次延至 25 天后，演示续借功能
        createBorrowRecord(b9, r3, LocalDate.now().minusDays(50),
                LocalDate.now().plusDays(25), null, BorrowStatus.BORROWED, 1, 0.0, "续借一次");
        // 已归还正常：b8 由刘教授借出 15 天提前归还，演示教师正常借还流程
        createBorrowRecord(b8, r4, LocalDate.now().minusDays(15),
                LocalDate.now().plusDays(45), LocalDate.now().minusDays(5),
                BorrowStatus.RETURNED, 0, 0.0, null);

        // 更新读者当前借阅计数，与借阅记录保持一致，避免借书时限额校验失效
        updateReaderBorrowCount(r1, 1); // b1 借出中
        updateReaderBorrowCount(r2, 1); // b2 借出中
        updateReaderBorrowCount(r3, 2); // b6, b9 借出中
        updateReaderBorrowCount(r4, 0); // 全部归还

        System.out.println("[DataInitializer] 示例数据初始化完成：1 管理员、4 读者、10 图书、7 借阅记录");
    }

    /**
     * 创建并保存读者
     */
    private Reader createReader(String readerNo, String encodedPwd, String name, ReaderType type,
                                String department, String phone) {
        Reader r = new Reader(readerNo, encodedPwd, name, type, department, phone); // 构造读者实体，密码已是 BCrypt 密文
        r.setCreateTime(LocalDateTime.now()); // 记录创建时间
        return readerRepo.save(r); // 持久化并返回带主键的读者实体
    }

    /**
     * 创建并保存图书
     */
    private Book createBook(String title, String author, String isbn, String category,
                            String publisher, int publishYear, String description,
                            int totalCopies, String location) {
        // 通过构造方法设置必填字段（书名、作者、分类、总副本数），可借副本数默认等于总副本数
        Book b = new Book(title, author, category, totalCopies);
        b.setIsbn(isbn); // 设置 ISBN 编号，用于图书唯一标识
        b.setPublisher(publisher); // 设置出版社
        b.setPublishYear(publishYear); // 设置出版年份
        b.setDescription(description); // 设置简介，便于读者了解图书内容
        b.setLocation(location); // 设置存放位置，便于线下找书
        b.setCreateTime(LocalDateTime.now()); // 记录创建时间
        return bookRepo.save(b); // 持久化并返回带主键的图书实体
    }

    /**
     * 创建并保存借阅记录
     */
    private void createBorrowRecord(Book book, Reader reader, LocalDate borrowDate,
                                    LocalDate dueDate, LocalDate returnDate,
                                    BorrowStatus status, int renewCount, double fine, String remark) {
        BorrowRecord record = new BorrowRecord(); // 创建借阅记录实体
        record.setBook(book); // 关联图书，建立外键关系
        record.setReader(reader); // 关联读者，建立外键关系
        record.setBorrowDate(borrowDate); // 记录借出日期
        record.setDueDate(dueDate); // 记录应还日期，用于逾期判定
        record.setReturnDate(returnDate); // 记录实际归还日期，未归还时为 null
        record.setStatus(status); // 设置借阅状态
        record.setRenewCount(renewCount); // 记录续借次数，限制最多续借 1 次
        record.setFine(fine); // 记录罚款金额，超期按 0.5 元/天计算
        record.setRemark(remark); // 记录备注信息
        recordRepo.save(record); // 持久化借阅记录

        // 调整图书可借数量：借出减 1，归还加 1
        // 初始化时需同步库存与借阅状态，保证首页统计的库存数据准确
        if (status == BorrowStatus.BORROWED || status == BorrowStatus.OVERDUE) {
            // 借出状态（OVERDUE 表示归还时已超期，库存已恢复，这里只处理 BORROWED）
            if (status == BorrowStatus.BORROWED) {
                book.setAvailableCopies(book.getAvailableCopies() - 1); // 借出时扣减可借副本，防止超借
                bookRepo.save(book); // 持久化库存变更
            } else {
                // OVERDUE 状态：归还时已超期，库存已恢复（+1），但状态标记为逾期
                // 此处不调整库存
            }
        }
        // RETURNED 状态：归还时已恢复库存，初始化时不重复调整
    }

    /**
     * 更新读者当前借阅计数
     */
    private void updateReaderBorrowCount(Reader reader, int count) {
        reader.setCurrentBorrowCount(count); // 设置当前借阅数，借书时校验该值是否达限额
        readerRepo.save(reader); // 持久化读者借阅计数
    }
}

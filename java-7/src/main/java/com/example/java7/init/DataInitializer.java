package com.example.java7.init; // 声明当前类所在的包路径，归类为 init 初始化模块

import com.example.java7.model.Book; // 引入图书实体类
import com.example.java7.model.BookCategory; // 引入图书分类枚举
import com.example.java7.model.BorrowRecord; // 引入借阅记录实体类
import com.example.java7.model.BorrowStatus; // 引入借阅状态枚举
import com.example.java7.repository.BookRepository; // 引入图书 JPA 仓储
import com.example.java7.repository.BorrowRecordRepository; // 引入借阅记录 JPA 仓储
import org.springframework.boot.CommandLineRunner; // 引入启动后回调接口，应用启动完成后执行 run 方法
import org.springframework.stereotype.Component; // 引入 @Component 注解，标记为 Spring 组件

import java.time.LocalDate; // 引入日期类，用于借出/应还/归还日期
import java.time.LocalDateTime; // 引入日期时间类，用于记录图书入库时间

/**
 * 数据初始化器
 * <p>
 * 职责：应用启动时若数据库为空，则插入示例图书与借阅记录，
 * 便于首次启动即可看到完整功能演示。
 * 设计为幂等：仅当 books 表为空时才执行插入，避免重复数据。
 * </p>
 */
@Component // 声明为 Spring 组件，由容器扫描并管理，启动后自动执行 run 方法
public class DataInitializer implements CommandLineRunner { // 实现 CommandLineRunner 接口，应用就绪后执行一次性初始化逻辑

    /** 图书仓储 */
    private final BookRepository bookRepository; // 图书仓储，final 保证不可变

    /** 借阅记录仓储 */
    private final BorrowRecordRepository recordRepository; // 借阅记录仓储，final 保证不可变

    /**
     * 构造方法注入依赖
     *
     * @param bookRepository   图书仓储
     * @param recordRepository 借阅记录仓储
     */
    public DataInitializer(BookRepository bookRepository, // 构造方法注入图书仓储
                           BorrowRecordRepository recordRepository) { // 构造方法注入借阅记录仓储
        this.bookRepository = bookRepository; // 赋值图书仓储
        this.recordRepository = recordRepository; // 赋值借阅记录仓储
    }

    /**
     * 应用启动后执行数据初始化
     *
     * @param args 启动参数
     */
    @Override // 标记为接口方法实现，编译器校验签名一致性
    public void run(String... args) { // 应用启动后回调入口，args 为命令行参数可变数组
        // 幂等判断：已有数据则跳过
        if (bookRepository.count() > 0) { // 检查 books 表记录数，大于 0 表示已有数据
            return; // 已有数据则直接返回，跳过初始化，保证幂等性
        }

        // ===== 初始化示例图书 =====
        Book b1 = createBook("三体", "刘慈欣", "9787536692930", BookCategory.LITERATURE, // 创建第一本图书：科幻文学
                "重庆出版社", 2008, "中国科幻里程碑，讲述地球文明与三体文明的首次接触", // 出版社、出版年、简介
                2, "书架A-1"); // 总副本数与存放位置
        Book b2 = createBook("活着", "余华", "9787506365437", BookCategory.LITERATURE, // 创建第二本图书：当代文学
                "作家出版社", 2012, "以福贵的一生展现中国近现代历史变迁中的人性坚韧",
                1, "书架A-2");
        Book b3 = createBook("深入理解Java虚拟机", "周志明", "9787111421900", // 创建第三本图书：技术书籍
                BookCategory.TECHNOLOGY, "机械工业出版社", 2020,
                "JVM 领域经典著作，全面讲解 Java 虚拟机原理与最佳实践", 2, "书架B-1");
        Book b4 = createBook("Spring Boot实战", "丁雪丰", "9787115417198", // 创建第四本图书：技术书籍
                BookCategory.TECHNOLOGY, "人民邮电出版社", 2016,
                "Spring Boot 入门到实践，涵盖自动配置、数据访问、Web 开发等核心内容",
                1, "书架B-2");
        Book b5 = createBook("人类简史", "尤瓦尔·赫拉利", "9787508647357", // 创建第五本图书：历史类
                BookCategory.HISTORY, "中信出版社", 2014,
                "从认知革命到科学革命，全景展现人类发展历程", 3, "书架C-1");
        Book b6 = createBook("万历十五年", "黄仁宇", "9787101055825", BookCategory.HISTORY, // 创建第六本图书：历史类
                "中华书局", 2007, "以大历史观审视明朝衰落，开创历史写作新范式",
                1, "书架C-2");
        Book b7 = createBook("穷查理宝典", "查理·芒格", "9787508684031", // 创建第七本图书：经济类
                BookCategory.ECONOMICS, "中信出版社", 2019,
                "芒格智慧箴言录，涵盖投资、决策与多元思维模型", 2, "书架D-1");
        Book b8 = createBook("小王子", "圣埃克苏佩里", "9787020042494", BookCategory.CHILDREN, // 创建第八本图书：儿童类
                "人民文学出版社", 2003, "童话经典，以孩子视角探讨爱与责任",
                3, "书架E-1");

        // ===== 初始化示例借阅记录 =====
        // 同步调整图书可借数量，保证数据一致性
        // b1 借出一本（可借从 2 -> 1）
        borrowAndSave(b1, "张三", LocalDate.now().minusDays(10), // 创建 b1 的未归还借阅记录，借出日期为 10 天前
                LocalDate.now().plusDays(4), BorrowStatus.BORROWED, "个人阅读"); // 应还日期为 4 天后，状态为借出中
        // b3 借出一本（可借从 2 -> 1）
        borrowAndSave(b3, "李四", LocalDate.now().minusDays(20), // 创建 b3 的未归还借阅记录，借出日期为 20 天前
                LocalDate.now().minusDays(6), BorrowStatus.BORROWED, "技术学习"); // 应还日期为 6 天前，实际已逾期但仍标记 BORROWED
        // b5 借出一本并已归还（可借从 3 -> 3，借出减1，归还加1）
        borrowAndReturn(b5, "王五", LocalDate.now().minusDays(30), // 创建 b5 的已归还借阅记录，借出日期为 30 天前
                LocalDate.now().minusDays(15), "通识阅读", // 应还日期为 15 天前，备注为通识阅读
                LocalDate.now().minusDays(16)); // 实际归还日期为 16 天前，可借数量不变化
        // b8 借出一本（可借从 3 -> 2）
        borrowAndSave(b8, "赵六", LocalDate.now().minusDays(3), // 创建 b8 的未归还借阅记录，借出日期为 3 天前
                LocalDate.now().plusDays(11), BorrowStatus.BORROWED, "亲子共读"); // 应还日期为 11 天后，状态为借出中

        System.out.println("[DataInitializer] 示例数据初始化完成：8 本图书，4 条借阅记录"); // 控制台输出初始化完成日志，便于排查启动过程
    }

    /**
     * 创建未归还借阅记录，同步减少图书可借数量
     *
     * @param book         关联图书
     * @param borrowerName 借阅人
     * @param borrowDate   借出日期
     * @param dueDate      应还日期
     * @param status       状态（BORROWED 或 OVERDUE）
     * @param remark       备注
     */
    private void borrowAndSave(Book book, String borrowerName, LocalDate borrowDate, // 私有方法：创建未归还借阅记录
                               LocalDate dueDate, BorrowStatus status, String remark) {
        // 减少可借数量并保存图书
        book.setAvailableCopies(book.getAvailableCopies() - 1); // 同步扣减图书可借数量，保证与借阅记录一致
        bookRepository.save(book); // 持久化更新后的图书库存
        // 创建借阅记录
        BorrowRecord record = new BorrowRecord(); // 创建借阅记录实体
        record.setBook(book); // 关联图书
        record.setBorrowerName(borrowerName); // 设置借阅人姓名
        record.setBorrowDate(borrowDate); // 设置借出日期
        record.setDueDate(dueDate); // 设置应还日期
        record.setStatus(status); // 设置借阅状态
        record.setRemark(remark); // 设置备注
        recordRepository.save(record); // 持久化借阅记录
    }

    /**
     * 创建已归还借阅记录（可借数量不变：借出减1，归还加1）
     *
     * @param book         关联图书
     * @param borrowerName 借阅人
     * @param borrowDate   借出日期
     * @param dueDate      应还日期
     * @param remark       备注
     * @param returnDate   实际归还日期
     */
    private void borrowAndReturn(Book book, String borrowerName, LocalDate borrowDate, // 私有方法：创建已归还借阅记录
                                 LocalDate dueDate, String remark, LocalDate returnDate) {
        // 借出与归还抵消，可借数量不变，无需调整
        BorrowRecord record = new BorrowRecord(); // 创建借阅记录实体
        record.setBook(book); // 关联图书
        record.setBorrowerName(borrowerName); // 设置借阅人姓名
        record.setBorrowDate(borrowDate); // 设置借出日期
        record.setDueDate(dueDate); // 设置应还日期
        record.setStatus(BorrowStatus.RETURNED); // 状态直接置为 RETURNED（已归还）
        record.setRemark(remark); // 设置备注
        record.setReturnDate(returnDate); // 设置实际归还日期
        recordRepository.save(record); // 持久化借阅记录
    }

    /**
     * 创建并保存图书
     *
     * @param title           书名
     * @param author          作者
     * @param isbn            ISBN
     * @param category        分类
     * @param publisher       出版社
     * @param publishYear     出版年份
     * @param description     简介
     * @param totalCopies     总数量
     * @param location        存放位置
     * @return 保存后的图书实体
     */
    private Book createBook(String title, String author, String isbn, BookCategory category, // 私有方法：创建并保存图书
                            String publisher, int publishYear, String description,
                            int totalCopies, String location) {
        Book book = new Book(title, author, category, totalCopies, totalCopies); // 调用 Book 构造方法，初始可借数量等于总数量
        book.setIsbn(isbn); // 设置 ISBN
        book.setPublisher(publisher); // 设置出版社
        book.setPublishYear(publishYear); // 设置出版年份
        book.setDescription(description); // 设置简介
        book.setLocation(location); // 设置存放位置
        book.setCreateTime(LocalDateTime.now()); // 记录入库时间
        return bookRepository.save(book); // 持久化并返回带主键的图书实体
    }
}

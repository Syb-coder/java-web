// 声明包路径，存放数据初始化组件
package com.example.java4.init;

// 导入实体类
import com.example.java4.model.AdminUser;
import com.example.java4.model.Book;
import com.example.java4.model.BookCategory;
import com.example.java4.model.Reader;
import com.example.java4.model.ReaderType;

// 导入 Repository
import com.example.java4.repository.AdminUserRepository;
import com.example.java4.repository.BookCategoryRepository;
import com.example.java4.repository.BookRepository;
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 工具
import com.example.java4.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * <p>
 * 应用启动时自动检查并插入种子数据，包括管理员账号、测试读者、图书分类与图书。
 * 使用 CommandLineRunner 保证在 Spring 容器初始化完成后执行。
 * </p>
 * <p>
 * 设计说明：
 * - 通过 existsByXxx 检查数据是否已存在，避免重复插入；
 * - 密码使用 BCrypt 加密后存储，不存明文；
 * - 种子数据仅在数据库为空时插入，已有数据不会覆盖。
 * </p>
 */
@Component // 声明为 Spring 组件，由容器管理
public class DataInitializer implements CommandLineRunner {

    /** 管理员仓储 */
    private final AdminUserRepository adminUserRepository;

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /** 分类仓储 */
    private final BookCategoryRepository categoryRepository;

    /** 图书仓储 */
    private final BookRepository bookRepository;

    /** 认证服务（用于密码加密） */
    private final AuthService authService;

    /**
     * 构造方法注入依赖
     */
    public DataInitializer(AdminUserRepository adminUserRepository, ReaderRepository readerRepository,
                           BookCategoryRepository categoryRepository, BookRepository bookRepository,
                           AuthService authService) {
        this.adminUserRepository = adminUserRepository;
        this.readerRepository = readerRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        // 初始化管理员账号
        initAdminUser();
        // 初始化测试读者
        initReaders();
        // 初始化图书分类
        initCategories();
        // 初始化图书数据
        initBooks();
    }

    /**
     * 初始化管理员账号
     * <p>默认账号：admin / admin123</p>
     */
    private void initAdminUser() {
        if (!adminUserRepository.existsByUsername("admin")) {
            AdminUser admin = new AdminUser(
                    "admin",
                    authService.encodePassword("admin123"),
                    "系统管理员"
            );
            adminUserRepository.save(admin);
            System.out.println("[DataInitializer] 管理员账号已初始化: admin / admin123");
        }
    }

    /**
     * 初始化测试读者
     * <p>学生：S001 / student123，教师：T001 / teacher123</p>
     */
    private void initReaders() {
        // 学生读者
        if (!readerRepository.existsByReaderNo("S001")) {
            Reader student = new Reader(
                    "S001",
                    authService.encodePassword("student123"),
                    "张三",
                    ReaderType.STUDENT,
                    "计算机学院",
                    "13800138001"
            );
            readerRepository.save(student);
            System.out.println("[DataInitializer] 学生读者已初始化: S001 / student123");
        }
        // 教师读者
        if (!readerRepository.existsByReaderNo("T001")) {
            Reader teacher = new Reader(
                    "T001",
                    authService.encodePassword("teacher123"),
                    "李教授",
                    ReaderType.TEACHER,
                    "计算机学院",
                    "13800138002"
            );
            readerRepository.save(teacher);
            System.out.println("[DataInitializer] 教师读者已初始化: T001 / teacher123");
        }
    }

    /**
     * 初始化图书分类
     */
    private void initCategories() {
        if (categoryRepository.count() > 0) {
            return; // 已有分类数据，跳过
        }
        // 创建 5 个图书分类
        categoryRepository.save(new BookCategory("计算机科学", "计算机相关图书", 1));
        categoryRepository.save(new BookCategory("文学", "中外文学作品", 2));
        categoryRepository.save(new BookCategory("历史", "历史与人文类图书", 3));
        categoryRepository.save(new BookCategory("哲学", "哲学与思想类图书", 4));
        categoryRepository.save(new BookCategory("经济学", "经济与金融类图书", 5));
        System.out.println("[DataInitializer] 图书分类已初始化: 5 个分类");
    }

    /**
     * 初始化图书数据
     */
    private void initBooks() {
        if (bookRepository.count() > 0) {
            return; // 已有图书数据，跳过
        }

        // 获取分类
        BookCategory csCategory = categoryRepository.findByName("计算机科学").orElse(null);
        BookCategory litCategory = categoryRepository.findByName("文学").orElse(null);
        BookCategory hisCategory = categoryRepository.findByName("历史").orElse(null);
        BookCategory phiCategory = categoryRepository.findByName("哲学").orElse(null);
        BookCategory ecoCategory = categoryRepository.findByName("经济学").orElse(null);

        // ===== 计算机科学类图书（封面 book_01 ~ book_04） =====
        if (csCategory != null) {
            createBook("Java核心技术 卷I", "凯 S. 霍斯特曼", "978-7-111-56230-5",
                    csCategory, "机械工业出版社", 2020,
                    "Java领域最有影响力的技术著作之一，全面覆盖Java SE的核心API。",
                    5, "A区1排", "/images/covers/book_01.png");
            createBook("深入理解Java虚拟机", "周志明", "978-7-111-42190-0",
                    csCategory, "机械工业出版社", 2019,
                    "全面讲解Java虚拟机原理，JVM必读经典。",
                    3, "A区1排", "/images/covers/book_02.png");
            createBook("Spring Boot实战", "克雷格 沃斯", "978-7-115-43218-0",
                    csCategory, "人民邮电出版社", 2021,
                    "Spring Boot入门到精通，涵盖微服务开发实践。",
                    4, "A区2排", "/images/covers/book_03.png");
            createBook("数据结构与算法分析", "马克 艾伦 韦斯", "978-7-111-40126-1",
                    csCategory, "机械工业出版社", 2018,
                    "经典数据结构教材，用Java语言描述算法实现。",
                    3, "A区2排", "/images/covers/book_04.png");
        }

        // ===== 文学类图书（封面 book_05 ~ book_07） =====
        if (litCategory != null) {
            createBook("红楼梦", "曹雪芹", "978-7-020-00220-4",
                    litCategory, "人民文学出版社", 2017,
                    "中国古典四大名著之一，封建社会的百科全书。",
                    6, "B区1排", "/images/covers/book_05.png");
            createBook("百年孤独", "加西亚 马尔克斯", "978-7-544-25399-4",
                    litCategory, "南海出版公司", 2017,
                    "魔幻现实主义文学代表作，讲述布恩迪亚家族七代人的故事。",
                    4, "B区1排", "/images/covers/book_06.png");
            createBook("活着", "余华", "978-7-506-36587-0",
                    litCategory, "作家出版社", 2012,
                    "当代文学经典，讲述一个人历尽世间苦难的故事。",
                    5, "B区2排", "/images/covers/book_07.png");
        }

        // ===== 历史类图书（封面 book_08 ~ book_10） =====
        if (hisCategory != null) {
            createBook("史记", "司马迁", "978-7-101-00304-1",
                    hisCategory, "中华书局", 2006,
                    "中国第一部纪传体通史，二十四史之首。",
                    3, "C区1排", "/images/covers/book_08.png");
            createBook("全球通史", "斯塔夫里阿诺斯", "978-7-301-09480-6",
                    hisCategory, "北京大学出版社", 2006,
                    "以全球视角讲述人类历史发展进程的经典著作。",
                    4, "C区1排", "/images/covers/book_09.png");
            createBook("明朝那些事儿", "当年明月", "978-7-802-11220-5",
                    hisCategory, "中国海关出版社", 2009,
                    "用通俗语言讲述明朝三百年历史的畅销读物。",
                    5, "C区2排", "/images/covers/book_10.png");
        }

        // ===== 哲学类图书（封面 book_11 ~ book_12） =====
        if (phiCategory != null) {
            createBook("中国哲学简史", "冯友兰", "978-7-301-09359-5",
                    phiCategory, "北京大学出版社", 2013,
                    "用英文向西方介绍中国哲学的经典著作，中文译本广为流传。",
                    3, "D区1排", "/images/covers/book_11.png");
            createBook("西方哲学史", "伯特兰 罗素", "978-7-100-02090-1",
                    phiCategory, "商务印书馆", 2015,
                    "诺贝尔文学奖得主罗素撰写的西方哲学通史。",
                    2, "D区1排", "/images/covers/book_12.png");
        }

        // ===== 经济学类图书（封面 book_13 ~ book_15） =====
        if (ecoCategory != null) {
            createBook("经济学原理", "曼昆", "978-7-301-21540-0",
                    ecoCategory, "北京大学出版社", 2015,
                    "哈佛大学经济学教授曼昆的代表作，经济学入门必读。",
                    4, "E区1排", "/images/covers/book_13.png");
            createBook("国富论", "亚当 斯密", "978-7-544-29462-3",
                    ecoCategory, "上海三联书店", 2011,
                    "现代经济学奠基之作，市场经济理论的源头。",
                    3, "E区1排", "/images/covers/book_14.png");
            createBook("穷查理宝典", "查理 芒格", "978-7-508-68420-8",
                    ecoCategory, "中信出版社", 2019,
                    "巴菲特黄金搭档查理芒格的智慧箴言录。",
                    4, "E区2排", "/images/covers/book_15.png");
        }

        System.out.println("[DataInitializer] 图书数据已初始化");
    }

    /**
     * 创建图书的辅助方法
     *
     * @param title       书名
     * @param author      作者
     * @param isbn        ISBN
     * @param category    分类
     * @param publisher   出版社
     * @param publishYear 出版年份
     * @param description 简介
     * @param totalCopies 总副本数
     * @param location    存放位置
     * @param coverImage  封面图片路径（如 /images/covers/book_01.png）
     */
    private void createBook(String title, String author, String isbn, BookCategory category,
                            String publisher, int publishYear, String description,
                            int totalCopies, String location, String coverImage) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setPublishYear(publishYear);
        book.setDescription(description);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies); // 新书全部可借
        book.setLocation(location);
        book.setCoverImage(coverImage); // 设置封面图片路径，前端 renderCover 据此渲染
        book.setCreateTime(LocalDateTime.now());
        bookRepository.save(book);
    }
}

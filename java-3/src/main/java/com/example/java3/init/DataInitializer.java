package com.example.java3.init;

import com.example.java3.model.*;
import com.example.java3.repository.*;
import com.example.java3.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * <p>
 * 应用启动后自动注入种子数据：管理员、分类、学生、商品、公告。
 * 仅在各表为空时执行，避免重复插入。
 * </p>
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AnnouncementRepository announcementRepository;
    private final AuthService authService;

    public DataInitializer(AdminUserRepository adminUserRepository,
                           UserRepository userRepository,
                           ProductCategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           AnnouncementRepository announcementRepository,
                           AuthService authService) {
        this.adminUserRepository = adminUserRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.announcementRepository = announcementRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = authService.getPasswordEncoder();

        // 1. 初始化管理员
        if (adminUserRepository.count() == 0) {
            adminUserRepository.save(new AdminUser(
                    "admin", encoder.encode("admin123"), "平台管理员"));
            System.out.println("[DataInit] 已初始化管理员：admin / admin123");
        }

        // 2. 初始化商品分类
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new ProductCategory("教材书籍", "📚", 1));
            categoryRepository.save(new ProductCategory("数码设备", "💻", 2));
            categoryRepository.save(new ProductCategory("宿舍家具", "🛏️", 3));
            categoryRepository.save(new ProductCategory("运动器材", "⚽", 4));
            categoryRepository.save(new ProductCategory("服装鞋帽", "👕", 5));
            categoryRepository.save(new ProductCategory("生活用品", "🧴", 6));
            System.out.println("[DataInit] 已初始化 6 个商品分类");
        }

        // 3. 初始化学生用户
        if (userRepository.count() == 0) {
            User u1 = new User("20210101001", "zhangsan", encoder.encode("123456"), "张三");
            u1.setPhone("13800000001");
            User u2 = new User("20210101002", "lisi", encoder.encode("123456"), "李四");
            u2.setPhone("13800000002");
            User u3 = new User("20210101003", "wangwu", encoder.encode("123456"), "王五");
            u3.setPhone("13800000003");
            userRepository.save(u1);
            userRepository.save(u2);
            userRepository.save(u3);
            System.out.println("[DataInit] 已初始化 3 个学生用户（密码均为 123456）");
        }

        // 4. 初始化商品（需绑定卖家与分类）
        if (productRepository.count() == 0) {
            // 取出已初始化的用户和分类
            User zhangsan = userRepository.findByUsername("zhangsan").orElse(null);
            User lisi = userRepository.findByUsername("lisi").orElse(null);
            User wangwu = userRepository.findByUsername("wangwu").orElse(null);
            ProductCategory bookCat = categoryRepository.findByName("教材书籍").orElse(null);
            ProductCategory digitalCat = categoryRepository.findByName("数码设备").orElse(null);
            ProductCategory furnitureCat = categoryRepository.findByName("宿舍家具").orElse(null);
            ProductCategory sportCat = categoryRepository.findByName("运动器材").orElse(null);

            if (zhangsan != null && bookCat != null) {
                Product p1 = new Product(
                        "高等数学第七版 教材", "同济大学高等数学第七版，9成新，无笔记",
                        15.0, 49.8, "", "9成新", bookCat.getId(), zhangsan.getId());
                p1.setAuditStatus(ProductAuditStatus.APPROVED);
                productRepository.save(p1);

                Product p2 = new Product(
                        "大学英语精读 第三册", "配套课文，附光盘，几乎全新",
                        10.0, 35.0, "", "几乎全新", bookCat.getId(), zhangsan.getId());
                p2.setAuditStatus(ProductAuditStatus.APPROVED);
                productRepository.save(p2);
            }

            if (lisi != null && digitalCat != null) {
                Product p3 = new Product(
                        "罗技 MX Master 3 鼠标", "使用半年，外观无划痕，原盒配件齐全",
                        280.0, 699.0, "", "8成新", digitalCat.getId(), lisi.getId());
                p3.setAuditStatus(ProductAuditStatus.APPROVED);
                productRepository.save(p3);

                Product p4 = new Product(
                        "小米充电宝 10000mAh", " Type-C 双向快充，9成新",
                        45.0, 99.0, "", "9成新", digitalCat.getId(), lisi.getId());
                p4.setAuditStatus(ProductAuditStatus.APPROVED);
                productRepository.save(p4);
            }

            if (wangwu != null) {
                if (furnitureCat != null) {
                    Product p5 = new Product(
                            "折叠床 宿舍午休", "毕业转让，承重好，自提",
                            50.0, 159.0, "", "7成新", furnitureCat.getId(), wangwu.getId());
                    p5.setAuditStatus(ProductAuditStatus.APPROVED);
                    productRepository.save(p5);
                }
                if (sportCat != null) {
                    Product p6 = new Product(
                            "羽毛球拍 尤尼克斯", "使用次数少，线刚换",
                            120.0, 280.0, "", "9成新", sportCat.getId(), wangwu.getId());
                    p6.setAuditStatus(ProductAuditStatus.APPROVED);
                    productRepository.save(p6);
                }
                // 一条待审核商品，用于演示审核流程
                if (bookCat != null) {
                    Product p7 = new Product(
                            "线性代数 同济版", "课程教材，笔记较少",
                            8.0, 32.0, "", "8成新", bookCat.getId(), wangwu.getId());
                    p7.setAuditStatus(ProductAuditStatus.PENDING);
                    productRepository.save(p7);
                }
            }
            System.out.println("[DataInit] 已初始化 7 件商品（6 已通过，1 待审核）");
        }

        // 5. 初始化公告
        if (announcementRepository.count() == 0) {
            AdminUser admin = adminUserRepository.findByUsername("admin").orElse(null);
            if (admin != null) {
                announcementRepository.save(new Announcement(
                        "欢迎使用校园闲置交易平台",
                        "本平台仅面向本校学生开放，请使用学号实名注册。交易请选择线下自提，注意人身安全。",
                        true, admin.getId()));
                announcementRepository.save(new Announcement(
                        "毕业季闲置集市活动通知",
                        "本月将举办线下闲置集市，地点：学生活动中心广场，欢迎同学们踊跃参与。",
                        false, admin.getId()));
                System.out.println("[DataInit] 已初始化 2 条公告");
            }
        }
    }
}

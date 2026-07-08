// 声明当前类所在的包路径
package com.example.java3.init;

// 导入 model 包下所有实体类（AdminUser、User、Product、ProductCategory、Announcement 等）
import com.example.java3.model.*;
// 导入 repository 包下所有 JPA 仓储接口
import com.example.java3.repository.*;
// 导入 AuthService，用于获取 BCrypt 密码编码器
import com.example.java3.service.AuthService;
// 导入 CommandLineRunner，应用启动后执行其 run 方法完成数据初始化
import org.springframework.boot.CommandLineRunner;
// 导入 @Order，指定多个 CommandLineRunner 之间的执行顺序（数值越小越先执行）
import org.springframework.core.annotation.Order;
// 导入 BCryptPasswordEncoder，用于密码加密存储
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// 导入 @Component，将初始化器注册为 Spring Bean
import org.springframework.stereotype.Component;

// 导入 LocalDateTime，用于时间字段（虽然本类未直接使用，保留以备扩展）
import java.time.LocalDateTime;

/**
 * 数据初始化器
 * <p>
 * 应用启动后自动注入种子数据：管理员、分类、学生、商品、公告。
 * 仅在各表为空时执行，避免重复插入。
 * </p>
 */
// @Component：注册为 Spring Bean，容器启动时被发现
@Component
// @Order(1)：当存在多个 CommandLineRunner 时，本初始化器优先执行
@Order(1)
// 实现 CommandLineRunner 接口，容器启动完成后自动调用 run 方法
public class DataInitializer implements CommandLineRunner {

    // 管理员仓储，用于初始化管理员账号
    private final AdminUserRepository adminUserRepository;
    // 学生用户仓储，用于初始化学生账号
    private final UserRepository userRepository;
    // 商品分类仓储，用于初始化商品分类
    private final ProductCategoryRepository categoryRepository;
    // 商品仓储，用于初始化示例商品
    private final ProductRepository productRepository;
    // 公告仓储，用于初始化系统公告
    private final AnnouncementRepository announcementRepository;
    // 认证服务，用于获取密码编码器
    private final AuthService authService;

    // 构造器注入：Spring 自动注入所有仓储与服务依赖
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

    // 重写 run 方法：容器启动完成后自动执行，args 为命令行启动参数
    @Override
    public void run(String... args) {
        // 从 AuthService 获取 BCrypt 编码器，用于密码加密
        BCryptPasswordEncoder encoder = authService.getPasswordEncoder();

        // 1. 初始化管理员
        // 仅当管理员表为空时执行，避免重启时重复插入
        if (adminUserRepository.count() == 0) {
            // 创建管理员：用户名 admin，密码 admin123（BCrypt 加密），显示名"平台管理员"
            adminUserRepository.save(new AdminUser(
                    "admin", encoder.encode("admin123"), "平台管理员"));
            // 控制台输出初始化结果，便于开发调试
            System.out.println("[DataInit] 已初始化管理员：admin / admin123");
        }

        // 2. 初始化商品分类
        // 仅当分类表为空时执行
        if (categoryRepository.count() == 0) {
            // 教材书籍分类，emoji 图标 📚，排序号 1
            categoryRepository.save(new ProductCategory("教材书籍", "📚", 1));
            // 数码设备分类，emoji 图标 💻，排序号 2
            categoryRepository.save(new ProductCategory("数码设备", "💻", 2));
            // 宿舍家具分类，emoji 图标 🛏️，排序号 3
            categoryRepository.save(new ProductCategory("宿舍家具", "🛏️", 3));
            // 运动器材分类，emoji 图标 ⚽，排序号 4
            categoryRepository.save(new ProductCategory("运动器材", "⚽", 4));
            // 服装鞋帽分类，emoji 图标 👕，排序号 5
            categoryRepository.save(new ProductCategory("服装鞋帽", "👕", 5));
            // 生活用品分类，emoji 图标 🧴，排序号 6
            categoryRepository.save(new ProductCategory("生活用品", "🧴", 6));
            // 输出初始化日志
            System.out.println("[DataInit] 已初始化 6 个商品分类");
        }

        // 3. 初始化学生用户
        // 仅当用户表为空时执行
        if (userRepository.count() == 0) {
            // 创建学生 zhangsan，学号 20210101001，密码 123456（BCrypt 加密），姓名张三
            User u1 = new User("20210101001", "zhangsan", encoder.encode("123456"), "张三");
            // 设置联系电话
            u1.setPhone("13800000001");
            // 创建学生 lisi
            User u2 = new User("20210101002", "lisi", encoder.encode("123456"), "李四");
            u2.setPhone("13800000002");
            // 创建学生 wangwu
            User u3 = new User("20210101003", "wangwu", encoder.encode("123456"), "王五");
            u3.setPhone("13800000003");
            // 保存三个学生到数据库
            userRepository.save(u1);
            userRepository.save(u2);
            userRepository.save(u3);
            // 输出初始化日志
            System.out.println("[DataInit] 已初始化 3 个学生用户（密码均为 123456）");
        }

        // 4. 初始化商品（需绑定卖家与分类）
        // 仅当商品表为空时执行
        if (productRepository.count() == 0) {
            // 取出已初始化的用户和分类
            // 通过用户名查询学生 zhangsan，orElse(null) 避免抛异常
            User zhangsan = userRepository.findByUsername("zhangsan").orElse(null);
            // 查询学生 lisi
            User lisi = userRepository.findByUsername("lisi").orElse(null);
            // 查询学生 wangwu
            User wangwu = userRepository.findByUsername("wangwu").orElse(null);
            // 查询"教材书籍"分类
            ProductCategory bookCat = categoryRepository.findByName("教材书籍").orElse(null);
            // 查询"数码设备"分类
            ProductCategory digitalCat = categoryRepository.findByName("数码设备").orElse(null);
            // 查询"宿舍家具"分类
            ProductCategory furnitureCat = categoryRepository.findByName("宿舍家具").orElse(null);
            // 查询"运动器材"分类
            ProductCategory sportCat = categoryRepository.findByName("运动器材").orElse(null);

            // 张三发布两本教材书籍
            if (zhangsan != null && bookCat != null) {
                // 创建商品：高数教材，售价 15，原价 49.8，9成新
                Product p1 = new Product(
                        "高等数学第七版 教材", "同济大学高等数学第七版，9成新，无笔记",
                        15.0, 49.8, "", "9成新", bookCat.getId(), zhangsan.getId());
                // 设置审核状态为已通过，前台可见
                p1.setAuditStatus(ProductAuditStatus.APPROVED);
                // 保存商品
                productRepository.save(p1);

                // 创建商品：大学英语精读第三册
                Product p2 = new Product(
                        "大学英语精读 第三册", "配套课文，附光盘，几乎全新",
                        10.0, 35.0, "", "几乎全新", bookCat.getId(), zhangsan.getId());
                // 设置为已通过
                p2.setAuditStatus(ProductAuditStatus.APPROVED);
                // 保存
                productRepository.save(p2);
            }

            // 李四发布两件数码设备
            if (lisi != null && digitalCat != null) {
                // 罗技鼠标，售价 280，原价 699，8成新
                Product p3 = new Product(
                        "罗技 MX Master 3 鼠标", "使用半年，外观无划痕，原盒配件齐全",
                        280.0, 699.0, "", "8成新", digitalCat.getId(), lisi.getId());
                // 审核通过
                p3.setAuditStatus(ProductAuditStatus.APPROVED);
                // 保存
                productRepository.save(p3);

                // 小米充电宝
                Product p4 = new Product(
                        "小米充电宝 10000mAh", " Type-C 双向快充，9成新",
                        45.0, 99.0, "", "9成新", digitalCat.getId(), lisi.getId());
                // 审核通过
                p4.setAuditStatus(ProductAuditStatus.APPROVED);
                // 保存
                productRepository.save(p4);
            }

            // 王五发布家具、运动器材及一条待审核商品
            if (wangwu != null) {
                // 折叠床
                if (furnitureCat != null) {
                    Product p5 = new Product(
                            "折叠床 宿舍午休", "毕业转让，承重好，自提",
                            50.0, 159.0, "", "7成新", furnitureCat.getId(), wangwu.getId());
                    // 审核通过
                    p5.setAuditStatus(ProductAuditStatus.APPROVED);
                    productRepository.save(p5);
                }
                // 羽毛球拍
                if (sportCat != null) {
                    Product p6 = new Product(
                            "羽毛球拍 尤尼克斯", "使用次数少，线刚换",
                            120.0, 280.0, "", "9成新", sportCat.getId(), wangwu.getId());
                    // 审核通过
                    p6.setAuditStatus(ProductAuditStatus.APPROVED);
                    productRepository.save(p6);
                }
                // 一条待审核商品，用于演示审核流程
                if (bookCat != null) {
                    Product p7 = new Product(
                            "线性代数 同济版", "课程教材，笔记较少",
                            8.0, 32.0, "", "8成新", bookCat.getId(), wangwu.getId());
                    // 设置为待审核状态，前台不可见，等待管理员审核
                    p7.setAuditStatus(ProductAuditStatus.PENDING);
                    productRepository.save(p7);
                }
            }
            // 输出初始化日志
            System.out.println("[DataInit] 已初始化 7 件商品（6 已通过，1 待审核）");
        }

        // 5. 初始化公告
        // 仅当公告表为空时执行
        if (announcementRepository.count() == 0) {
            // 查询管理员 admin，作为公告发布者
            AdminUser admin = adminUserRepository.findByUsername("admin").orElse(null);
            // 管理员存在时才创建公告
            if (admin != null) {
                // 第一条公告：欢迎语，置顶（true）
                announcementRepository.save(new Announcement(
                        "欢迎使用校园闲置交易平台",
                        "本平台仅面向本校学生开放，请使用学号实名注册。交易请选择线下自提，注意人身安全。",
                        true, admin.getId()));
                // 第二条公告：活动通知，不置顶（false）
                announcementRepository.save(new Announcement(
                        "毕业季闲置集市活动通知",
                        "本月将举办线下闲置集市，地点：学生活动中心广场，欢迎同学们踊跃参与。",
                        false, admin.getId()));
                // 输出初始化日志
                System.out.println("[DataInit] 已初始化 2 条公告");
            }
        }
    }
}

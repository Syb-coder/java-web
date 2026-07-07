// 声明包路径
package com.example.java2.init;

// 导入实体类与仓储
import com.example.java2.model.AdminUser;
import com.example.java2.model.Article;
import com.example.java2.model.Category;
import com.example.java2.model.Question;
import com.example.java2.repository.AdminUserRepository;
import com.example.java2.repository.ArticleRepository;
import com.example.java2.repository.CategoryRepository;
import com.example.java2.repository.QuestionRepository;

// 导入 Spring Boot 初始化钩子
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * <p>
 * 应用启动后自动执行，预置：
 * <ol>
 *   <li>默认管理员账号（admin / admin123）；</li>
 *   <li>4 个网络安全文章分类（Web 安全、移动安全、密码学、社会工程学）；</li>
 *   <li>8 篇科普文章（每分类 2 篇）；</li>
 *   <li>8 道自测题目（每分类 2 道）。</li>
 * </ol>
 * 已存在数据会跳过，保证幂等。
 * </p>
 */
// @Component 将本类注册为 Spring Bean，使其被容器管理并在启动时触发 CommandLineRunner 回调
@Component
// @Order(1) 指定多个 CommandLineRunner 时的执行顺序，数值越小优先级越高；
// 本类设为 1 确保在依赖数据的其他 Runner（如缓存预热）之前完成基础数据初始化
@Order(1)
// CommandLineRunner 的执行时机：Spring Boot 应用上下文初始化完成、所有 Bean 装配就绪后，
// 在 main 方法接收命令行参数前自动调用 run()，此时数据库连接池、JPA 等基础设施已可用
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final QuestionRepository questionRepository;

    public DataInitializer(AdminUserRepository adminRepository,
                           CategoryRepository categoryRepository,
                           ArticleRepository articleRepository,
                           QuestionRepository questionRepository) {
        this.adminRepository = adminRepository;
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    // run 方法在应用启动完成后由 Spring Boot 自动调用，args 为命令行参数（本场景未使用）
    public void run(String... args) {
        // 顺序：先建管理员（后台可登录）→ 再建分类（提供 ID 给文章/题目关联）→ 最后建文章与题目
        initAdmin();
        // 分类初始化后获得 ID，文章与题目按分类 ID 关联
        // sortOrder 1~4 控制前台分类导航的展示顺序，与课程难度递进无关，仅按教学逻辑排序
        Long webCatId = initCategory("Web 安全", "涵盖 SQL 注入、XSS、CSRF 等 Web 应用安全主题", 1);
        Long mobileCatId = initCategory("移动安全", "涵盖 Android/iOS 应用逆向、权限滥用、移动支付安全", 2);
        Long cryptoCatId = initCategory("密码学", "涵盖对称加密、非对称加密、哈希算法、数字签名", 3);
        Long socialCatId = initCategory("社会工程学", "涵盖钓鱼邮件、电话诈骗、心理操纵等人身安全主题", 4);

        // 文章与题目复用同一组分类 ID，确保"学完某分类文章 → 做该分类题目"的学习闭环
        initArticles(webCatId, mobileCatId, cryptoCatId, socialCatId);
        initQuestions(webCatId, mobileCatId, cryptoCatId, socialCatId);
    }

    /**
     * 初始化默认管理员账号（admin / admin123）
     * <p>幂等：已存在则跳过。</p>
     */
    private void initAdmin() {
        // 幂等性检查：按用户名查重，已存在则直接跳过，避免重启时重复插入导致 unique 约束冲突
        // 幂等设计的核心目的：保证应用可重复启动而不报错，CI/CD 与容器重启场景下无需手动清理数据
        if (adminRepository.findByUsername("admin").isPresent()) {
            return;
        }
        // 每次新建 encoder 而非注入 Bean：BCryptPasswordEncoder 无状态且线程安全，局部构造避免污染容器
        // BCryptPasswordEncoder 在初始化中的用途：将明文密码 "admin123" 加盐哈希为不可逆密文存储，
        // BCrypt 每次加密同一明文会生成不同盐值，确保即使两个用户密码相同，密文也不同，抵御彩虹表攻击
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 默认密码 admin123 仅用于首次启动演示，生产环境应由管理员登录后立即修改
        // encoder.encode() 返回格式：$2a$10$<22位盐><31位哈希>，其中 10 为 cost factor（迭代轮数）
        AdminUser admin = new AdminUser(
                "admin",
                encoder.encode("admin123"),
                "超级管理员"
        );
        adminRepository.save(admin);
    }

    /**
     * 初始化单个分类
     *
     * @param name        分类名称
     * @param description 分类描述
     * @param sortOrder   排序序号
     * @return 分类 ID（已存在则返回已有分类的 ID）
     */
    private Long initCategory(String name, String description, int sortOrder) {
        // 幂等性设计：通过名称查重，已存在则直接返回旧 ID，避免重复插入导致 unique 约束冲突
        // 为什么每个初始化方法都要先检查是否已存在：应用可能因容器重启、CI/CD 部署多次启动，
        // 幂等检查确保每次启动都是安全的"无操作"（no-op），不会产生重复数据
        // 采用全表查询 + 内存过滤而非 findByName：分类数量极少（4 个），全表扫描开销可忽略，
        // 且 findAllByOrderBySortOrderAsc 复用于其他场景，减少 Repository 方法爆炸
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                // 已存在则返回旧 ID，保证外键引用稳定；不存在才 save 新分类
                // map 返回已有 ID → orElseGet 执行 save 并取新 ID，形成"有则复用、无则创建"的幂等模式
                .map(Category::getId)
                .orElseGet(() -> categoryRepository.save(
                        new Category(name, description, sortOrder)).getId());
    }

    /**
     * 初始化科普文章
     * <p>按分类各预置 2 篇，正文为 Markdown 风格科普文本。</p>
     */
    private void initArticles(Long webCatId, Long mobileCatId, Long cryptoCatId, Long socialCatId) {
        // 幂等检查：已有文章则跳过整批初始化
        // 幂等设计的考量：初始化数据为整体批次，要么全有要么全无，用 count() > 0 粒度匹配，
        // 避免逐条查重的开销与部分插入导致的数据不一致问题
        if (articleRepository.count() > 0) {
            return;
        }

        // ===== Web 安全 =====
        articleRepository.save(new Article(webCatId,
                "SQL 注入攻击原理与防御",
                "深入理解 SQL 注入的形成原理、常见类型与防御措施。",
                "## 一、什么是 SQL 注入\n\nSQL 注入是指攻击者通过在应用程序的输入参数中注入恶意 SQL 片段，" +
                        "使得后端数据库执行非预期命令的攻击方式。\n\n## 二、常见类型\n\n1. **联合查询注入**：利用 UNION 拼接查询语句。\n" +
                        "2. **盲注**：通过页面返回差异推断数据。\n3. **时间盲注**：通过延迟函数判断条件真假。\n\n" +
                        "## 三、防御措施\n\n- 使用**参数化查询**（PreparedStatement）；\n" +
                        "- 对输入进行**白名单校验**；\n- 最小化数据库账号权限；\n- 部署 WAF 进行流量过滤。"));
        articleRepository.save(new Article(webCatId,
                "跨站脚本攻击（XSS）详解",
                "XSS 是最常见的 Web 漏洞之一，本文介绍其三种类型与防护方案。",
                "## 一、XSS 概述\n\n跨站脚本攻击（XSS）是指攻击者向 Web 页面注入恶意客户端脚本，" +
                        "当其他用户浏览该页面时，脚本在用户浏览器中执行，从而窃取 Cookie 或会话信息。\n\n" +
                        "## 二、三种类型\n\n1. **反射型 XSS**：恶意脚本来自 URL 参数，单次生效。\n" +
                        "2. **存储型 XSS**：恶意脚本存入数据库，持续危害其他用户。\n" +
                        "3. **DOM 型 XSS**：纯前端 DOM 操作触发，不经过服务端。\n\n" +
                        "## 三、防御方案\n\n- 对输出进行 **HTML 实体编码**；\n" +
                        "- 设置 Cookie 的 **HttpOnly** 属性；\n- 使用 **CSP（内容安全策略）** 限制脚本来源。"));

        // ===== 移动安全 =====
        articleRepository.save(new Article(mobileCatId,
                "Android 应用逆向分析入门",
                "介绍 Android 应用的反编译、smali 修改与重打包流程。",
                "## 一、逆向分析概述\n\nAndroid 应用以 APK 形式分发，本质上是 ZIP 压缩包，包含 dex 字节码、资源文件和签名信息。\n\n" +
                        "## 二、常用工具\n\n- **apktool**：反编译资源与 smali；\n- **jadx**：dex 转 Java 源码；\n" +
                        "- **Frida**：动态 Hook 框架。\n\n## 三、防御建议\n\n- 关键逻辑放入 **native 层**（C/C++）；\n" +
                        "- 使用 **代码混淆**（ProGuard/R8）；\n- 加固 APK 防止重打包。"));
        articleRepository.save(new Article(mobileCatId,
                "移动支付安全风险与防护",
                "二维码替换、中间人攻击、键盘记录器是移动支付的主要威胁。",
                "## 一、移动支付威胁\n\n1. **二维码替换**：攻击者篡改商户二维码，资金被劫持。\n" +
                        "2. **中间人攻击**：在公共 WiFi 下截获支付请求。\n" +
                        "3. **键盘记录器**：恶意应用记录用户输入的支付密码。\n\n" +
                        "## 二、防护建议\n\n- 支付前**核对收款方**信息；\n" +
                        "- 避免在公共 WiFi 下进行支付；\n- 安装**安全软件**定期扫描恶意应用；\n" +
                        "- 使用**指纹/人脸**等生物识别替代密码。"));

        // ===== 密码学 =====
        articleRepository.save(new Article(cryptoCatId,
                "对称加密与非对称加密对比",
                "AES 与 RSA 是两种主流加密算法，各有适用场景。",
                "## 一、对称加密\n\n对称加密使用**同一密钥**进行加密与解密，代表算法 AES、DES。\n\n" +
                        "**优点**：速度快，适合大数据量加密。\n**缺点**：密钥分发困难。\n\n" +
                        "## 二、非对称加密\n\n非对称加密使用**公钥加密、私钥解密**，代表算法 RSA、ECC。\n\n" +
                        "**优点**：解决密钥分发问题，支持数字签名。\n**缺点**：计算开销大，速度慢。\n\n" +
                        "## 三、实际应用\n\n- HTTPS 使用 **RSA 协商会话密钥，AES 加密数据**，兼顾安全与性能；\n" +
                        "- 数字签名使用 **私钥签名、公钥验签**。"));
        articleRepository.save(new Article(cryptoCatId,
                "哈希算法与数字签名",
                "MD5、SHA-256 是常用哈希算法，数字签名用于保证数据完整性与不可否认性。",
                "## 一、哈希算法\n\n哈希算法将任意长度输入映射为固定长度输出，具有**不可逆性**与**抗碰撞性**。\n\n" +
                        "- **MD5**：128 位输出，已被发现碰撞，不推荐安全场景使用。\n" +
                        "- **SHA-256**：256 位输出，目前安全。\n\n## 二、数字签名\n\n" +
                        "数字签名流程：\n1. 发送方对原文计算**哈希值**；\n2. 用**私钥**加密哈希值得到签名；\n" +
                        "3. 接收方用**公钥**解密签名，再对原文重新哈希比对。\n\n" +
                        "## 三、应用场景\n\n- 软件发布签名；\n- HTTPS 证书；\n- 区块链交易验证。"));

        // ===== 社会工程学 =====
        articleRepository.save(new Article(socialCatId,
                "识别钓鱼邮件的 5 个关键点",
                "钓鱼邮件是社会工程学攻击最常见的载体，掌握识别技巧至关重要。",
                "## 一、什么是钓鱼邮件\n\n钓鱼邮件伪装成合法机构，诱导用户点击恶意链接或下载附件，从而窃取账号密码或植入木马。\n\n" +
                        "## 二、识别要点\n\n1. **发件人地址异常**：域名拼写错误，如 `ta0bao.com`。\n" +
                        "2. **紧急语气**：声称账号即将冻结，催促立即操作。\n" +
                        "3. **可疑链接**：鼠标悬停查看真实 URL。\n" +
                        "4. **附件类型**：.exe、.scr 等可执行文件高度可疑。\n" +
                        "5. **拼写错误**：正规机构邮件极少出现明显错别字。\n\n" +
                        "## 三、防护建议\n\n- 不点击陌生邮件中的链接；\n" +
                        "- 开启邮箱的**反钓鱼过滤**；\n- 重要账号启用**两步验证**。"));
        articleRepository.save(new Article(socialCatId,
                "电话诈骗的常见套路与应对",
                "冒充公检法、退款诈骗、中奖诈骗是电话诈骗的三大类型。",
                "## 一、常见诈骗套路\n\n1. **冒充公检法**：声称你涉嫌洗钱，要求转入安全账户。\n" +
                        "2. **退款诈骗**：自称客服，称商品有质量问题需退款，诱导提供验证码。\n" +
                        "3. **中奖诈骗**：通知中奖，需先缴税才能领奖。\n\n" +
                        "## 二、应对原则\n\n- **公检法不会通过电话办案**，更不存在安全账户；\n" +
                        "- **验证码绝不告诉他人**；\n- 遇到可疑电话，**挂断后主动回拨官方客服**核实；\n" +
                        "- 安装**国家反诈中心** APP，识别诈骗号码。"));
    }

    /**
     * 初始化题库
     * <p>按分类各预置 2 道题，答案序号 0~3 对应 A/B/C/D。</p>
     */
    private void initQuestions(Long webCatId, Long mobileCatId, Long cryptoCatId, Long socialCatId) {
        // 幂等检查：同 initArticles，已有题目则跳过整批初始化
        // 幂等设计目的：保证应用可重复启动而不产生重复题目，配合 count() > 0 实现批次级幂等
        if (questionRepository.count() > 0) {
            return;
        }

        // ===== Web 安全 =====
        questionRepository.save(new Question(webCatId,
                "下列哪种方式能有效防御 SQL 注入？",
                "拼接 SQL 字符串",
                "使用参数化查询（PreparedStatement）",
                "将密码明文存储",
                "关闭数据库日志",
                1));
        questionRepository.save(new Question(webCatId,
                "存储型 XSS 与反射型 XSS 的主要区别是？",
                "存储型 XSS 恶意脚本存入数据库，持续危害其他用户",
                "存储型 XSS 只能由管理员触发",
                "反射型 XSS 危害更大",
                "二者无区别",
                0));

        // ===== 移动安全 =====
        questionRepository.save(new Question(mobileCatId,
                "下列哪项不属于 Android 应用逆向常用工具？",
                "apktool",
                "jadx",
                "Frida",
                "Photoshop",
                3));
        questionRepository.save(new Question(mobileCatId,
                "移动支付场景下，下列哪种做法最不安全？",
                "在公共 WiFi 下进行支付",
                "使用生物识别替代密码",
                "支付前核对收款方",
                "安装安全软件定期扫描",
                0));

        // ===== 密码学 =====
        questionRepository.save(new Question(cryptoCatId,
                "下列哪种算法属于非对称加密？",
                "AES",
                "DES",
                "RSA",
                "3DES",
                2));
        questionRepository.save(new Question(cryptoCatId,
                "下列关于哈希算法的描述，错误的是？",
                "哈希算法具有不可逆性",
                "哈希算法具有抗碰撞性",
                "MD5 目前仍绝对安全，可用于数字签名",
                "SHA-256 输出 256 位",
                2));

        // ===== 社会工程学 =====
        questionRepository.save(new Question(socialCatId,
                "收到自称公检法的电话，称你涉嫌洗钱需转入安全账户，应当？",
                "立即按要求转账以证清白",
                "提供银行卡号与密码配合调查",
                "挂断电话并报警核实，公检法不会通过电话办案",
                "告知对方身份证号",
                2));
        questionRepository.save(new Question(socialCatId,
                "下列哪项不属于钓鱼邮件的常见特征？",
                "发件人地址域名拼写错误",
                "邮件语气紧急，催促立即操作",
                "附件为可执行文件",
                "邮件正文使用规范的官方模板且无任何可疑链接",
                3));
    }
}

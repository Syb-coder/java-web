package com.example.java5.init; // 声明包路径，归属 init（初始化层）

// ===== 枚举类型导入 =====
import com.example.java5.model.EducationLevel;   // 学历枚举
import com.example.java5.model.ExperienceLevel;  // 经验枚举
import com.example.java5.model.JobCategory;      // 分类枚举
import com.example.java5.model.JobPosting;       // 招聘岗位实体
import com.example.java5.model.JobSource;        // 数据来源枚举

// ===== 仓库与 Spring =====
import com.example.java5.repository.JobPostingRepository; // JPA 仓库
import org.slf4j.Logger;                  // SLF4J 日志接口
import org.slf4j.LoggerFactory;           // 日志工厂
import org.springframework.boot.CommandLineRunner; // 启动后执行钩子
import org.springframework.stereotype.Component;   // 声明为 Spring 组件

// ===== JDK 工具 =====
import java.time.LocalDate; // 日期类型
import java.util.List;      // 列表容器

/**
 * 项目启动时插入本地种子岗位数据
 * <p>
 * 职责：应用启动后检测数据库是否为空，若为空则注入 13 条示例岗位，
 * 便于首次启动即可在前端看到演示数据。
 * </p>
 * 触发时机：Spring Boot 启动完成、Bean 初始化之后自动调用 run 方法。
 * <p>
 * 幂等设计：通过 repository.count() > 0 判断，已存在数据则跳过，
 * 保证重启不会重复注入（配合 H2 文件模式持久化尤为重要）。
 * </p>
 */
@Component // 声明为 Spring 组件，被容器扫描注册
public class JobDataInitializer implements CommandLineRunner {

    /** SLF4J 日志器 */
    private static final Logger log = LoggerFactory.getLogger(JobDataInitializer.class);

    /** 岗位仓库，用于查询数量和批量保存种子数据 */
    private final JobPostingRepository repository;

    /**
     * 构造函数注入仓库
     *
     * @param repository 岗位 JPA 仓库
     */
    public JobDataInitializer(JobPostingRepository repository) {
        this.repository = repository; // 赋值仓库
    }

    /**
     * 启动钩子方法
     * <p>
     * 职责：检测库是否为空 → 构造种子列表 → 批量保存 → 日志记录。
     * </p>
     * 为什么用 CommandLineRunner 而非 @PostConstruct：
     * CommandLineRunner 在所有 Bean 初始化完成后执行，
     * 保证 Repository 已就绪；@PostConstruct 时机偏早，可能注入未完成。
     *
     * @param args 启动参数（未使用）
     */
    @Override
    public void run(String... args) {
        // 幂等保护：已有数据则跳过，避免重启重复注入
        if (repository.count() > 0) {
            return; // 直接返回，不注入
        }
        LocalDate today = LocalDate.now(); // 取当天，用于计算相对发布日期
        List<JobPosting> seeds = List.of( // 构造种子列表（13 条示例岗位）
                // 1. 字节跳动 - 高级 Java 后端
                build("高级 Java 后端工程师", "字节跳动", "北京", JobCategory.DEVELOPMENT,
                        ExperienceLevel.THREE_TO_FIVE, EducationLevel.BACHELOR,
                        30000, 60000, "30-60K", "Java/Spring/MySQL/Redis",
                        "负责抖音电商后端核心服务设计与开发，承接高并发交易场景。",
                        "3 年以上 Java 后端经验，精通 Spring 生态，熟悉分布式系统设计。",
                        "免费三餐/房补/期权/弹性工作", "字节跳动，全球领先的内容科技公司。",
                        today.minusDays(1)), // 发布于 1 天前
                // 2. 腾讯 - 前端工程师
                build("前端工程师", "腾讯", "深圳", JobCategory.DEVELOPMENT,
                        ExperienceLevel.ONE_TO_THREE, EducationLevel.BACHELOR,
                        20000, 40000, "20-40K", "React/TypeScript/Webpack",
                        "参与微信小程序开发者工具前端研发，提升开发者体验。",
                        "熟练掌握 React/Vue，理解浏览器原理，有大型前端项目经验优先。",
                        "免费班车/股票期权/年度体检", "腾讯，中国领先的互联网综合服务商。",
                        today.minusDays(2)), // 发布于 2 天前
                // 3. 阿里巴巴 - 产品经理
                build("产品经理", "阿里巴巴", "杭州", JobCategory.PRODUCT,
                        ExperienceLevel.THREE_TO_FIVE, EducationLevel.BACHELOR,
                        25000, 50000, "25-50K", "B 端产品/数据驱动/用户研究",
                        "负责淘宝商家后台核心模块的产品规划与迭代。",
                        "3 年以上 B 端产品经验，具备数据分析能力，懂电商业务。",
                        "股票期权/午餐补助/弹性工作", "阿里巴巴，让天下没有难做的生意。",
                        today.minusDays(3)),
                // 4. 美团 - UI 设计师
                build("UI 设计师", "美团", "北京", JobCategory.DESIGN,
                        ExperienceLevel.ONE_TO_THREE, EducationLevel.BACHELOR,
                        15000, 30000, "15-30K", "Figma/视觉设计/交互",
                        "负责美团外卖 App 关键交易链路的视觉与交互设计。",
                        "有移动端设计经验，作品集优秀，理解平台设计规范。",
                        "免费午餐/团建/电脑补贴", "美团，帮大家吃得更好，生活更好。",
                        today.minusDays(2)),
                // 5. 拼多多 - 运营专员
                build("运营专员", "拼多多", "上海", JobCategory.OPERATIONS,
                        ExperienceLevel.NONE, EducationLevel.COLLEGE,
                        8000, 15000, "8-15K", "活动运营/数据分析",
                        "负责百亿补贴频道日常活动运营与效果复盘。",
                        "对电商运营有热情，沟通能力强，能独立策划活动。",
                        "六险一金/全勤奖/年终奖", "拼多多，多实惠多乐趣。",
                        today.minusDays(5)),
                // 6. 网易 - 测试工程师
                build("测试工程师", "网易", "杭州", JobCategory.QA,
                        ExperienceLevel.ONE_TO_THREE, EducationLevel.BACHELOR,
                        15000, 25000, "15-25K", "自动化测试/Selenium/Python",
                        "负责网易严选核心业务的自动化测试体系建设。",
                        "熟悉 Python/Java 至少一门，有接口/UI 自动化经验。",
                        "免费健身房/下午茶/股票", "网易，以匠心致创新。",
                        today.minusDays(4)),
                // 7. 百度 - DevOps 工程师
                build("DevOps 工程师", "百度", "北京", JobCategory.DEVOPS,
                        ExperienceLevel.THREE_TO_FIVE, EducationLevel.BACHELOR,
                        25000, 45000, "25-45K", "Kubernetes/Docker/CI-CD",
                        "负责百度搜索核心集群的容器化与发布平台建设。",
                        "深入理解 K8s，有大规模集群运维经验，熟悉 Go 优先。",
                        "股票期权/免费晚餐/班车", "百度，用科技让复杂的世界更简单。",
                        today.minusDays(1)),
                // 8. 字节跳动 - 数据分析师
                build("数据分析师", "字节跳动", "上海", JobCategory.DATA,
                        ExperienceLevel.ONE_TO_THREE, EducationLevel.BACHELOR,
                        20000, 35000, "20-35K", "SQL/Python/Tableau",
                        "负责抖音电商商家数据看板搭建与业务分析。",
                        "熟练 SQL，掌握 Python 数据分析栈，业务敏感度高。",
                        "免费三餐/房补/期权", "字节跳动，激发创造，丰富生活。",
                        today.minusDays(2)),
                // 9. 商汤科技 - 算法工程师
                build("算法工程师", "商汤科技", "北京", JobCategory.ALGORITHM,
                        ExperienceLevel.THREE_TO_FIVE, EducationLevel.MASTER,
                        35000, 70000, "35-70K", "深度学习/PyTorch/CV",
                        "负责计算机视觉模型研发与产品化落地。",
                        "硕士及以上，熟悉 PyTorch，有顶会论文或竞赛经验优先。",
                        "股票期权/弹性工作/算力补贴", "商汤科技，坚持原创，让 AI 引领人类进步。",
                        today.minusDays(3)),
                // 10. 滴滴出行 - 高级前端工程师
                build("高级前端工程师", "滴滴出行", "北京", JobCategory.DEVELOPMENT,
                        ExperienceLevel.THREE_TO_FIVE, EducationLevel.BACHELOR,
                        25000, 45000, "25-45K", "Vue/Node.js/微前端",
                        "负责滴滴司机端 Web 平台架构演进与性能优化。",
                        "精通前端工程化，有微前端/SSR 实战经验，懂 Node.js。",
                        "免费晚餐/打车报销/股票", "滴滴出行，让出行更美好。",
                        today.minusDays(2)),
                // 11. 小红书 - 用户运营
                build("用户运营", "小红书", "上海", JobCategory.OPERATIONS,
                        ExperienceLevel.ONE_TO_THREE, EducationLevel.BACHELOR,
                        12000, 22000, "12-22K", "社群运营/内容策划",
                        "负责小红书社区核心用户社群的运营与活跃度提升。",
                        "熟悉内容平台玩法，有社群运营经验，懂年轻人。",
                        "零食下午茶/股票期权/弹性", "小红书，标记我的生活。",
                        today.minusDays(4)),
                // 12. 京东 - Java 开发（应届）
                build("Java 开发工程师", "京东", "北京", JobCategory.DEVELOPMENT,
                        ExperienceLevel.FRESH, EducationLevel.BACHELOR,
                        18000, 30000, "18-30K", "Java/Spring Boot/MySQL",
                        "参与京东到家订单核心系统研发，2024 届校招岗位。",
                        "2024 届毕业生，本科及以上，计算机相关专业，基础扎实。",
                        "应届生房补/股票/导师制", "京东，技术驱动引领高品质消费。",
                        today.minusDays(1)),
                // 13. 字节跳动 - 高级算法工程师
                build("高级算法工程师", "字节跳动", "深圳", JobCategory.ALGORITHM,
                        ExperienceLevel.FIVE_TO_TEN, EducationLevel.MASTER,
                        50000, 90000, "50-90K", "推荐系统/TensorFlow/排序模型",
                        "负责抖音推荐核心排序模型迭代与线上效果优化。",
                        "5 年以上推荐/搜索算法经验，有大规模机器学习实战经验。",
                        "免费三餐/房补/股票期权", "字节跳动，全球创作与交流平台。",
                        today.minusDays(2))
        );

        repository.saveAll(seeds); // 批量保存种子数据
        log.info("已注入 {} 条本地种子岗位数据", seeds.size()); // 记录注入日志
    }

    /**
     * 构造单条种子岗位的工厂方法
     * <p>
     * 职责：封装 JobPosting 的字段设置，避免种子列表中重复样板代码。
     * </p>
     * 为什么用 private 工厂方法：13 条数据字段相同，工厂方法让列表更紧凑可读。
     *
     * @param title           岗位标题
     * @param company         公司名称
     * @param city            工作城市
     * @param category        岗位分类
     * @param exp             经验要求
     * @param edu             学历要求
     * @param salaryMin       薪资下限（元）
     * @param salaryMax       薪资上限（元）
     * @param salaryDesc      薪资描述
     * @param skills          技能标签
     * @param responsibilities 岗位职责
     * @param requirements    任职要求
     * @param benefits        福利待遇
     * @param companyDesc     公司简介
     * @param publishedDate   发布日期
     * @return 填充完毕的 JobPosting 实体（未持久化）
     */
    private JobPosting build(String title, String company, String city, JobCategory category,
                             ExperienceLevel exp, EducationLevel edu,
                             int salaryMin, int salaryMax, String salaryDesc, String skills,
                             String responsibilities, String requirements,
                             String benefits, String companyDesc, LocalDate publishedDate) {
        JobPosting j = new JobPosting(); // 新建实体
        j.setTitle(title);                       // 标题
        j.setCompany(company);                   // 公司
        j.setCity(city);                         // 城市
        j.setCategory(category);                 // 分类
        j.setExperience(exp);                    // 经验
        j.setEducation(edu);                     // 学历
        j.setSalaryMin(salaryMin);               // 薪资下限
        j.setSalaryMax(salaryMax);               // 薪资上限
        j.setSalaryDesc(salaryDesc);             // 薪资描述
        j.setSkills(skills);                     // 技能
        j.setResponsibilities(responsibilities); // 职责
        j.setRequirements(requirements);         // 要求
        j.setBenefits(benefits);                 // 福利
        j.setCompanyDesc(companyDesc);           // 公司简介
        j.setPublishedDate(publishedDate);       // 发布日期
        j.setSource(JobSource.LOCAL);            // 标记为本地种子数据
        return j; // 返回实体
    }
}

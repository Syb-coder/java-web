package com.example.java6.init; // 声明当前类所在的包路径，属于应用初始化模块

import com.example.java6.model.AdminUser; // 导入管理员实体类，用于创建默认管理员账号
import com.example.java6.model.KnowledgeArticle; // 导入安全知识文章实体类，用于持久化示例知识文章
import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，标识知识文章类别（如防护技巧、科普、案例、应急等）
import com.example.java6.model.News; // 导入新闻实体类，用于持久化示例新闻资讯
import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，标识新闻类别（如安全动态、行业动态、政策解读、漏洞预警等）
import com.example.java6.model.Regulation; // 导入法规实体类，用于持久化示例政策法规
import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，标识法规层级（如法律、行政法规、部门规章等）
import com.example.java6.model.Report; // 导入举报实体类，用于持久化示例举报记录
import com.example.java6.model.ReportStatus; // 导入举报状态枚举，标识举报处理状态（待处理、已处理等）
import com.example.java6.model.ReportType; // 导入举报类型枚举，标识举报内容类别（如赌博诈骗、谣言、色情等）
import com.example.java6.repository.AdminUserRepository; // 导入管理员仓库接口，用于查询与保存管理员账号
import com.example.java6.repository.KnowledgeArticleRepository; // 导入知识文章仓库接口，用于查询与保存知识文章
import com.example.java6.repository.NewsRepository; // 导入新闻仓库接口，用于查询与保存新闻资讯
import com.example.java6.repository.RegulationRepository; // 导入法规仓库接口，用于查询与保存政策法规
import com.example.java6.repository.ReportRepository; // 导入举报仓库接口，用于查询与保存举报记录
import com.example.java6.service.AuthService; // 导入认证服务，用于对默认密码进行 BCrypt 加密
import org.slf4j.Logger; // 导入 SLF4J 日志接口，用于记录初始化过程的关键日志
import org.slf4j.LoggerFactory; // 导入日志工厂，用于创建 Logger 实例
import org.springframework.boot.ApplicationRunner; // 导入 Spring Boot 启动回调接口，应用启动后自动执行其逻辑
import org.springframework.context.annotation.Bean; // 导入 Bean 注解，将方法返回值注册为 Spring Bean
import org.springframework.context.annotation.Configuration; // 导入配置类注解，标识此类为 Spring 配置类

import java.time.LocalDate; // 导入日期类（不含时间），用于法规的颁布日期与生效日期
import java.time.LocalDateTime; // 导入日期时间类，用于新闻的发布时间与举报的处置时间

/**
 * 国家网络安全宣传官网 - 示例数据初始化器
 *
 * <p>应用首次启动时若数据库为空，则预置一批示范数据，
 * 涵盖新闻资讯、安全知识、政策法规、举报记录四大模块，
 * 便于首次访问即可看到完整内容。</p>
 */
@Configuration // 标记为 Spring 配置类，容器启动时扫描并执行其中的 Bean 定义
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class); // 创建日志记录器，输出初始化过程信息

    /**
     * 启动时按需注入示例数据
     *
     * @param newsRepo      新闻仓库
     * @param knowledgeRepo 知识仓库
     * @param regulationRepo 法规仓库
     * @param reportRepo    举报仓库
     * @param adminRepo     管理员账号仓库
     * @param authService   认证服务（用于 BCrypt 加密密码）
     * @return ApplicationRunner
     */
    @Bean // 将方法返回值注册为 Spring Bean，类型为 ApplicationRunner，Spring Boot 启动完成后自动调用其 run 方法
    ApplicationRunner initData(NewsRepository newsRepo, // Spring 自动注入新闻仓库
                               KnowledgeArticleRepository knowledgeRepo, // Spring 自动注入知识文章仓库
                               RegulationRepository regulationRepo, // Spring 自动注入法规仓库
                               ReportRepository reportRepo, // Spring 自动注入举报仓库
                               AdminUserRepository adminRepo, // Spring 自动注入管理员仓库
                               AuthService authService) { // Spring 自动注入认证服务（用于密码加密）
        return args -> { // 返回 ApplicationRunner 的 lambda 实现，args 为启动参数
            if (newsRepo.count() == 0) { // 新闻表为空时才初始化，避免重复启动重复插入
                initNews(newsRepo); // 调用新闻初始化方法
            }
            if (knowledgeRepo.count() == 0) { // 知识文章表为空时才初始化
                initKnowledge(knowledgeRepo); // 调用知识文章初始化方法
            }
            if (regulationRepo.count() == 0) { // 法规表为空时才初始化
                initRegulations(regulationRepo); // 调用法规初始化方法
            }
            if (reportRepo.count() == 0) { // 举报表为空时才初始化
                initReports(reportRepo); // 调用举报初始化方法
            }
            // 初始化默认管理员账号（仅当不存在任何管理员时）
            if (adminRepo.count() == 0) { // 管理员表为空时才创建默认账号
                initAdminUser(adminRepo, authService); // 调用管理员初始化方法
            }
            log.info("国家网络安全宣传官网示例数据初始化完成：新闻 {} 条、知识 {} 篇、法规 {} 条、举报 {} 条、管理员 {} 个", // 输出初始化完成日志，便于排查数据初始化情况
                    newsRepo.count(), knowledgeRepo.count(), regulationRepo.count(), reportRepo.count(), adminRepo.count()); // 各表当前记录数
        };
    }

    /**
     * 初始化默认管理员账号
     *
     * <p>默认账号：admin / admin@123（生产环境请立即修改密码）。</p>
     *
     * @param repo        管理员仓库
     * @param authService 认证服务（用于 BCrypt 加密）
     */
    private void initAdminUser(AdminUserRepository repo, AuthService authService) {
        String encodedPassword = authService.encodePassword("admin@123"); // 使用 BCrypt 加密默认密码，避免明文存储
        AdminUser admin = new AdminUser( // 创建管理员实体，三参数构造器：用户名、加密密码、显示名
                "admin", // 用户名：admin
                encodedPassword, // 密码：BCrypt 加密后的 admin@123
                "系统管理员" // 显示名：系统管理员
        );
        repo.save(admin); // 持久化保存管理员账号到数据库
        log.info("默认管理员账号已创建：admin / admin@123（请及时修改密码）"); // 输出创建日志，提示运维人员修改默认密码
    }

    /**
     * 初始化新闻资讯示例数据
     *
     * @param repo 新闻仓库
     */
    private void initNews(NewsRepository repo) {
        repo.save(new News( // 第1条：网络安全宣传周开幕新闻（安全动态类）
                "2026年国家网络安全宣传周在京开幕", // 新闻标题
                NewsCategory.SECURITY_NEWS, // 分类：安全动态（重大安全活动新闻）
                "以“网络安全为人民，网络安全靠人民”为主题的2026年国家网络安全宣传周今日在北京开幕。", // 摘要
                "2026年国家网络安全宣传周开幕式今日在京举行。本届宣传周以“网络安全为人民，网络安全靠人民”为主题，" + // 正文第一段（活动概况）
                        "由中央宣传部、中央网信办、教育部、工业和信息化部、公安部等部门联合举办。\n\n" + // 主办单位
                        "宣传周期间，将举行网络安全博览会、网络安全技术高峰论坛、网络安全主题日等活动，" + // 主要活动
                        "深入宣传贯彻习近平总书记关于网络强国的重要思想，普及网络安全知识，提升全民网络安全意识和防护技能。", // 活动意义
                "中央网信办", // 来源：中央网信办
                LocalDateTime.now().minusDays(1) // 发布时间：当前时间减1天
        ));
        repo.findAll().get(0).setTop(true); // 将第1条新闻设为置顶（首页突出展示宣传周开幕信息）
        repo.save(repo.findAll().get(0)); // 保存置顶状态到数据库

        repo.save(new News( // 第2条：公安部打击网络诈骗成果新闻（安全动态类）
                "公安部通报打击网络诈骗专项行动成果", // 标题
                NewsCategory.SECURITY_NEWS, // 分类：安全动态
                "今年以来全国公安机关破获电信网络诈骗案件45万起，挽回经济损失120亿元。", // 摘要
                "公安部今日召开新闻发布会，通报2026年上半年打击治理电信网络诈骗犯罪专项行动成果。" + // 正文
                        "今年以来，全国公安机关共破获电信网络诈骗案件45万起，抓获犯罪嫌疑人8.9万名，" +
                        "拦截诈骗电话2.3亿次、诈骗短信4.7亿条，为群众挽回经济损失120亿元。\n\n" +
                        "公安部相关负责人表示，将继续保持对电信网络诈骗犯罪的严打高压态势，" +
                        "深入推进“断卡”“断流”“拔塞”等专项行动，坚决遏制电信网络诈骗犯罪高发多发态势。",
                "公安部", // 来源：公安部
                LocalDateTime.now().minusDays(3) // 发布时间：3天前
        ));

        repo.save(new News( // 第3条：网络安全产业规模突破2500亿新闻（行业动态类）
                "国内网络安全产业规模突破2500亿元", // 标题
                NewsCategory.INDUSTRY_TREND, // 分类：行业动态（产业发展趋势）
                "2026年上半年我国网络安全产业规模达2538亿元，同比增长15.6%。", // 摘要
                "中国网络安全产业联盟发布的《2026年上半年网络安全产业发展报告》显示，" + // 正文
                        "上半年我国网络安全产业规模达2538亿元，同比增长15.6%。" +
                        "其中，网络安全产品收入892亿元，服务收入1023亿元，集成收入623亿元。\n\n" +
                        "报告指出，随着数字化转型深入推进和数据安全法、个人信息保护法等法律法规实施，" +
                        "政企机构网络安全投入持续加大，网络安全产业保持快速增长态势。",
                "中国网络安全产业联盟", // 来源：中国网络安全产业联盟
                LocalDateTime.now().minusDays(5) // 发布时间：5天前
        ));

        repo.save(new News( // 第4条：AI大模型在威胁检测中的应用新闻（行业动态类）
                "AI赋能网络安全：大模型在威胁检测中的应用", // 标题
                NewsCategory.INDUSTRY_TREND, // 分类：行业动态
                "网络安全大模型加速落地，多家厂商推出安全垂类大模型产品。", // 摘要
                "近期，多家网络安全厂商陆续发布安全大模型产品，将人工智能技术深度应用于威胁检测、" + // 正文
                        "安全运营、漏洞分析等场景。奇安信、深信服、安恒信息等企业均推出网络安全垂类大模型。\n\n" +
                        "业内专家表示，大模型技术可显著提升安全运营效率，但同时也带来模型安全、" +
                        "数据隐私等新型风险，需要同步加强AI安全治理。",
                "科技日报", // 来源：科技日报
                LocalDateTime.now().minusDays(7) // 发布时间：7天前
        ));

        repo.save(new News( // 第5条：数据出境安全评估办法解读新闻（政策解读类）
                "《数据出境安全评估办法》最新解读", // 标题
                NewsCategory.POLICY_INTERPRETATION, // 分类：政策解读
                "国家网信办就《数据出境安全评估办法》适用范围、申报流程等进行权威解读。", // 摘要
                "国家网信办今日就《数据出境安全评估办法》发布最新解读，明确数据出境安全评估的适用范围、" + // 正文
                        "申报材料、评估流程、评估结果等关键问题。\n\n" +
                        "解读指出，处理100万人以上个人信息的数据处理者向境外提供个人信息，" +
                        "或累计向境外提供10万人以上个人信息的，应当申报数据出境安全评估。" +
                        "评估结果自作出之日起2年内有效，有效期届满需继续数据出境活动的，应重新申报。",
                "国家网信办", // 来源：国家网信办
                LocalDateTime.now().minusDays(10) // 发布时间：10天前
        ));

        repo.save(new News( // 第6条：个人信息保护法实施要点解析新闻（政策解读类）
                "《个人信息保护法》实施要点解析", // 标题
                NewsCategory.POLICY_INTERPRETATION, // 分类：政策解读
                "个人信息处理规则、跨境提供规则、个人信息主体权利等核心要点详解。", // 摘要
                "《个人信息保护法》实施以来，个人信息处理活动的合规要求更加明确。" + // 正文
                        "本文从个人信息处理规则、敏感个人信息保护、跨境提供规则、个人信息主体权利、" +
                        "个人信息处理者义务五个方面进行系统解析。\n\n" +
                        "重点强调“告知-同意”原则的落地实施，以及对人脸、指纹等敏感个人信息的特殊保护要求，" +
                        "为企业合规建设提供指引。",
                "法治日报", // 来源：法治日报
                LocalDateTime.now().minusDays(14) // 发布时间：14天前
        ));

        repo.save(new News( // 第7条：Apache Log4j 高危漏洞预警（漏洞预警类）
                "紧急：Apache Log4j 高危漏洞预警", // 标题
                NewsCategory.VULNERABILITY_ALERT, // 分类：漏洞预警
                "Apache Log4j 2被发现远程代码执行漏洞（CVE-2026-xxxx），影响全球数百万系统。", // 摘要
                "国家信息安全漏洞共享平台（CNVD）收录了Apache Log4j 2远程代码执行漏洞（CVE-2026-xxxx），" + // 正文
                        "该漏洞允许攻击者在目标服务器上执行任意代码，影响范围极广。\n\n" +
                        "受影响版本：Log4j 2.0-beta9 至 2.17.1。\n" +
                        "建议措施：1. 立即升级至 Log4j 2.18.0 及以上版本；2. 临时缓解措施包括设置 log4j2.formatMsgNoLookups=true；" +
                        "3. 对受影响系统进行全面排查，检测是否已被利用。\n\n" +
                        "请各单位高度重视，及时处置，严防被攻击利用。",
                "CNVD", // 来源：国家信息安全漏洞共享平台
                LocalDateTime.now().minusDays(2) // 发布时间：2天前
        ));

        repo.save(new News( // 第8条：LockBit 4.0 勒索病毒预警（漏洞预警类）
                "警惕：新型勒索病毒 LockBit 4.0 在国内传播", // 标题
                NewsCategory.VULNERABILITY_ALERT, // 分类：漏洞预警
                "LockBit 4.0勒索病毒针对国内政企单位发起攻击，请加强防范。", // 摘要
                "国家计算机病毒应急处理中心监测发现，LockBit 4.0勒索病毒近期在国内传播，" + // 正文
                        "主要针对政府、医疗、教育等单位发起攻击，通过钓鱼邮件、漏洞利用等方式入侵内网，" +
                        "加密关键数据并勒索赎金。\n\n" +
                        "防护建议：1. 及时更新系统补丁和杀毒软件病毒库；2. 定期备份重要数据并离线存储；" +
                        "3. 加强员工网络安全意识培训，警惕钓鱼邮件；4. 关闭不必要的端口和服务；" +
                        "5. 建立应急响应机制，发现异常及时隔离处置。",
                "国家计算机病毒应急处理中心", // 来源：国家计算机病毒应急处理中心
                LocalDateTime.now().minusDays(4) // 发布时间：4天前
        ));
        log.info("新闻资讯示例数据已载入"); // 输出新闻初始化完成日志
    }

    /**
     * 初始化安全知识文章示例数据
     *
     * @param repo 知识仓库
     */
    private void initKnowledge(KnowledgeArticleRepository repo) {
        repo.save(new KnowledgeArticle( // 第1篇：密码安全设置指南（防护技巧类）
                "密码安全设置指南：如何打造强密码", // 标题
                KnowledgeCategory.PROTECTION_TIPS, // 分类：防护技巧
                "强密码是保护账户安全的第一道防线，掌握密码设置技巧至关重要。", // 摘要
                "一、密码长度：建议至少12位，长度越长越难破解。\n\n" + // 正文：详细介绍密码设置七大要点
                        "二、密码复杂度：包含大小写字母、数字、特殊符号四类字符中的至少三类。\n\n" +
                        "三、密码唯一性：不同账户使用不同密码，避免一处泄露处处沦陷。\n\n" +
                        "四、避免使用：生日、手机号、姓名拼音、连续字符（123456、qwerty）等易被猜到的密码。\n\n" +
                        "五、管理工具：推荐使用密码管理器（如KeePass、1Password）统一管理密码。\n\n" +
                        "六、二次验证：开启短信、邮箱、验证器App等两步验证，提升账户安全性。\n\n" +
                        "七、定期更换：重要账户密码建议每3-6个月更换一次。",
                "网络安全宣传周专家组" // 作者：网络安全宣传周专家组
        ));

        repo.save(new KnowledgeArticle( // 第2篇：识别钓鱼邮件技巧（防护技巧类）
                "识别钓鱼邮件的五个关键技巧", // 标题
                KnowledgeCategory.PROTECTION_TIPS, // 分类：防护技巧
                "钓鱼邮件是网络攻击最常用的入口，掌握识别技巧可有效防范。", // 摘要
                "钓鱼邮件伪装成合法机构发送，诱导用户点击恶意链接或下载附件。识别要点：\n\n" + // 正文：五项识别技巧
                        "一、核查发件人邮箱：注意邮箱域名是否与官方一致，攻击者常使用相似域名（如 paypal-security.com 伪装成 paypal.com）。\n\n" +
                        "二、警惕紧急语气：钓鱼邮件常制造紧迫感，如“账户即将冻结”“立即验证”等，催促用户尽快操作。\n\n" +
                        "三、检查链接地址：鼠标悬停查看真实链接，注意是否指向陌生域名，切勿直接点击。\n\n" +
                        "四、谨慎处理附件：不轻易打开陌生邮件附件，尤其是 .exe、.scr、.zip 等可执行文件。\n\n" +
                        "五、核对邮件内容：注意错别字、语法错误、称呼不当等异常，正规机构邮件通常严谨规范。\n\n" +
                        "如收到可疑邮件，请通过官方渠道核实，切勿直接回复或点击其中链接。",
                "网络安全宣传周专家组" // 作者
        ));

        repo.save(new KnowledgeArticle( // 第3篇：网络安全基本概念科普（科普类）
                "什么是网络安全？一文读懂基本概念", // 标题
                KnowledgeCategory.POPULAR_SCIENCE, // 分类：科普
                "网络安全是保护网络系统、数据免受攻击、访问、修改或破坏的实践。", // 摘要
                "网络安全（Cybersecurity）是指通过采取必要措施，防范对网络的攻击、侵入、干扰、破坏和非法使用以及意外事故，" + // 正文：科普网络安全定义与CIA三要素
                        "使网络处于稳定可靠运行的状态，以及保障网络数据的完整性、保密性、可用性的能力。\n\n" +
                        "网络安全三大核心属性（CIA三要素）：\n" +
                        "1. 保密性（Confidentiality）：确保信息只被授权人员访问；\n" +
                        "2. 完整性（Integrity）：确保信息不被非法篡改或破坏；\n" +
                        "3. 可用性（Availability）：确保授权人员能及时访问所需信息。\n\n" +
                        "常见网络威胁包括：病毒木马、勒索软件、钓鱼攻击、DDoS攻击、SQL注入、社会工程学等。" +
                        "做好网络安全需要技术、管理、法律、教育多管齐下，每位网民都是网络安全的重要参与者。",
                "网络安全宣传周专家组" // 作者
        ));

        repo.save(new KnowledgeArticle( // 第4篇：个人信息保护科普（科普类）
                "个人信息保护：你的数据你做主", // 标题
                KnowledgeCategory.POPULAR_SCIENCE, // 分类：科普
                "了解个人信息范围、处理规则及个人权利，做自身信息的主人。", // 摘要
                "个人信息是以电子或者其他方式记录的与已识别或者可识别的自然人有关的各种信息，" + // 正文：科普个人信息范围与权利
                        "不包括匿名化处理后的信息。\n\n" +
                        "敏感个人信息包括：生物识别、宗教信仰、特定身份、医疗健康、金融账户、行踪轨迹等信息，" +
                        "以及不满14周岁未成年人的个人信息。\n\n" +
                        "个人信息处理应遵循合法、正当、必要、诚信原则，取得个人同意。" +
                        "个人信息主体享有知情权、决定权、查阅复制权、更正补充权、删除权、可携带权、注销权等权利。\n\n" +
                        "日常防护建议：1. 谨慎授权App权限；2. 不在非正规渠道填写个人信息；" +
                        "3. 定期清理手机App授权；4. 警惕各类“填写信息领奖品”活动；5. 使用正规平台进行交易。",
                "网络安全宣传周专家组" // 作者
        ));

        repo.save(new KnowledgeArticle( // 第5篇：电商数据泄露案例剖析（案例分析类）
                "案例剖析：某电商平台用户数据泄露事件", // 标题
                KnowledgeCategory.CASE_ANALYSIS, // 分类：案例分析
                "通过真实案例剖析数据泄露的原因、影响与防范措施。", // 摘要
                "【事件回顾】2025年某电商平台因接口未做权限校验，导致攻击者通过遍历用户ID获取超过500万用户个人信息，" + // 正文：事件回顾、原因分析、防范措施
                        "包括姓名、手机号、收货地址等。\n\n" +
                        "【原因分析】\n" +
                        "1. 接口权限缺失：用户信息查询接口未做身份认证和权限校验；\n" +
                        "2. 频率限制不足：未对单IP、单账户请求频率进行限制，导致遍历攻击得逞；\n" +
                        "3. 数据脱敏缺失：返回数据未对手机号等敏感信息进行脱敏处理；\n" +
                        "4. 监控告警缺失：异常访问未及时被发现和阻断。\n\n" +
                        "【防范措施】\n" +
                        "1. 严格落实接口身份认证和授权机制；\n" +
                        "2. 部署WAF、API网关等防护设备，设置频率限制；\n" +
                        "3. 对敏感数据进行脱敏、加密存储；\n" +
                        "4. 建立健全安全监测和应急响应机制；\n" +
                        "5. 定期开展安全评估和渗透测试。",
                "网络安全宣传周专家组" // 作者
        ));

        repo.save(new KnowledgeArticle( // 第6篇：勒索病毒应急响应六步法（应急处置类）
                "遭遇勒索病毒怎么办？应急响应六步法", // 标题
                KnowledgeCategory.EMERGENCY_RESPONSE, // 分类：应急处置
                "遭遇勒索病毒攻击后的标准应急处置流程，最大限度减少损失。", // 摘要
                "一旦发现勒索病毒感染，应立即按照以下步骤处置：\n\n" + // 正文：六步应急处置流程
                        "第一步：隔离断网。立即断开感染主机的网络连接（拔网线、关闭WiFi），防止病毒在内网横向传播。" +
                        "切勿急于重启或关机，以免丢失内存中的解密线索。\n\n" +
                        "第二步：评估范围。排查内网其他主机是否被感染，确定感染范围和影响程度。\n\n" +
                        "第三步：保留证据。对感染主机进行内存镜像、磁盘镜像，保留病毒样本、勒索信等证据，" +
                        "便于后续溯源分析和报警。\n\n" +
                        "第四步：清除病毒。使用专业杀毒软件对感染主机进行全盘查杀，清除病毒本体及持久化项。\n\n" +
                        "第五步：恢复数据。优先从离线备份恢复数据；若无备份，可查询是否有公开解密工具" +
                        "（如 No More Ransom 项目），切勿轻易支付赎金。\n\n" +
                        "第六步：复盘整改。分析入侵路径，修复漏洞，加强防护，完善备份机制，防止再次发生。",
                "网络安全宣传周专家组" // 作者
        ));
        log.info("安全知识示例数据已载入"); // 输出知识文章初始化完成日志
    }

    /**
     * 初始化政策法规示例数据
     *
     * @param repo 法规仓库
     */
    private void initRegulations(RegulationRepository repo) {
        Regulation cyberLaw = new Regulation( // 第1部：《网络安全法》（基础性法律）
                "中华人民共和国网络安全法", // 法规标题
                "全国人民代表大会常务委员会", // 颁布机构：全国人大常委会
                RegulationCategory.LAW, // 分类：法律（最高层级）
                LocalDate.of(2016, 11, 7), // 颁布日期：2016年11月7日通过
                "《中华人民共和国网络安全法》是我国网络安全领域的基础性法律，于2016年11月7日由第十二届全国人民代表大会常务委员会第二十四次会议通过，自2017年6月1日起施行。\n\n" + // 正文：法律概况与核心制度
                        "本法共七章七十九条，主要内容包括：\n" +
                        "1. 网络安全支持与促进；\n" +
                        "2. 网络运行安全一般规定；\n" +
                        "3. 关键信息基础设施运行安全；\n" +
                        "4. 网络信息安全；\n" +
                        "5. 监测预警与应急处置；\n" +
                        "6. 法律责任。\n\n" +
                        "核心制度：网络安全等级保护制度、关键信息基础设施保护制度、个人信息保护制度、网络实名制等。"
        );
        cyberLaw.setEffectiveDate(LocalDate.of(2017, 6, 1)); // 生效日期：2017年6月1日
        cyberLaw.setDocumentNumber("中华人民共和国主席令第五十三号"); // 文号：主席令第五十三号
        repo.save(cyberLaw); // 持久化保存

        Regulation pipl = new Regulation( // 第2部：《个人信息保护法》
                "中华人民共和国个人信息保护法", // 法规标题
                "全国人民代表大会常务委员会", // 颁布机构：全国人大常委会
                RegulationCategory.LAW, // 分类：法律
                LocalDate.of(2021, 8, 20), // 颁布日期：2021年8月20日通过
                "《中华人民共和国个人信息保护法》于2021年8月20日由第十三届全国人民代表大会常务委员会第三十次会议通过，自2021年11月1日起施行。\n\n" + // 正文：法律概况与重点内容
                        "本法共八章七十四条，确立个人信息处理应遵循合法、正当、必要、诚信原则，" +
                        "构建以“告知-同意”为核心的个人信息处理规则，" +
                        "设立敏感个人信息保护、跨境提供、个人信息主体权利、个人信息处理者义务等专章。\n\n" +
                        "重点内容：\n" +
                        "1. 明确个人信息处理规则；\n" +
                        "2. 强化敏感个人信息保护；\n" +
                        "3. 规范个人信息跨境提供；\n" +
                        "4. 保障个人信息主体权利；\n" +
                        "5. 明确个人信息处理者义务；\n" +
                        "6. 强化履行个人信息保护职责部门的监管权限。"
        );
        pipl.setEffectiveDate(LocalDate.of(2021, 11, 1)); // 生效日期：2021年11月1日
        pipl.setDocumentNumber("中华人民共和国主席令第九十一号"); // 文号：主席令第九十一号
        repo.save(pipl); // 持久化保存

        Regulation cilsp = new Regulation( // 第3部：《关键信息基础设施安全保护条例》
                "关键信息基础设施安全保护条例", // 法规标题
                "国务院", // 颁布机构：国务院
                RegulationCategory.ADMINISTRATIVE_REGULATION, // 分类：行政法规
                LocalDate.of(2021, 7, 30), // 颁布日期：2021年7月30日公布
                "《关键信息基础设施安全保护条例》于2021年7月30日由国务院令第745号公布，自2021年9月1日起施行。\n\n" + // 正文：条例概况与核心制度
                        "本条例共六章五十一条，旨在保障关键信息基础设施安全，维护网络安全。" +
                        "明确关键信息基础设施是指公共通信和信息服务、能源、交通、水利、金融、公共服务、电子政务、国防科技工业等重要行业和领域的，" +
                        "以及其他一旦遭到破坏、丧失功能或者数据泄露，可能严重危害国家安全、国计民生、公共利益的重要网络设施、信息系统等。\n\n" +
                        "核心制度：\n" +
                        "1. 关键信息基础设施认定机制；\n" +
                        "2. 运营者安全保护义务；\n" +
                        "3. 保护工作部门职责；\n" +
                        "4. 安全审查机制；\n" +
                        "5. 应急处置机制。"
        );
        cilsp.setEffectiveDate(LocalDate.of(2021, 9, 1)); // 生效日期：2021年9月1日
        cilsp.setDocumentNumber("国务院令第745号"); // 文号：国务院令第745号
        repo.save(cilsp); // 持久化保存

        Regulation dsdl = new Regulation( // 第4部：《数据安全法》
                "数据安全法", // 法规标题
                "全国人民代表大会常务委员会", // 颁布机构：全国人大常委会
                RegulationCategory.LAW, // 分类：法律
                LocalDate.of(2021, 6, 10), // 颁布日期：2021年6月10日通过
                "《中华人民共和国数据安全法》于2021年6月10日由第十三届全国人民代表大会常务委员会第二十九次会议通过，自2021年9月1日起施行。\n\n" + // 正文：法律概况与核心制度
                        "本法共七章五十五条，确立数据安全保护管理各项制度，规范数据处理活动，保障数据安全，促进数据开发利用。\n\n" +
                        "核心制度：\n" +
                        "1. 数据分类分级保护制度；\n" +
                        "2. 重要数据保护目录；\n" +
                        "3. 数据安全风险评估机制；\n" +
                        "4. 数据安全审查制度；\n" +
                        "5. 数据出口管制；\n" +
                        "6. 数据跨境传输管理。"
        );
        dsdl.setEffectiveDate(LocalDate.of(2021, 9, 1)); // 生效日期：2021年9月1日
        dsdl.setDocumentNumber("中华人民共和国主席令第八十四号"); // 文号：主席令第八十四号
        repo.save(dsdl); // 持久化保存

        Regulation dsae = new Regulation( // 第5部：《数据出境安全评估办法》
                "数据出境安全评估办法", // 法规标题
                "国家互联网信息办公室", // 颁布机构：国家网信办
                RegulationCategory.DEPARTMENTAL_RULE, // 分类：部门规章
                LocalDate.of(2022, 7, 7), // 颁布日期：2022年7月7日公布
                "《数据出境安全评估办法》于2022年7月7日由国家互联网信息办公室公布，自2022年9月1日起施行。\n\n" + // 正文：办法概况与申报情形
                        "本办法共十八条，规范数据出境活动，保护个人信息权益，维护国家安全和公共利益。\n\n" +
                        "应当申报数据出境安全评估的情形：\n" +
                        "1. 数据处理者向境外提供重要数据；\n" +
                        "2. 关键信息基础设施运营者和处理100万人以上个人信息的数据处理者向境外提供个人信息；\n" +
                        "3. 自上年1月1日起累计向境外提供10万人个人信息或者1万人敏感个人信息的数据处理者向境外提供个人信息。\n\n" +
                        "评估重点：数据出境目的、接收方所在国家/地区数据安全保护环境、接收方数据保护水平等。"
        );
        dsae.setEffectiveDate(LocalDate.of(2022, 9, 1)); // 生效日期：2022年9月1日
        dsae.setDocumentNumber("国家互联网信息办公室令第11号"); // 文号：网信办令第11号
        repo.save(dsae); // 持久化保存
        log.info("政策法规示例数据已载入"); // 输出法规初始化完成日志
    }

    /**
     * 初始化举报记录示例数据
     *
     * @param repo 举报仓库
     */
    private void initReports(ReportRepository repo) {
        Report r1 = new Report( // 第1条举报：钓鱼诈骗网站举报
                ReportType.GAMBLING_FRAUD, // 类型：赌博诈骗
                "https://example-scam-site.com/login", // 举报 URL：钓鱼网站登录页
                "该网站冒充银行进行钓鱼诈骗，诱导用户输入银行卡号、密码、短信验证码，已有多人受骗。", // 举报描述
                "张先生", // 举报人姓名
                "138****5678" // 举报人联系方式（已脱敏）
        );
        repo.save(r1); // 持久化保存（状态默认为待处理）

        Report r2 = new Report( // 第2条举报：社交平台谣言举报
                ReportType.RUMOR, // 类型：谣言
                "https://example-social.com/post/123456", // 举报 URL：社交平台帖子
                "该社交平台账号发布不实疫情信息，编造某地封城谣言，引发民众恐慌抢购，请核实处置。", // 举报描述
                "李女士", // 举报人姓名
                "lijing@example.com" // 举报人邮箱
        );
        repo.save(r2); // 持久化保存（状态默认为待处理）

        Report r3 = new Report( // 第3条举报：论坛色情低俗内容举报（已处置）
                ReportType.PORNOGRAPHY, // 类型：色情
                "https://example-forum.com/thread/789", // 举报 URL：论坛帖子
                "该论坛板块存在大量色情低俗内容，且未设置年龄验证机制，影响恶劣。", // 举报描述
                "王先生", // 举报人姓名
                "139****1234" // 举报人联系方式（已脱敏）
        );
        r3.setStatus(ReportStatus.RESOLVED); // 设置状态为已处置（展示处置流程示例）
        r3.setHandleNote("经核查举报属实，已通知平台删除违规内容并对账号进行封禁处理。"); // 处置备注
        r3.setHandledAt(LocalDateTime.now().minusDays(1)); // 处置时间：1天前
        repo.save(r3); // 持久化保存
        log.info("举报记录示例数据已载入"); // 输出举报初始化完成日志
    }
}

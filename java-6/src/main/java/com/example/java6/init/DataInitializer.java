package com.example.java6.init;

import com.example.java6.model.AdminUser;
import com.example.java6.model.KnowledgeArticle;
import com.example.java6.model.KnowledgeCategory;
import com.example.java6.model.News;
import com.example.java6.model.NewsCategory;
import com.example.java6.model.Regulation;
import com.example.java6.model.RegulationCategory;
import com.example.java6.model.Report;
import com.example.java6.model.ReportStatus;
import com.example.java6.model.ReportType;
import com.example.java6.repository.AdminUserRepository;
import com.example.java6.repository.KnowledgeArticleRepository;
import com.example.java6.repository.NewsRepository;
import com.example.java6.repository.RegulationRepository;
import com.example.java6.repository.ReportRepository;
import com.example.java6.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 国家网络安全宣传官网 - 示例数据初始化器
 *
 * <p>应用首次启动时若数据库为空，则预置一批示范数据，
 * 涵盖新闻资讯、安全知识、政策法规、举报记录四大模块，
 * 便于首次访问即可看到完整内容。</p>
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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
    @Bean
    ApplicationRunner initData(NewsRepository newsRepo,
                               KnowledgeArticleRepository knowledgeRepo,
                               RegulationRepository regulationRepo,
                               ReportRepository reportRepo,
                               AdminUserRepository adminRepo,
                               AuthService authService) {
        return args -> {
            if (newsRepo.count() == 0) {
                initNews(newsRepo);
            }
            if (knowledgeRepo.count() == 0) {
                initKnowledge(knowledgeRepo);
            }
            if (regulationRepo.count() == 0) {
                initRegulations(regulationRepo);
            }
            if (reportRepo.count() == 0) {
                initReports(reportRepo);
            }
            // 初始化默认管理员账号（仅当不存在任何管理员时）
            if (adminRepo.count() == 0) {
                initAdminUser(adminRepo, authService);
            }
            log.info("国家网络安全宣传官网示例数据初始化完成：新闻 {} 条、知识 {} 篇、法规 {} 条、举报 {} 条、管理员 {} 个",
                    newsRepo.count(), knowledgeRepo.count(), regulationRepo.count(), reportRepo.count(), adminRepo.count());
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
        String encodedPassword = authService.encodePassword("admin@123");
        AdminUser admin = new AdminUser(
                "admin",
                encodedPassword,
                "系统管理员"
        );
        repo.save(admin);
        log.info("默认管理员账号已创建：admin / admin@123（请及时修改密码）");
    }

    /**
     * 初始化新闻资讯示例数据
     *
     * @param repo 新闻仓库
     */
    private void initNews(NewsRepository repo) {
        repo.save(new News(
                "2026年国家网络安全宣传周在京开幕",
                NewsCategory.SECURITY_NEWS,
                "以“网络安全为人民，网络安全靠人民”为主题的2026年国家网络安全宣传周今日在北京开幕。",
                "2026年国家网络安全宣传周开幕式今日在京举行。本届宣传周以“网络安全为人民，网络安全靠人民”为主题，" +
                        "由中央宣传部、中央网信办、教育部、工业和信息化部、公安部等部门联合举办。\n\n" +
                        "宣传周期间，将举行网络安全博览会、网络安全技术高峰论坛、网络安全主题日等活动，" +
                        "深入宣传贯彻习近平总书记关于网络强国的重要思想，普及网络安全知识，提升全民网络安全意识和防护技能。",
                "中央网信办",
                LocalDateTime.now().minusDays(1)
        ));
        repo.findAll().get(0).setTop(true);
        repo.save(repo.findAll().get(0));

        repo.save(new News(
                "公安部通报打击网络诈骗专项行动成果",
                NewsCategory.SECURITY_NEWS,
                "今年以来全国公安机关破获电信网络诈骗案件45万起，挽回经济损失120亿元。",
                "公安部今日召开新闻发布会，通报2026年上半年打击治理电信网络诈骗犯罪专项行动成果。" +
                        "今年以来，全国公安机关共破获电信网络诈骗案件45万起，抓获犯罪嫌疑人8.9万名，" +
                        "拦截诈骗电话2.3亿次、诈骗短信4.7亿条，为群众挽回经济损失120亿元。\n\n" +
                        "公安部相关负责人表示，将继续保持对电信网络诈骗犯罪的严打高压态势，" +
                        "深入推进“断卡”“断流”“拔塞”等专项行动，坚决遏制电信网络诈骗犯罪高发多发态势。",
                "公安部",
                LocalDateTime.now().minusDays(3)
        ));

        repo.save(new News(
                "国内网络安全产业规模突破2500亿元",
                NewsCategory.INDUSTRY_TREND,
                "2026年上半年我国网络安全产业规模达2538亿元，同比增长15.6%。",
                "中国网络安全产业联盟发布的《2026年上半年网络安全产业发展报告》显示，" +
                        "上半年我国网络安全产业规模达2538亿元，同比增长15.6%。" +
                        "其中，网络安全产品收入892亿元，服务收入1023亿元，集成收入623亿元。\n\n" +
                        "报告指出，随着数字化转型深入推进和数据安全法、个人信息保护法等法律法规实施，" +
                        "政企机构网络安全投入持续加大，网络安全产业保持快速增长态势。",
                "中国网络安全产业联盟",
                LocalDateTime.now().minusDays(5)
        ));

        repo.save(new News(
                "AI赋能网络安全：大模型在威胁检测中的应用",
                NewsCategory.INDUSTRY_TREND,
                "网络安全大模型加速落地，多家厂商推出安全垂类大模型产品。",
                "近期，多家网络安全厂商陆续发布安全大模型产品，将人工智能技术深度应用于威胁检测、" +
                        "安全运营、漏洞分析等场景。奇安信、深信服、安恒信息等企业均推出网络安全垂类大模型。\n\n" +
                        "业内专家表示，大模型技术可显著提升安全运营效率，但同时也带来模型安全、" +
                        "数据隐私等新型风险，需要同步加强AI安全治理。",
                "科技日报",
                LocalDateTime.now().minusDays(7)
        ));

        repo.save(new News(
                "《数据出境安全评估办法》最新解读",
                NewsCategory.POLICY_INTERPRETATION,
                "国家网信办就《数据出境安全评估办法》适用范围、申报流程等进行权威解读。",
                "国家网信办今日就《数据出境安全评估办法》发布最新解读，明确数据出境安全评估的适用范围、" +
                        "申报材料、评估流程、评估结果等关键问题。\n\n" +
                        "解读指出，处理100万人以上个人信息的数据处理者向境外提供个人信息，" +
                        "或累计向境外提供10万人以上个人信息的，应当申报数据出境安全评估。" +
                        "评估结果自作出之日起2年内有效，有效期届满需继续数据出境活动的，应重新申报。",
                "国家网信办",
                LocalDateTime.now().minusDays(10)
        ));

        repo.save(new News(
                "《个人信息保护法》实施要点解析",
                NewsCategory.POLICY_INTERPRETATION,
                "个人信息处理规则、跨境提供规则、个人信息主体权利等核心要点详解。",
                "《个人信息保护法》实施以来，个人信息处理活动的合规要求更加明确。" +
                        "本文从个人信息处理规则、敏感个人信息保护、跨境提供规则、个人信息主体权利、" +
                        "个人信息处理者义务五个方面进行系统解析。\n\n" +
                        "重点强调“告知-同意”原则的落地实施，以及对人脸、指纹等敏感个人信息的特殊保护要求，" +
                        "为企业合规建设提供指引。",
                "法治日报",
                LocalDateTime.now().minusDays(14)
        ));

        repo.save(new News(
                "紧急：Apache Log4j 高危漏洞预警",
                NewsCategory.VULNERABILITY_ALERT,
                "Apache Log4j 2被发现远程代码执行漏洞（CVE-2026-xxxx），影响全球数百万系统。",
                "国家信息安全漏洞共享平台（CNVD）收录了Apache Log4j 2远程代码执行漏洞（CVE-2026-xxxx），" +
                        "该漏洞允许攻击者在目标服务器上执行任意代码，影响范围极广。\n\n" +
                        "受影响版本：Log4j 2.0-beta9 至 2.17.1。\n" +
                        "建议措施：1. 立即升级至 Log4j 2.18.0 及以上版本；2. 临时缓解措施包括设置 log4j2.formatMsgNoLookups=true；" +
                        "3. 对受影响系统进行全面排查，检测是否已被利用。\n\n" +
                        "请各单位高度重视，及时处置，严防被攻击利用。",
                "CNVD",
                LocalDateTime.now().minusDays(2)
        ));

        repo.save(new News(
                "警惕：新型勒索病毒 LockBit 4.0 在国内传播",
                NewsCategory.VULNERABILITY_ALERT,
                "LockBit 4.0勒索病毒针对国内政企单位发起攻击，请加强防范。",
                "国家计算机病毒应急处理中心监测发现，LockBit 4.0勒索病毒近期在国内传播，" +
                        "主要针对政府、医疗、教育等单位发起攻击，通过钓鱼邮件、漏洞利用等方式入侵内网，" +
                        "加密关键数据并勒索赎金。\n\n" +
                        "防护建议：1. 及时更新系统补丁和杀毒软件病毒库；2. 定期备份重要数据并离线存储；" +
                        "3. 加强员工网络安全意识培训，警惕钓鱼邮件；4. 关闭不必要的端口和服务；" +
                        "5. 建立应急响应机制，发现异常及时隔离处置。",
                "国家计算机病毒应急处理中心",
                LocalDateTime.now().minusDays(4)
        ));
        log.info("新闻资讯示例数据已载入");
    }

    /**
     * 初始化安全知识文章示例数据
     *
     * @param repo 知识仓库
     */
    private void initKnowledge(KnowledgeArticleRepository repo) {
        repo.save(new KnowledgeArticle(
                "密码安全设置指南：如何打造强密码",
                KnowledgeCategory.PROTECTION_TIPS,
                "强密码是保护账户安全的第一道防线，掌握密码设置技巧至关重要。",
                "一、密码长度：建议至少12位，长度越长越难破解。\n\n" +
                        "二、密码复杂度：包含大小写字母、数字、特殊符号四类字符中的至少三类。\n\n" +
                        "三、密码唯一性：不同账户使用不同密码，避免一处泄露处处沦陷。\n\n" +
                        "四、避免使用：生日、手机号、姓名拼音、连续字符（123456、qwerty）等易被猜到的密码。\n\n" +
                        "五、管理工具：推荐使用密码管理器（如KeePass、1Password）统一管理密码。\n\n" +
                        "六、二次验证：开启短信、邮箱、验证器App等两步验证，提升账户安全性。\n\n" +
                        "七、定期更换：重要账户密码建议每3-6个月更换一次。",
                "网络安全宣传周专家组"
        ));

        repo.save(new KnowledgeArticle(
                "识别钓鱼邮件的五个关键技巧",
                KnowledgeCategory.PROTECTION_TIPS,
                "钓鱼邮件是网络攻击最常用的入口，掌握识别技巧可有效防范。",
                "钓鱼邮件伪装成合法机构发送，诱导用户点击恶意链接或下载附件。识别要点：\n\n" +
                        "一、核查发件人邮箱：注意邮箱域名是否与官方一致，攻击者常使用相似域名（如 paypal-security.com 伪装成 paypal.com）。\n\n" +
                        "二、警惕紧急语气：钓鱼邮件常制造紧迫感，如“账户即将冻结”“立即验证”等，催促用户尽快操作。\n\n" +
                        "三、检查链接地址：鼠标悬停查看真实链接，注意是否指向陌生域名，切勿直接点击。\n\n" +
                        "四、谨慎处理附件：不轻易打开陌生邮件附件，尤其是 .exe、.scr、.zip 等可执行文件。\n\n" +
                        "五、核对邮件内容：注意错别字、语法错误、称呼不当等异常，正规机构邮件通常严谨规范。\n\n" +
                        "如收到可疑邮件，请通过官方渠道核实，切勿直接回复或点击其中链接。",
                "网络安全宣传周专家组"
        ));

        repo.save(new KnowledgeArticle(
                "什么是网络安全？一文读懂基本概念",
                KnowledgeCategory.POPULAR_SCIENCE,
                "网络安全是保护网络系统、数据免受攻击、访问、修改或破坏的实践。",
                "网络安全（Cybersecurity）是指通过采取必要措施，防范对网络的攻击、侵入、干扰、破坏和非法使用以及意外事故，" +
                        "使网络处于稳定可靠运行的状态，以及保障网络数据的完整性、保密性、可用性的能力。\n\n" +
                        "网络安全三大核心属性（CIA三要素）：\n" +
                        "1. 保密性（Confidentiality）：确保信息只被授权人员访问；\n" +
                        "2. 完整性（Integrity）：确保信息不被非法篡改或破坏；\n" +
                        "3. 可用性（Availability）：确保授权人员能及时访问所需信息。\n\n" +
                        "常见网络威胁包括：病毒木马、勒索软件、钓鱼攻击、DDoS攻击、SQL注入、社会工程学等。" +
                        "做好网络安全需要技术、管理、法律、教育多管齐下，每位网民都是网络安全的重要参与者。",
                "网络安全宣传周专家组"
        ));

        repo.save(new KnowledgeArticle(
                "个人信息保护：你的数据你做主",
                KnowledgeCategory.POPULAR_SCIENCE,
                "了解个人信息范围、处理规则及个人权利，做自身信息的主人。",
                "个人信息是以电子或者其他方式记录的与已识别或者可识别的自然人有关的各种信息，" +
                        "不包括匿名化处理后的信息。\n\n" +
                        "敏感个人信息包括：生物识别、宗教信仰、特定身份、医疗健康、金融账户、行踪轨迹等信息，" +
                        "以及不满14周岁未成年人的个人信息。\n\n" +
                        "个人信息处理应遵循合法、正当、必要、诚信原则，取得个人同意。" +
                        "个人信息主体享有知情权、决定权、查阅复制权、更正补充权、删除权、可携带权、注销权等权利。\n\n" +
                        "日常防护建议：1. 谨慎授权App权限；2. 不在非正规渠道填写个人信息；" +
                        "3. 定期清理手机App授权；4. 警惕各类“填写信息领奖品”活动；5. 使用正规平台进行交易。",
                "网络安全宣传周专家组"
        ));

        repo.save(new KnowledgeArticle(
                "案例剖析：某电商平台用户数据泄露事件",
                KnowledgeCategory.CASE_ANALYSIS,
                "通过真实案例剖析数据泄露的原因、影响与防范措施。",
                "【事件回顾】2025年某电商平台因接口未做权限校验，导致攻击者通过遍历用户ID获取超过500万用户个人信息，" +
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
                "网络安全宣传周专家组"
        ));

        repo.save(new KnowledgeArticle(
                "遭遇勒索病毒怎么办？应急响应六步法",
                KnowledgeCategory.EMERGENCY_RESPONSE,
                "遭遇勒索病毒攻击后的标准应急处置流程，最大限度减少损失。",
                "一旦发现勒索病毒感染，应立即按照以下步骤处置：\n\n" +
                        "第一步：隔离断网。立即断开感染主机的网络连接（拔网线、关闭WiFi），防止病毒在内网横向传播。" +
                        "切勿急于重启或关机，以免丢失内存中的解密线索。\n\n" +
                        "第二步：评估范围。排查内网其他主机是否被感染，确定感染范围和影响程度。\n\n" +
                        "第三步：保留证据。对感染主机进行内存镜像、磁盘镜像，保留病毒样本、勒索信等证据，" +
                        "便于后续溯源分析和报警。\n\n" +
                        "第四步：清除病毒。使用专业杀毒软件对感染主机进行全盘查杀，清除病毒本体及持久化项。\n\n" +
                        "第五步：恢复数据。优先从离线备份恢复数据；若无备份，可查询是否有公开解密工具" +
                        "（如 No More Ransom 项目），切勿轻易支付赎金。\n\n" +
                        "第六步：复盘整改。分析入侵路径，修复漏洞，加强防护，完善备份机制，防止再次发生。",
                "网络安全宣传周专家组"
        ));
        log.info("安全知识示例数据已载入");
    }

    /**
     * 初始化政策法规示例数据
     *
     * @param repo 法规仓库
     */
    private void initRegulations(RegulationRepository repo) {
        Regulation cyberLaw = new Regulation(
                "中华人民共和国网络安全法",
                "全国人民代表大会常务委员会",
                RegulationCategory.LAW,
                LocalDate.of(2016, 11, 7),
                "《中华人民共和国网络安全法》是我国网络安全领域的基础性法律，于2016年11月7日由第十二届全国人民代表大会常务委员会第二十四次会议通过，自2017年6月1日起施行。\n\n" +
                        "本法共七章七十九条，主要内容包括：\n" +
                        "1. 网络安全支持与促进；\n" +
                        "2. 网络运行安全一般规定；\n" +
                        "3. 关键信息基础设施运行安全；\n" +
                        "4. 网络信息安全；\n" +
                        "5. 监测预警与应急处置；\n" +
                        "6. 法律责任。\n\n" +
                        "核心制度：网络安全等级保护制度、关键信息基础设施保护制度、个人信息保护制度、网络实名制等。"
        );
        cyberLaw.setEffectiveDate(LocalDate.of(2017, 6, 1));
        cyberLaw.setDocumentNumber("中华人民共和国主席令第五十三号");
        repo.save(cyberLaw);

        Regulation pipl = new Regulation(
                "中华人民共和国个人信息保护法",
                "全国人民代表大会常务委员会",
                RegulationCategory.LAW,
                LocalDate.of(2021, 8, 20),
                "《中华人民共和国个人信息保护法》于2021年8月20日由第十三届全国人民代表大会常务委员会第三十次会议通过，自2021年11月1日起施行。\n\n" +
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
        pipl.setEffectiveDate(LocalDate.of(2021, 11, 1));
        pipl.setDocumentNumber("中华人民共和国主席令第九十一号");
        repo.save(pipl);

        Regulation cilsp = new Regulation(
                "关键信息基础设施安全保护条例",
                "国务院",
                RegulationCategory.ADMINISTRATIVE_REGULATION,
                LocalDate.of(2021, 7, 30),
                "《关键信息基础设施安全保护条例》于2021年7月30日由国务院令第745号公布，自2021年9月1日起施行。\n\n" +
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
        cilsp.setEffectiveDate(LocalDate.of(2021, 9, 1));
        cilsp.setDocumentNumber("国务院令第745号");
        repo.save(cilsp);

        Regulation dsdl = new Regulation(
                "数据安全法",
                "全国人民代表大会常务委员会",
                RegulationCategory.LAW,
                LocalDate.of(2021, 6, 10),
                "《中华人民共和国数据安全法》于2021年6月10日由第十三届全国人民代表大会常务委员会第二十九次会议通过，自2021年9月1日起施行。\n\n" +
                        "本法共七章五十五条，确立数据安全保护管理各项制度，规范数据处理活动，保障数据安全，促进数据开发利用。\n\n" +
                        "核心制度：\n" +
                        "1. 数据分类分级保护制度；\n" +
                        "2. 重要数据保护目录；\n" +
                        "3. 数据安全风险评估机制；\n" +
                        "4. 数据安全审查制度；\n" +
                        "5. 数据出口管制；\n" +
                        "6. 数据跨境传输管理。"
        );
        dsdl.setEffectiveDate(LocalDate.of(2021, 9, 1));
        dsdl.setDocumentNumber("中华人民共和国主席令第八十四号");
        repo.save(dsdl);

        Regulation dsae = new Regulation(
                "数据出境安全评估办法",
                "国家互联网信息办公室",
                RegulationCategory.DEPARTMENTAL_RULE,
                LocalDate.of(2022, 7, 7),
                "《数据出境安全评估办法》于2022年7月7日由国家互联网信息办公室公布，自2022年9月1日起施行。\n\n" +
                        "本办法共十八条，规范数据出境活动，保护个人信息权益，维护国家安全和公共利益。\n\n" +
                        "应当申报数据出境安全评估的情形：\n" +
                        "1. 数据处理者向境外提供重要数据；\n" +
                        "2. 关键信息基础设施运营者和处理100万人以上个人信息的数据处理者向境外提供个人信息；\n" +
                        "3. 自上年1月1日起累计向境外提供10万人个人信息或者1万人敏感个人信息的数据处理者向境外提供个人信息。\n\n" +
                        "评估重点：数据出境目的、接收方所在国家/地区数据安全保护环境、接收方数据保护水平等。"
        );
        dsae.setEffectiveDate(LocalDate.of(2022, 9, 1));
        dsae.setDocumentNumber("国家互联网信息办公室令第11号");
        repo.save(dsae);
        log.info("政策法规示例数据已载入");
    }

    /**
     * 初始化举报记录示例数据
     *
     * @param repo 举报仓库
     */
    private void initReports(ReportRepository repo) {
        Report r1 = new Report(
                ReportType.GAMBLING_FRAUD,
                "https://example-scam-site.com/login",
                "该网站冒充银行进行钓鱼诈骗，诱导用户输入银行卡号、密码、短信验证码，已有多人受骗。",
                "张先生",
                "138****5678"
        );
        repo.save(r1);

        Report r2 = new Report(
                ReportType.RUMOR,
                "https://example-social.com/post/123456",
                "该社交平台账号发布不实疫情信息，编造某地封城谣言，引发民众恐慌抢购，请核实处置。",
                "李女士",
                "lijing@example.com"
        );
        repo.save(r2);

        Report r3 = new Report(
                ReportType.PORNOGRAPHY,
                "https://example-forum.com/thread/789",
                "该论坛板块存在大量色情低俗内容，且未设置年龄验证机制，影响恶劣。",
                "王先生",
                "139****1234"
        );
        r3.setStatus(ReportStatus.RESOLVED);
        r3.setHandleNote("经核查举报属实，已通知平台删除违规内容并对账号进行封禁处理。");
        r3.setHandledAt(LocalDateTime.now().minusDays(1));
        repo.save(r3);
        log.info("举报记录示例数据已载入");
    }
}

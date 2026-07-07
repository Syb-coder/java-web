package com.example.java5.service; // 声明包路径，归属 service（业务服务层）

// ===== 模型与仓库导入 =====
import com.example.java5.model.JobCategory;        // 岗位分类枚举
import com.example.java5.model.JobPosting;         // 招聘岗位实体
import com.example.java5.model.JobSource;          // 数据来源枚举
import com.example.java5.repository.JobPostingRepository; // JPA 仓库

// ===== Jackson 注解，用于反序列化 V2EX API 响应 =====
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 忽略未知字段，避免 V2EX 返回多余字段导致反序列化失败
import com.fasterxml.jackson.annotation.JsonProperty;          // 显式映射 JSON 字段名

// ===== 日志与 Spring 组件 =====
import org.slf4j.Logger;                  // SLF4J 日志接口
import org.slf4j.LoggerFactory;           // 日志工厂
import org.springframework.stereotype.Service; // 声明为 Spring Service
import org.springframework.web.client.RestClient; // Spring 6 的 HTTP 客户端（替代 RestTemplate）

// ===== 时间类型 =====
import java.time.Instant;    // 时间戳瞬时点
import java.time.LocalDate;  // 日期类型
import java.time.ZoneId;     // 时区

// ===== 正则表达式 =====
import java.util.regex.Matcher; // 正则匹配器
import java.util.regex.Pattern; // 正则模式（预编译提升性能）

/**
 * V2EX 酷工作节点 API 同步服务
 * <p>
 * 职责：定时/手动拉取 V2EX 酷工作节点最新主题，
 * 解析标题中的城市、薪资等信息，去重后入库。
 * </p>
 * 接口文档：https://www.v2ex.com/api/topics/show.json?node_name=jobs
 * <p>
 * V2EX 主题标题常见格式：[城市] 公司 招聘 岗位 薪资 xx-xxK
 * 例如：[北京] 字节跳动 招聘 Java 工程师 30-60K
 * </p>
 * 设计要点：
 * 1. 用 RestClient（Spring 6 推荐）替代 RestTemplate，API 更流畅。
 * 2. 正则预编译为 static final 常量，避免每次调用重复编译。
 * 3. 逐条 save 容错，单条失败不拖垮整批（曾因字段超长导致整批回滚的修复）。
 */
@Service // 标记为 Spring Service 组件
public class V2exJobSyncService {

    /** SLF4J 日志器，用于记录同步过程的关键事件 */
    private static final Logger log = LoggerFactory.getLogger(V2exJobSyncService.class);

    /** V2EX 酷工作节点 API 地址（保留常量便于维护，实际请求用 restClient 的 uri） */
    private static final String V2EX_JOBS_API =
            "https://www.v2ex.com/api/topics/show.json?node_name=jobs";

    /** 城市提取正则：匹配 [xxx] 中的内容，如 [北京] → 北京 */
    private static final Pattern CITY_PATTERN = Pattern.compile("\\[(.+?)]");
    /** 薪资提取正则：匹配 xx-xxK，忽略大小写，如 30-60K */
    private static final Pattern SALARY_PATTERN = Pattern.compile("(\\d+)-(\\d+)\\s*K", Pattern.CASE_INSENSITIVE);

    /** HTTP 客户端，用于调用 V2EX API */
    private final RestClient restClient;
    /** 岗位仓库，用于去重查询和持久化 */
    private final JobPostingRepository repository;

    /**
     * 构造函数：初始化仓库与 HTTP 客户端
     * <p>
     * 为什么自定义 User-Agent：V2EX API 对默认 Java UA 可能限流，
     * 标识应用身份有助于被识别为合法客户端。
     * </p>
     *
     * @param repository 岗位 JPA 仓库
     */
    public V2exJobSyncService(JobPostingRepository repository) {
        this.repository = repository; // 赋值仓库
        this.restClient = RestClient.builder() // 构建 RestClient
                .baseUrl("https://www.v2ex.com") // 设置基础 URL
                .defaultHeader("User-Agent", "java5-job-board/1.0") // 标识客户端身份
                .build(); // 构建实例
    }

    /**
     * 拉取 V2EX 酷工作节点最新主题，去重后入库
     * <p>
     * 职责：调用 V2EX API → 逐条去重 → 转换 → 容错入库 → 返回新增数。
     * </p>
     * 容错策略：
     * - API 调用失败：返回 0，不中断应用
     * - 单条入库失败：记录警告日志并跳过，继续处理后续主题
     * （曾因 responsibilities 字段超长导致 saveAll 整批回滚，已改为逐条 save）
     *
     * @return 新增的岗位数量（已去重）
     */
    public int syncLatest() {
        V2exTopic[] topics; // V2EX 主题数组
        try {
            topics = restClient.get()                    // 构造 GET 请求
                    .uri("/api/topics/show.json?node_name=jobs") // 拼接路径
                    .retrieve()                          // 发起请求
                    .body(V2exTopic[].class);            // 反序列化为数组
        } catch (Exception e) {
            // API 调用失败（网络/限流/超时）：降级返回 0，不抛异常
            log.warn("调用 V2EX API 失败: {}", e.getMessage());
            return 0; // 返回 0 表示未新增
        }
        // 空结果校验：API 返回 null 或空数组时直接返回
        if (topics == null || topics.length == 0) {
            return 0;
        }

        int inserted = 0; // 新增计数
        int failed = 0;   // 失败计数
        for (V2exTopic t : topics) {              // 遍历每条主题
            if (t.id() <= 0) {                    // ID 非法则跳过
                continue;
            }
            String externalId = String.valueOf(t.id()); // 转字符串作为去重键
            // 去重：已存在同来源同 externalId 的记录则跳过，避免重复入库
            if (repository.existsBySourceAndExternalId(JobSource.V2EX, externalId)) {
                continue;
            }
            JobPosting j = convert(t); // 将 V2EX 主题转换为实体
            if (j == null) {           // 转换失败（如标题为空）跳过
                continue;
            }
            // 逐条保存：单条失败不影响整体同步，避免整批回滚
            try {
                repository.save(j); // 持久化单条
                inserted++;         // 新增计数+1
            } catch (Exception e) {
                failed++;           // 失败计数+1
                log.warn("V2EX 主题入库失败 externalId={} title={} 原因={}",
                        externalId, j.getTitle(), e.getMessage()); // 记录失败详情
            }
        }
        log.info("V2EX 同步完成：共 {} 条主题，新增 {} 条，失败 {} 条",
                topics.length, inserted, failed); // 汇总日志
        return inserted; // 返回新增数
    }

    /**
     * 将 V2EX 主题转换为岗位实体
     * <p>
     * 职责：从 V2EX 主题中解析城市、薪资、分类等结构化字段，
     * V2EX 无结构化字段的部分用标题正则或作者信息占位。
     * </p>
     * 解析规则：
     * - 城市：标题 [xxx] 提取
     * - 薪资：标题 xx-xxK 提取
     * - 公司：V2EX 无结构化字段，用作者用户名占位
     * - 分类：标题关键词推断
     * - 职责：用主题正文 content 填充
     *
     * @param t V2EX 主题
     * @return 岗位实体，标题为空时返回 null
     */
    private JobPosting convert(V2exTopic t) {
        JobPosting j = new JobPosting();           // 新建实体
        j.setSource(JobSource.V2EX);               // 标记来源为 V2EX
        j.setExternalId(String.valueOf(t.id()));   // 主题 ID 作为去重键
        j.setExternalUrl(t.url());                 // 主题链接，供前端跳转原帖

        String title = t.title();                  // 取标题
        if (title == null || title.isBlank()) {    // 标题为空则无法解析
            return null;                           // 返回 null 让调用方跳过
        }
        j.setTitle(title.trim());                  // 去首尾空白后设置标题

        // 城市：[xxx] 提取，如 "[北京] 招聘" → "北京"
        Matcher cityMatcher = CITY_PATTERN.matcher(title); // 用预编译正则匹配
        if (cityMatcher.find()) {                  // 找到匹配
            j.setCity(cityMatcher.group(1).trim()); // 取第一捕获组并去空白
        }

        // 薪资：xx-xxK 提取，如 "30-60K"
        Matcher salaryMatcher = SALARY_PATTERN.matcher(title); // 匹配薪资
        if (salaryMatcher.find()) {                // 找到匹配
            try {
                int min = Integer.parseInt(salaryMatcher.group(1)); // 解析下限
                int max = Integer.parseInt(salaryMatcher.group(2)); // 解析上限
                j.setSalaryMin(min * 1000);        // K → 元
                j.setSalaryMax(max * 1000);        // K → 元
                j.setSalaryDesc(min + "-" + max + "K"); // 生成描述文本
            } catch (NumberFormatException ignored) {
                // 数字解析失败时忽略，薪资字段保持 null
            }
        }

        // 公司：标题里没有结构化公司名，暂用作者用户名占位
        if (t.member() != null && t.member().username() != null) {
            j.setCompany(t.member().username());   // 用作者用户名占位公司名
        }

        // 发布人姓名：V2EX 无结构化字段，用作者用户名占位
        if (t.member() != null && t.member().username() != null) {
            j.setPublisherName(t.member().username()); // 发布人用作者用户名
        }
        // 联系方式：V2EX API 无此字段，留空
        j.setContact(null);                        // 显式置空，避免歧义

        // 分类：按关键词推断
        j.setCategory(detectCategory(title));      // 调用分类推断方法

        // 详情正文：用 V2EX 主题 content 作为岗位职责
        if (t.content() != null && !t.content().isBlank()) {
            j.setResponsibilities(t.content());    // 设置职责（已扩容到 4000 字符）
        }

        // 发布日期：从 V2EX 时间戳转换，无时间戳则用当天
        if (t.created() > 0) {
            j.setPublishedDate(LocalDate.ofInstant( // 时间戳 → LocalDate
                    Instant.ofEpochSecond(t.created()), // 秒级时间戳 → Instant
                    ZoneId.of("Asia/Shanghai")));      // 用上海时区
        } else {
            j.setPublishedDate(LocalDate.now());   // 兜底用当天
        }

        return j; // 返回填充完毕的实体
    }

    /**
     * 根据标题关键词推断岗位分类
     * <p>
     * 职责：按关键词优先级匹配，命中即返回对应分类，
     * 都不命中则默认研发类（V2EX 酷工作以研发岗为主）。
     * </p>
     * 为什么先匹配算法/数据：这些关键词更具体，优先级高可避免被"开发"误判。
     *
     * @param title 岗位标题
     * @return 推断的分类枚举
     */
    private JobCategory detectCategory(String title) {
        String lower = title.toLowerCase(); // 转小写，统一匹配
        // 算法类：关键词优先级最高
        if (lower.contains("算法") || lower.contains("ai") || lower.contains("machine learning")) {
            return JobCategory.ALGORITHM; // 返回算法类
        }
        // 数据类
        if (lower.contains("数据") || lower.contains("data") || lower.contains("etl") || lower.contains("数仓")) {
            return JobCategory.DATA; // 返回数据类
        }
        // 运维类
        if (lower.contains("devops") || lower.contains("运维") || lower.contains("sre") || lower.contains("infra")) {
            return JobCategory.DEVOPS; // 返回运维类
        }
        // 测试类
        if (lower.contains("测试") || lower.contains("qa") || lower.contains("质量")) {
            return JobCategory.QA; // 返回测试类
        }
        // 设计类
        if (lower.contains("设计") || lower.contains("design") || lower.contains("ui") || lower.contains("ux")) {
            return JobCategory.DESIGN; // 返回设计类
        }
        // 产品类
        if (lower.contains("产品") || lower.contains("product") || lower.contains("pm")) {
            return JobCategory.PRODUCT; // 返回产品类
        }
        // 运营类
        if (lower.contains("运营") || lower.contains("operations") || lower.contains("运营")) {
            return JobCategory.OPERATIONS; // 返回运营类
        }
        return JobCategory.DEVELOPMENT; // 默认研发类（V2EX 酷工作以研发为主）
    }

    /**
     * V2EX 主题响应（仅保留需要的字段）
     * <p>
     * 为什么用 record：V2EX 返回字段很多，只映射需要的几个，
     * 配合 @JsonIgnoreProperties 忽略其余字段。
     * </p>
     */
    @JsonIgnoreProperties(ignoreUnknown = true) // 忽略 V2EX 返回的未映射字段
    public record V2exTopic(
            long id,                                      // 主题 ID（去重键）
            String title,                                 // 主题标题（解析城市/薪资/分类）
            String content,                               // 主题正文（作为岗位职责）
            String url,                                   // 主题链接
            @JsonProperty("created") long created,        // 创建时间戳（秒）
            @JsonProperty("last_modified") long lastModified, // 最后修改时间戳
            V2exMember member                             // 发布者信息
    ) {
    }

    /** V2EX 主题作者信息（仅保留用户名） */
    @JsonIgnoreProperties(ignoreUnknown = true) // 忽略其他作者字段
    public record V2exMember(String username) { // 仅映射 username
    }
}

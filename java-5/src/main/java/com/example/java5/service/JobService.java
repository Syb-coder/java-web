package com.example.java5.service; // 声明包路径，归属 service（业务服务层）

// ===== DTO 导入 =====
import com.example.java5.dto.JobPostingResponse; // 响应 DTO，返回给前端
import com.example.java5.dto.JobRequest;         // 请求 DTO，前端入参

// ===== 枚举类型导入 =====
import com.example.java5.model.EducationLevel;   // 学历要求枚举
import com.example.java5.model.ExperienceLevel;  // 经验要求枚举
import com.example.java5.model.JobCategory;      // 岗位分类枚举
import com.example.java5.model.JobSource;        // 数据来源枚举

// ===== 实体与仓库导入 =====
import com.example.java5.model.JobPosting;               // 招聘岗位实体
import com.example.java5.repository.JobPostingRepository; // JPA 仓库

// ===== Spring 注解 =====
import org.springframework.stereotype.Service; // 声明为 Spring Bean，业务层组件

// ===== JDK 工具导入 =====
import java.time.LocalDate;          // 日期类型，用于默认发布日期
import java.time.LocalDateTime;      // 日期时间类型，用于更新时间戳
import java.util.HashMap;            // 哈希表，用于统计结果
import java.util.List;               // 列表容器
import java.util.Map;                // 键值对容器
import java.util.stream.Collectors;  // Stream 收集器，用于分组统计

/**
 * 招聘岗位业务服务
 * <p>
 * 职责：封装岗位的查询、创建、更新、删除、统计等业务逻辑，
 * 作为 Controller 与 Repository 之间的中间层。
 * </p>
 * 设计要点：
 * 1. 通过构造函数注入 Repository，便于单元测试替换 Mock。
 * 2. 对外返回 DTO（JobPostingResponse），不暴露实体，保证数据边界清晰。
 * 3. create/update 复用 applyRequest 统一字段映射，避免重复代码。
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期
public class JobService {

    /** 岗位数据访问仓库，由 Spring 通过构造函数注入 */
    private final JobPostingRepository repository;

    /**
     * 构造函数注入 Repository
     * <p>
     * 为什么用构造函数注入而非 @Autowired 字段注入：
     * 1. 字段不可变（final），保证依赖在构造完成后不再被篡改；
     * 2. 无需 Spring 容器即可实例化，便于单元测试直接 new；
     * 3. 循环依赖在编译期即可暴露，而非运行时才发现。
     * </p>
     *
     * @param repository 岗位 JPA 仓库
     */
    public JobService(JobPostingRepository repository) {
        this.repository = repository; // 赋值给不可变字段
    }

    /**
     * 多条件搜索岗位列表
     * <p>
     * 职责：将前端筛选条件透传给 Repository 的自定义查询，
     * 再将实体结果批量转换为响应 DTO 返回。
     * </p>
     * 为什么不在 Service 拼接 SQL：Repository 的 @Query 已用 JPQL 封装，
     * Service 层只做编排，保持单一职责。
     *
     * @param keyword    关键词（匹配标题/公司/技能），可为 null
     * @param city       城市过滤，可为 null
     * @param category   分类过滤，可为 null
     * @param experience 经验过滤，可为 null
     * @param education  学历过滤，可为 null
     * @param salaryMin  薪资下限过滤，可为 null
     * @return 岗位响应 DTO 列表，按发布日期倒序
     */
    public List<JobPostingResponse> search(String keyword, String city,
                                           JobCategory category, ExperienceLevel experience,
                                           EducationLevel education, Integer salaryMin) {
        return repository.search(keyword, city, category, experience, education, salaryMin) // 调用仓库查询
                .stream()                       // 转为 Stream 进行流水线处理
                .map(JobPostingResponse::from)  // 每个实体转 DTO
                .collect(Collectors.toList());  // 收集为 List 返回
    }

    /**
     * 根据 ID 查询单个岗位
     * <p>
     * 职责：通过主键查询实体，存在则转 DTO，不存在返回 null。
     * </p>
     * 为什么返回 null 而非抛异常：前端列表页查询时容忍不存在，
     * 由 Controller 层决定是否转为 404。
     *
     * @param id 主键 ID
     * @return 岗位 DTO，不存在时为 null
     */
    public JobPostingResponse get(Long id) {
        return repository.findById(id)         // 按 ID 查询，返回 Optional
                .map(JobPostingResponse::from) // 存在则转 DTO
                .orElse(null);                 // 不存在返回 null
    }

    /**
     * 创建新岗位
     * <p>
     * 职责：将请求 DTO 映射到新实体，补全默认值后持久化。
     * 默认值策略：
     * - source：未指定则标记为 LOCAL（本地录入）
     * - publishedDate：未指定则取当天
     * </p>
     * 为什么补默认值放 Service 而非实体：实体保持纯数据，默认值属业务规则。
     *
     * @param req 创建请求 DTO
     * @return 已持久化的岗位 DTO（含生成的 ID）
     */
    public JobPostingResponse create(JobRequest req) {
        JobPosting j = new JobPosting(); // 新建空实体
        applyRequest(j, req);            // 填充业务字段（复用统一映射方法）
        // 数据来源：用户发布默认标记为本地数据，V2EX 同步走独立通道会显式设置
        if (j.getSource() == null) {
            j.setSource(JobSource.LOCAL); // 默认本地来源
        }
        // 发布日期：未指定则用当天，保证列表排序有值
        if (j.getPublishedDate() == null) {
            j.setPublishedDate(LocalDate.now()); // 取当前日期
        }
        return JobPostingResponse.from(repository.save(j)); // 持久化并返回 DTO
    }

    /**
     * 更新已有岗位
     * <p>
     * 职责：根据 ID 查找实体，存在则覆盖字段后保存，不存在返回 null。
     * </p>
     * 注意：此方法会覆盖全部可编辑字段（含发布人、联系方式），
     * 前端编辑时需传入完整数据。
     *
     * @param id  待更新岗位 ID
     * @param req 更新请求 DTO
     * @return 更新后的 DTO，不存在时为 null
     */
    public JobPostingResponse update(Long id, JobRequest req) {
        return repository.findById(id).map(j -> { // 查找实体，存在才更新
            applyRequest(j, req);                 // 覆盖业务字段
            j.setUpdateTime(LocalDateTime.now());
            return JobPostingResponse.from(repository.save(j)); // 保存并转 DTO
        }).orElse(null);                          // 不存在返回 null
    }

    /**
     * 删除岗位
     * <p>
     * 职责：按 ID 删除岗位，返回是否删除成功。
     * </p>
     * 为什么先 existsById 再 delete：避免删除不存在 ID 时 JPA 抛 EmptyResultDataAccessException，
     * 同时返回布尔值让 Controller 决定响应 404 还是 200。
     *
     * @param id 待删除岗位 ID
     * @return true=删除成功，false=ID 不存在
     */
    public boolean delete(Long id) {
        if (repository.existsById(id)) { // 先确认存在
            repository.deleteById(id);   // 执行删除
            return true;                 // 返回成功
        }
        return false;                    // ID 不存在，返回失败
    }

    /**
     * 生成首页统计数据
     * <p>
     * 职责：全量查询岗位后，按分类、城市、来源维度分组统计，
     * 供前端首页仪表盘展示。
     * </p>
     * 性能说明：当前为内存分组，数据量大时建议改为数据库 GROUP BY。
     * 数据量 < 1000 时内存方式足够。
     *
     * @return 统计结果 Map，含 total/byCategory/byCity/bySource 四个键
     */
    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();   // 用 Map 承载多维度统计
        List<JobPosting> all = repository.findAll();   // 全量加载（数据量小可接受）
        stats.put("total", all.size());                // 总数

        // 按分类统计：过滤掉无分类的数据，按中文 label 分组计数
        Map<String, Long> byCategory = all.stream()
                .filter(j -> j.getCategory() != null)  // 排除无分类数据
                .collect(Collectors.groupingBy(        // 分组统计
                        j -> j.getCategory().getLabel(), // 按中文标签分组
                        Collectors.counting()));         // 计数
        stats.put("byCategory", byCategory);           // 存入结果

        // 按城市统计：过滤掉空城市
        Map<String, Long> byCity = all.stream()
                .filter(j -> j.getCity() != null && !j.getCity().isBlank()) // 排除空/空白城市
                .collect(Collectors.groupingBy(
                        JobPosting::getCity,           // 按城市名分组
                        Collectors.counting()));         // 计数
        stats.put("byCity", byCity);                   // 存入结果

        // 按数据来源统计：区分本地与 V2EX
        Map<String, Long> bySource = all.stream()
                .filter(j -> j.getSource() != null)    // 排除无来源数据
                .collect(Collectors.groupingBy(
                        j -> j.getSource().getLabel(), // 按中文标签分组
                        Collectors.counting()));         // 计数
        stats.put("bySource", bySource);               // 存入结果

        return stats; // 返回完整统计 Map
    }

    /**
     * 将请求 DTO 字段映射到实体（create/update 复用）
     * <p>
     * 职责：把 JobRequest 的所有字段逐一 set 到 JobPosting 实体，
     * 保证创建和更新走同一套字段映射逻辑，避免遗漏。
     * </p>
     * 为什么不直接 BeanUtils.copyProperties：record 的访问器是 title() 而非 getTitle()，
     * 反射拷贝不兼容；显式 set 虽然啰嗦但类型安全、可读性强。
     *
     * @param j   目标实体（已实例化）
     * @param req 源请求 DTO
     */
    private void applyRequest(JobPosting j, JobRequest req) {
        j.setTitle(req.title());                       // 标题
        j.setCompany(req.company());                   // 公司
        j.setCity(req.city());                         // 城市
        j.setCategory(req.category());                 // 分类
        j.setExperience(req.experience());             // 经验
        j.setEducation(req.education());               // 学历
        j.setSalaryMin(req.salaryMin());               // 薪资下限
        j.setSalaryMax(req.salaryMax());               // 薪资上限
        j.setSalaryDesc(req.salaryDesc());             // 薪资描述
        j.setSkills(req.skills());                     // 技能标签
        j.setResponsibilities(req.responsibilities()); // 岗位职责
        j.setRequirements(req.requirements());         // 任职要求
        j.setBenefits(req.benefits());                 // 福利
        j.setCompanyDesc(req.companyDesc());           // 公司简介
        j.setPublishedDate(req.publishedDate());       // 发布日期
        j.setPublisherName(req.publisherName());       // 发布人姓名（本次新增）
        j.setContact(req.contact());                   // 联系方式（本次新增）
    }
}

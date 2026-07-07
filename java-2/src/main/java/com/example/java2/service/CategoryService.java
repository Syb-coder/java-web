// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.CategoryRequest;
import com.example.java2.dto.CategoryResponse;
import com.example.java2.model.Category;
import com.example.java2.repository.ArticleRepository;
import com.example.java2.repository.CategoryRepository;
import com.example.java2.repository.QuestionRepository;

// 导入 Spring 注解
import org.springframework.stereotype.Service;

// 导入集合类
import java.util.List;

/**
 * 文章分类业务服务
 * <p>
 * 负责分类的增删改查。删除分类前会校验该分类下是否仍有文章或题目占用，
 * 避免出现孤儿数据。
 * </p>
 */
@Service
public class CategoryService {

    // final 修饰：三个依赖在构造后不可变，确保多线程环境下的安全发布与不可变约束
    private final CategoryRepository repository;
    private final ArticleRepository articleRepository;
    private final QuestionRepository questionRepository;

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：构造注入可保证依赖完整后对象才可用，且便于单元测试 mock
     */
    public CategoryService(CategoryRepository repository,
                           ArticleRepository articleRepository,
                           QuestionRepository questionRepository) {
        this.repository = repository;
        this.articleRepository = articleRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * 查询全部分类（按 sortOrder 升序）
     *
     * @param withArticleCount 是否附带文章数（前台导览需要，后台列表不需要）
     * @return 分类响应列表
     */
    public List<CategoryResponse> listAll(boolean withArticleCount) {
        // 按 sortOrder 升序保证前台导览展示顺序稳定可控
        List<Category> categories = repository.findAllByOrderBySortOrderAsc();
        if (!withArticleCount) {
            // 后台列表无需文章数，跳过 count 查询减少 N 次额外 SQL
            return categories.stream().map(CategoryResponse::from).toList();
        }
        // 前台需要展示文章数：逐分类 count，数据量可控场景下可接受
        return categories.stream()
                .map(c -> CategoryResponse.from(c, articleRepository.countByCategoryId(c.getId())))
                .toList();
    }

    /**
     * 根据 ID 查询分类
     */
    public Category getById(Long id) {
        // orElse(null) 便于调用方做"存在性判断"分支处理
        return repository.findById(id).orElse(null);
    }

    /**
     * 根据 ID 查询分类名称（用于文章列表填充分类名）
     *
     * @param id 分类 ID
     * @return 分类名称，不存在返回 null
     */
    public String getNameById(Long id) {
        // 复用 getById 避免重复查询逻辑；返回 null 让调用方决定如何展示（如显示"未分类"）
        Category c = getById(id);
        return c != null ? c.getName() : null;
    }

    /**
     * 新增分类
     *
     * @param req 分类请求
     * @return 新建分类响应
     * @throws IllegalArgumentException 分类名已存在
     */
    public CategoryResponse create(CategoryRequest req) {
        // 名称唯一前置校验，避免依赖 DB 唯一约束抛异常导致流程中断
        // 抛 IllegalArgumentException：参数层面的命名冲突，属"调用方传错"语义
        if (repository.existsByName(req.name())) {
            throw new IllegalArgumentException("分类名已存在");
        }
        Category category = new Category(req.name(), req.description(), req.sortOrder());
        repository.save(category);
        return CategoryResponse.from(category);
    }

    /**
     * 更新分类
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 更新后的分类响应
     * @throws IllegalArgumentException 分类不存在或名称冲突
     */
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 仅当名称变更时才校验冲突，避免"未改名"也被判为重复
        if (!category.getName().equals(req.name()) && repository.existsByName(req.name())) {
            throw new IllegalArgumentException("分类名已存在");
        }
        category.setName(req.name());
        category.setDescription(req.description());
        category.setSortOrder(req.sortOrder());
        repository.save(category);
        return CategoryResponse.from(category);
    }

    /**
     * 删除分类
     *
     * <p>业务规则：分类下仍有文章或题目时不允许删除，避免孤儿数据。</p>
     *
     * @param id 分类 ID
     * @throws IllegalStateException 分类下仍存在文章或题目
     */
    public void delete(Long id) {
        // 引用完整性校验：分类下有文章则禁止删除，避免文章 categoryId 成为孤儿外键
        // 抛 IllegalStateException 而非 IllegalArgumentException：分类本身存在、参数合法，
        //   仅因"业务状态不允许删除"，与参数错误语义区分；全局异常处理器据此转 409 而非 400
        if (articleRepository.countByCategoryId(id) > 0) {
            throw new IllegalStateException("该分类下仍有文章，无法删除");
        }
        // 题目也引用分类，需同步校验避免题库分类丢失
        if (questionRepository.countByCategoryId(id) > 0) {
            throw new IllegalStateException("该分类下仍有题目，无法删除");
        }
        repository.deleteById(id);
    }

    /**
     * 统计分类数（仪表板用）
     */
    public long count() {
        // 走 DB count 聚合，仪表板只展示数字无需实体
        return repository.count();
    }
}

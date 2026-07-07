// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.QuestionForTest;
import com.example.java2.dto.QuestionRequest;
import com.example.java2.dto.QuestionResponse;
import com.example.java2.model.Question;
import com.example.java2.repository.QuestionRepository;

// 导入 Spring 注解
import org.springframework.stereotype.Service;

// 导入集合与并发工具
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目业务服务
 * <p>
 * 负责题库 CRUD、按分类抽题（前台自测用）。
 * 抽题采用 Collections.shuffle 随机化顺序，避免用户每次答题都遇到相同题序。
 * </p>
 */
@Service
public class QuestionService {

    /** 单次自测默认抽题数量 */
    // 命名常量而非魔法值：避免散落在代码中的 5 难以维护，调整只需改一处
    private static final int DEFAULT_TEST_SIZE = 5;

    // final 修饰：依赖在构造后不可变，确保线程安全发布与不可变约束
    private final QuestionRepository repository;
    private final CategoryService categoryService;

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：构造注入可声明 final、便于单元测试 mock、启动期即可发现循环依赖
     */
    public QuestionService(QuestionRepository repository, CategoryService categoryService) {
        this.repository = repository;
        this.categoryService = categoryService;
    }

    /**
     * 按分类查询全部题目（后台题库管理用）
     *
     * @param categoryId 分类 ID（null 表示全部）
     * @return 题目响应列表
     */
    public List<QuestionResponse> listAll(Long categoryId) {
        List<Question> list = (categoryId == null)
                ? repository.findAll()
                : repository.findByCategoryId(categoryId);
        return list.stream()
                .map(q -> QuestionResponse.from(q, categoryService.getNameById(q.getCategoryId())))
                .toList();
    }

    /**
     * 根据 ID 查询题目实体
     */
    public Question getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 新增题目
     *
     * @param req 题目请求
     * @return 新建题目响应
     * @throws IllegalArgumentException 分类不存在
     */
    public QuestionResponse create(QuestionRequest req) {
        if (categoryService.getById(req.categoryId()) == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        Question question = new Question(
                req.categoryId(),
                req.stem(),
                req.optionA(),
                req.optionB(),
                req.optionC(),
                req.optionD(),
                req.answerIndex()
        );
        if (req.score() != null) {
            question.setScore(req.score());
        }
        question.setAnalysis(req.analysis());
        repository.save(question);
        return QuestionResponse.from(question, categoryService.getNameById(question.getCategoryId()));
    }

    /**
     * 更新题目
     *
     * @param id  题目 ID
     * @param req 题目请求
     * @return 更新后的题目响应
     * @throws IllegalArgumentException 题目或分类不存在
     */
    public QuestionResponse update(Long id, QuestionRequest req) {
        Question question = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("题目不存在"));
        if (categoryService.getById(req.categoryId()) == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        question.setCategoryId(req.categoryId());
        question.setStem(req.stem());
        question.setOptionA(req.optionA());
        question.setOptionB(req.optionB());
        question.setOptionC(req.optionC());
        question.setOptionD(req.optionD());
        question.setAnswerIndex(req.answerIndex());
        if (req.score() != null) {
            question.setScore(req.score());
        }
        question.setAnalysis(req.analysis());
        repository.save(question);
        return QuestionResponse.from(question, categoryService.getNameById(question.getCategoryId()));
    }

    /**
     * 删除题目
     *
     * @param id 题目 ID
     * @throws IllegalArgumentException 题目不存在
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("题目不存在");
        }
        repository.deleteById(id);
    }

    /**
     * 按分类随机抽题（前台自测用）
     * <p>categoryId 为 0 表示跨分类综合测试。</p>
     *
     * @param categoryId 分类 ID
     * @param size       抽题数量（0 或负数则使用默认值 5）
     * @return 答题用题目列表（不含正确答案）
     */
    public List<QuestionForTest> pickForTest(Long categoryId, int size) {
        int targetSize = size <= 0 ? DEFAULT_TEST_SIZE : size;
        List<Question> pool = (categoryId == null || categoryId == 0L)
                ? repository.findAll()
                : repository.findByCategoryId(categoryId);
        // 打乱顺序后截取前 N 道，实现随机抽题
        // 为何 shuffle：① 防止用户背诵题序而非真正掌握知识（刷题作弊）；
        //              ② 同一用户多次自测题目顺序不同，提升练习覆盖度与记忆效果
        // Collections.shuffle 内部用 ThreadLocalRandom（Java 8+），线程安全且性能优于 Random
        Collections.shuffle(pool);
        return pool.stream()
                .limit(targetSize)
                // QuestionForTest.from 不含 answerIndex，避免答案泄露给前端
                .map(QuestionForTest::from)
                .collect(Collectors.toList());
    }

    /**
     * 统计题目总数（仪表板用）
     */
    public long count() {
        return repository.count();
    }
}

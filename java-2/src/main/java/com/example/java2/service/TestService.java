// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.SubmitAnswerRequest;
import com.example.java2.dto.TestRecordResponse;
import com.example.java2.dto.TestResultResponse;
import com.example.java2.model.Question;
import com.example.java2.model.TestRecord;
import com.example.java2.repository.QuestionRepository;
import com.example.java2.repository.TestRecordRepository;

// 导入 Spring 分页与注解
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// 导入集合与 JSON 处理类
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自测答题业务服务
 * <p>
 * 负责：① 生成自测题目（委托 {@link QuestionService}）；
 *      ② 接收用户答案并判分；
 *      ③ 持久化答题记录；
 *      ④ 查询用户答题历史。
 * </p>
 */
@Service
public class TestService {

    // final 修饰：依赖在构造后不可变，确保线程安全发布与不可变约束
    private final QuestionRepository questionRepository;
    private final TestRecordRepository recordRepository;
    private final CategoryService categoryService;

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：构造注入可声明 final、便于单元测试 mock、启动期即可发现循环依赖
     */
    public TestService(QuestionRepository questionRepository,
                       TestRecordRepository recordRepository,
                       CategoryService categoryService) {
        this.questionRepository = questionRepository;
        this.recordRepository = recordRepository;
        this.categoryService = categoryService;
    }

    /**
     * 判分并保存答题记录
     *
     * @param userId 用户 ID
     * @param req    答题提交请求
     * @return 答题结果响应
     */
    public TestResultResponse submit(Long userId, SubmitAnswerRequest req) {
        Map<String, Integer> answers = req.answers();
        int totalCount = answers.size();
        int correctCount = 0;
        int score = 0;
        int totalScore = 0;
        List<TestResultResponse.QuestionDetail> details = new ArrayList<>();
        // 用于持久化的 JSON 字符串
        // 为何手写 JSON 而非用 Jackson：本场景仅是简单的 {id:answer} 键值对，
        //   手写拼接比引入 ObjectMapper 序列化更轻量，避免为单字段引入额外依赖与反射开销
        // 安全性说明：questionId 为 Long 型已校验，userAnswer 为 Integer，无字符串注入风险
        StringBuilder answersJson = new StringBuilder("{");

        boolean first = true;
        for (Map.Entry<String, Integer> entry : answers.entrySet()) {
            Long questionId;
            try {
                questionId = Long.parseLong(entry.getKey());
            } catch (NumberFormatException e) {
                // 跳过非法的题目 ID
                // 静默跳过而非抛异常：保证单题异常不影响整体判分流程，最大化可用性
                continue;
            }
            Question question = questionRepository.findById(questionId).orElse(null);
            if (question == null) {
                // 题目可能已被删除，跳过不参与判分，避免抛异常中断整个答题结果
                continue;
            }
            int userAnswer = entry.getValue();
            int correctAnswer = question.getAnswerIndex();
            boolean isCorrect = (userAnswer == correctAnswer);
            if (isCorrect) {
                correctCount++;
                score += question.getScore();
            }
            totalScore += question.getScore();
            details.add(new TestResultResponse.QuestionDetail(
                    questionId, userAnswer, correctAnswer, isCorrect, question.getAnalysis()
            ));
            // 拼接 JSON，键为题目ID，值为用户答案序号
            if (!first) {
                answersJson.append(",");
            }
            answersJson.append("\"").append(questionId).append("\":").append(userAnswer);
            first = false;
        }
        answersJson.append("}");

        // 持久化答题记录
        TestRecord record = new TestRecord(
                userId,
                req.categoryId(),
                totalCount,
                correctCount,
                score,
                answersJson.toString()
        );
        recordRepository.save(record);

        return new TestResultResponse(
                totalCount,
                correctCount,
                score,
                totalScore,
                details,
                record.getId()
        );
    }

    /**
     * 查询用户答题历史（个人中心用）
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页条数
     * @return 答题记录分页
     */
    public Page<TestRecordResponse> listByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recordRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable)
                .map(r -> TestRecordResponse.from(r, categoryService.getNameById(r.getCategoryId())));
    }

    /**
     * 统计全平台答题次数（仪表板用）
     */
    public long count() {
        return recordRepository.count();
    }
}

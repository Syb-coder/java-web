// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Question;

/**
 * 答题用题目 DTO（前台自测展示）
 * <p>
 * 不含正确答案序号与解析，避免用户作答前看到答案。
 * 提交后由后端判分并返回 {@link TestResultResponse}。
 * </p>
 *
 * @param id       题目 ID
 * @param stem     题干
 * @param optionA  选项 A
 * @param optionB  选项 B
 * @param optionC  选项 C
 * @param optionD  选项 D
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；刻意省略 answerIndex 与 analysis 字段，防止用户作答前看到答案
public record QuestionForTest(
        Long id,      // 题目主键 ID，提交答案时作为 key 回传
        String stem,  // 题干文本
        String optionA,// 选项 A
        String optionB,// 选项 B
        String optionC,// 选项 C
        String optionD // 选项 D
) {
    /**
     * 由题目实体构造答题用 DTO
     *
     * @param question 题目实体
     * @return 答题用 DTO
     */
    // 使用静态工厂方法 from()：仅提取作答所需字段，过滤掉答案与解析，保证敏感信息不下发到前端
    public static QuestionForTest from(Question question) {
        return new QuestionForTest(
                question.getId(),
                question.getStem(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        );
    }
}

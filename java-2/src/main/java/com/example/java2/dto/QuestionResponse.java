// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Question;

/**
 * 题目响应 DTO（管理端列表展示）
 * <p>
 * 包含题目全部字段，用于后台题库管理。同时附带分类名称（由 Service 层查询后传入）。
 * </p>
 *
 * @param id           题目 ID
 * @param categoryId   所属分类 ID
 * @param categoryName 所属分类名称
 * @param stem         题干
 * @param optionA      选项 A
 * @param optionB      选项 B
 * @param optionC      选项 C
 * @param optionD      选项 D
 * @param answerIndex  正确答案序号
 * @param score        题目分值
 * @param analysis     题目解析
 * @param createTime   创建时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；管理端专用，包含正确答案与解析
public record QuestionResponse(
        Long id,           // 题目主键 ID
        Long categoryId,   // 所属分类 ID
        String categoryName,// 所属分类名称，由 Service 层关联查询后传入
        String stem,       // 题干文本
        String optionA,    // 选项 A
        String optionB,    // 选项 B
        String optionC,    // 选项 C
        String optionD,    // 选项 D
        int answerIndex,   // 正确答案序号 0~3，对应 A/B/C/D
        int score,         // 题目分值
        String analysis,   // 题目解析，可为 null
        String createTime  // 创建时间字符串
) {
    /**
     * 由题目实体构造响应
     *
     * @param question     题目实体
     * @param categoryName 分类名称
     * @return 题目响应
     */
    // 使用静态工厂方法 from()：分类名需外部传入，工厂方法集中处理实体到 DTO 的字段映射
    public static QuestionResponse from(Question question, String categoryName) {
        return new QuestionResponse(
                question.getId(),
                question.getCategoryId(),
                categoryName,
                question.getStem(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getAnswerIndex(),
                question.getScore(),
                question.getAnalysis(),
                question.getCreateTime() != null ? question.getCreateTime().toString() : null
        );
    }
}

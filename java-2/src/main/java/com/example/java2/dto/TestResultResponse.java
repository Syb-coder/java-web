// 声明包路径
package com.example.java2.dto;

// 导入集合类
import java.util.List;
import java.util.Map;

/**
 * 答题结果响应 DTO
 * <p>
 * 后端判分后返回，包含本次得分、对错统计、每题的判分细节。
 * </p>
 *
 * @param totalCount        总题数
 * @param correctCount      答对题数
 * @param score             本次得分
 * @param totalScore        满分
 * @param details           每题判分细节
 * @param recordId          答题记录 ID（用于个人中心查询历史）
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；后端判分后返回，包含对错统计与逐题判分细节
public record TestResultResponse(
        int totalCount,                // 总题数
        int correctCount,              // 答对题数
        int score,                     // 本次得分（各答对题目分值之和）
        int totalScore,                // 满分（所有题目分值之和）
        List<QuestionDetail> details,  // 每题判分细节列表，前端据此展示对错与解析
        Long recordId                  // 答题记录 ID，用于个人中心查询历史详情
) {
    /**
     * 单题判分细节
     *
     * @param questionId   题目 ID
     * @param userAnswer   用户答案序号（0~3）
     * @param correctAnswer 正确答案序号
     * @param correct      是否答对
     * @param analysis     题目解析
     */
    // 嵌套 record：作为 TestResultResponse 的内部成员，封装单题判分结果，与外层 DTO 共享生命周期
    public record QuestionDetail(
            Long questionId,    // 题目 ID，便于前端定位题目
            int userAnswer,     // 用户作答的选项序号 0~3
            int correctAnswer,  // 正确答案序号 0~3，作答后下发用于对照
            boolean correct,    // 是否答对，前端据此渲染对错图标
            String analysis     // 题目解析，答错时重点展示
    ) {
    }
}

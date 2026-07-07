// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotNull;

// 导入集合类
import java.util.Map;

/**
 * 答题提交请求 DTO
 * <p>
 * 前台用户提交自测答案时使用。answers 字段为"题目ID → 用户答案序号"映射，
 * 键为题目 ID（字符串形式，便于 JSON 反序列化），值为 0~3。
 * </p>
 *
 * @param categoryId 本次答题分类 ID（0 表示跨分类综合测试）
 * @param answers    答题映射：题目ID -> 用户答案序号
 */
// 采用 record 声明：不可变请求 DTO，自动生成 accessor/equals/hashCode/toString，作为答题提交入参
public record SubmitAnswerRequest(
        // @NotNull 拒绝 null；0 表示跨分类综合测试，是合法值，故不能用 @NotBlank（仅适用于 String）
        @NotNull(message = "分类不能为空") Long categoryId,
        // @NotNull 拒绝 null；Map 键为题目 ID 字符串（JSON 反序列化时数字键自动转字符串），值为 0~3 的答案序号
        @NotNull(message = "答案不能为空") Map<String, Integer> answers
) {
}

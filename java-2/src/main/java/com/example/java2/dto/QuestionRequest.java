// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 题目新增/编辑请求 DTO
 * <p>
 * 后台管理员维护题库时提交的数据。正确答案序号范围 0~3，分别对应 A/B/C/D。
 * </p>
 *
 * @param categoryId  所属分类 ID
 * @param stem        题干
 * @param optionA     选项 A
 * @param optionB     选项 B
 * @param optionC     选项 C
 * @param optionD     选项 D
 * @param answerIndex 正确答案序号（0~3）
 * @param score       题目分值
 * @param analysis    题目解析（可选）
 */
// 采用 record 声明：不可变请求 DTO，自动生成 accessor/equals/hashCode/toString，作为题目新增/编辑入参
public record QuestionRequest(
        // @NotNull 拒绝 null，题目必须归属某个分类
        @NotNull(message = "分类不能为空") Long categoryId,
        // 题干为必填项
        @NotBlank(message = "题干不能为空") String stem,
        // 四个选项均必填，保证题目完整性
        @NotBlank(message = "选项A不能为空") String optionA,
        @NotBlank(message = "选项B不能为空") String optionB,
        @NotBlank(message = "选项C不能为空") String optionC,
        @NotBlank(message = "选项D不能为空") String optionD,
        // @NotNull 拒绝 null，@Min/@Max 限定答案序号范围 0~3，分别对应 A/B/C/D
        @NotNull(message = "答案序号不能为空")
        @Min(value = 0, message = "答案序号最小为0")
        @Max(value = 3, message = "答案序号最大为3") Integer answerIndex,
        // 分值为可选字段，未传时由后端按默认值处理
        Integer score,
        // 解析为可选字段，作答后展示
        String analysis
) {
}

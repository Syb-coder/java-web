// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文章新增/编辑请求 DTO
 * <p>
 * 后台管理员创建或更新文章时提交的数据。
 * </p>
 *
 * @param categoryId 所属分类 ID
 * @param title      文章标题
 * @param summary    文章摘要
 * @param content    文章正文
 * @param published  是否发布
 */
// 采用 record 声明：不可变请求 DTO，自动生成 accessor/equals/hashCode/toString，作为文章新增/编辑入参
public record ArticleRequest(
        // @NotNull 用于包装类型 Long，仅拒绝 null；分类 ID 必须显式指定，文章不能脱离分类存在
        @NotNull(message = "分类不能为空") Long categoryId,
        // @NotBlank 拒绝 null 与空白串，标题为文章必填项
        @NotBlank(message = "标题不能为空") String title,
        // 摘要为可选字段，未填写时后端可由正文截取生成
        String summary,
        // 正文为必填项，不能为空白
        @NotBlank(message = "正文不能为空") String content,
        // 使用包装类型 Boolean 而非 boolean：允许前端不传该字段，由后端按业务默认值处理
        Boolean published
) {
}

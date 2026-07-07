// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分类新增/编辑请求 DTO
 * <p>
 * 后台管理员创建或更新文章分类时提交的数据。
 * </p>
 *
 * @param name        分类名称
 * @param description 分类描述
 * @param sortOrder   排序序号
 */
// 采用 record 声明：不可变请求 DTO，自动生成 accessor/equals/hashCode/toString，作为分类新增/编辑入参
public record CategoryRequest(
        // @NotBlank 拒绝 null 与空白串；分类名称为分类的唯一标识，必须显式提供
        @NotBlank(message = "分类名称不能为空") String name,
        // 描述为可选字段，可为 null
        String description,
        // @NotNull 仅拒绝 null，允许 0（排序序号 0 是合法值）；区别于 @NotBlank 仅适用于字符串
        @NotNull(message = "排序序号不能为空") Integer sortOrder
) {
}

// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入 @NotNull 校验注解，约束非 null（ID 为 Long 包装类型，需显式校验）
import jakarta.validation.constraints.NotNull;

/**
 * 借书请求 DTO
 *
 * @param bookId   图书 ID
 * @param readerId 读者 ID
 * @param remark   备注（可选）
 */
public record BorrowRequest(
        @NotNull(message = "图书 ID 不能为空") Long bookId, // @NotNull 防止前端漏传图书 ID 导致空指针，Long 包装类型允许 null 需显式校验
        @NotNull(message = "读者 ID 不能为空") Long readerId, // 同上，读者 ID 必填以关联借阅记录
        String remark // 备注可选，读者可填写借阅用途（如课程参考书），不强制
) {
}

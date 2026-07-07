// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入 @NotNull 校验注解，约束非 null（ID 为 Long 包装类型，需显式校验）
import jakarta.validation.constraints.NotNull;

/**
 * 续借请求 DTO
 *
 * @param recordId 借阅记录 ID
 */
public record RenewRequest(
        @NotNull(message = "借阅记录 ID 不能为空") Long recordId // @NotNull 防止漏传记录 ID 导致无法定位续借对象，Long 包装类型允许 null 需显式校验
) {
}

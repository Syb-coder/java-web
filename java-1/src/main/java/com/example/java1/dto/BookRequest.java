// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入 @Min 校验注解，约束数值字段下限
import jakarta.validation.constraints.Min;
// 导入 @NotBlank 校验注解，约束字符串非 null 且去空白后非空
import jakarta.validation.constraints.NotBlank;
// 导入 @NotNull 校验注解，仅约束非 null（允许空字符串）
import jakarta.validation.constraints.NotNull;

/**
 * 图书新增/修改请求 DTO
 * <p>
 * 接收前端提交的图书数据，使用 JSR-380 校验注解保证数据合法性。
 * </p>
 *
 * @param title            书名
 * @param author           作者
 * @param isbn             ISBN（可选）
 * @param category         分类
 * @param publisher        出版社（可选）
 * @param publishYear      出版年份（可选）
 * @param description      简介（可选）
 * @param totalCopies      总副本数
 * @param availableCopies  可借副本数
 * @param location         存放位置（可选）
 */
// 使用 record 关键字（JDK 16+）定义不可变 DTO，自动生成构造方法/getter/equals/hashCode
public record BookRequest(
        @NotBlank(message = "书名不能为空") String title, // @NotBlank 而非 @NotNull：书名不允许纯空白字符串，强制前端提交有效文本
        @NotBlank(message = "作者不能为空") String author, // 作者为图书检索关键字，必填且非空白
        String isbn, // ISBN 可选，部分老旧图书无 ISBN 编号
        String category, // 分类可选，但前端通常下拉选择，此处不强制以兼容旧数据
        String publisher, // 出版社可选，不影响借阅核心流程
        Integer publishYear, // 出版年份可选，使用包装类型允许 null（未录入年份）
        String description, // 简介可选，避免新书入库时强制填写增加管理负担
        @NotNull(message = "总副本数不能为空") @Min(value = 0, message = "总副本数不能为负") Integer totalCopies, // @NotNull 保证录入，@Min(0) 防止负数导致库存逻辑错乱
        @NotNull(message = "可借副本数不能为空") @Min(value = 0, message = "可借副本数不能为负") Integer availableCopies, // 同样非空且非负，且业务层会校验 ≤ totalCopies
        String location // 存放位置可选，便于线下找书但不影响借阅流程
) {
}

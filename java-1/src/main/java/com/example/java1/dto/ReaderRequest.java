// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入读者类型枚举（STUDENT / TEACHER），决定借阅限额与借期
import com.example.java1.model.ReaderType;
// 导入 @NotBlank 校验注解，约束字符串非 null 且去空白后非空
import jakarta.validation.constraints.NotBlank;
// 导入 @NotNull 校验注解，约束非 null（枚举类型需显式校验）
import jakarta.validation.constraints.NotNull;

/**
 * 读者新增/修改请求 DTO
 *
 * @param readerNo   学号/工号
 * @param password   明文密码（创建时必填，修改时可选）
 * @param name       姓名
 * @param type       读者类型
 * @param department 院系（可选）
 * @param phone      电话（可选）
 */
public record ReaderRequest(
        @NotBlank(message = "学号/工号不能为空") String readerNo, // @NotBlank 强制学号/工号非空，作为读者唯一标识用于登录与唯一性校验
        String password, // 密码不强制：创建时必填但修改时可选，故不在 DTO 层加 @NotBlank，业务层按场景校验
        @NotBlank(message = "姓名不能为空") String name, // @NotBlank 强制姓名非空，读者姓名为借阅记录展示关键字段
        @NotNull(message = "读者类型不能为空") ReaderType type, // @NotNull 强制读者类型非空，决定借阅限额（学生 5/教师 10）与借期（30/60 天）
        String department, // 院系可选，不影响核心借阅流程
        String phone // 电话可选，便于联系但非必填
) {
}

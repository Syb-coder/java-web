// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 商品审核请求 DTO
 * <p>
 * 管理员审核学生发布的商品时提交，APPROVED 通过则上架，REJECTED 驳回需填写备注。
 * </p>
 */
public class AuditRequest {

    // 审核结果字段，必填；取值 APPROVED（通过）或 REJECTED（驳回）
    /** 审核结果：APPROVED / REJECTED */
    @NotBlank(message = "审核结果不能为空")
    private String status;

    // 审核备注字段，可选，驳回时必填说明原因；限制最长 500 字
    /** 审核备注（驳回时必填） */
    // Size：限定审核备注最长 500 字符
    @Size(max = 500, message = "审核备注不能超过 500 字")
    private String remark;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取审核结果
    public String getStatus() {
        return status;
    }

    // 设置审核结果
    public void setStatus(String status) {
        this.status = status;
    }

    // 获取审核备注
    public String getRemark() {
        return remark;
    }

    // 设置审核备注
    public void setRemark(String remark) {
        this.remark = remark;
    }
}

// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 纠纷处理请求 DTO
 * <p>
 * 管理员介入处理订单纠纷时提交处理备注，记录纠纷处理过程与结论。
 * </p>
 */
public class DisputeRequest {

    // 纠纷处理备注字段，必填，限制最长 500 字
    /** 纠纷处理备注 */
    @NotBlank(message = "处理备注不能为空")
    // Size：限定备注最长 500 字符
    @Size(max = 500, message = "备注不能超过 500 字")
    private String remark;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取纠纷处理备注
    public String getRemark() {
        return remark;
    }

    // 设置纠纷处理备注
    public void setRemark(String remark) {
        this.remark = remark;
    }
}

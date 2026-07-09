package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

/**
 * 风控处理请求 DTO
 * <p>
 * 风控人员对触发风控规则的记录进行处理，
 * action 取值 HANDLE（处理）/IGNORE（忽略）。
 * </p>
 */
public class RiskHandleRequest {  // 风控处理请求 DTO 类定义

    /** 风控记录 ID */
    @NotNull(message = "风控记录 ID 不能为空")  // 非 null 校验:风控记录 ID 必填
    private Long id;  // 待处理的风控记录 ID

    /** 处理动作：HANDLE/IGNORE */
    @NotBlank(message = "处理动作不能为空")  // 非空字符串校验:处理动作必填
    private String action;  // 处理动作:HANDLE(处理)/IGNORE(忽略)

    /** 处理备注（处理说明） */
    private String handleRemark;  // 处理备注,说明处理依据或结论

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取风控记录 ID
        return id;  // 返回风控记录 ID
    }

    public void setId(Long id) {  // 设置风控记录 ID
        this.id = id;  // 赋值风控记录 ID
    }

    // —— action 字段的 getter/setter ——
    public String getAction() {  // 获取处理动作
        return action;  // 返回处理动作
    }

    public void setAction(String action) {  // 设置处理动作
        this.action = action;  // 赋值处理动作
    }

    // —— handleRemark 字段的 getter/setter ——
    public String getHandleRemark() {  // 获取处理备注
        return handleRemark;  // 返回处理备注
    }

    public void setHandleRemark(String handleRemark) {  // 设置处理备注
        this.handleRemark = handleRemark;  // 赋值处理备注
    }
}

package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解

/**
 * 工单创建请求 DTO
 * <p>
 * C 端用户或运营人员创建客服工单，提交标题与详细描述。
 * </p>
 */
public class TicketRequest {  // 工单创建请求 DTO 类定义

    /** 工单标题 */
    @NotBlank(message = "工单标题不能为空")  // 非空字符串校验:工单标题必填
    private String title;  // 工单标题(简述问题)

    /** 工单详细描述 */
    @NotBlank(message = "工单描述不能为空")  // 非空字符串校验:工单描述必填
    private String description;  // 工单详细描述(问题详情)

    // —— title 字段的 getter/setter ——
    public String getTitle() {  // 获取工单标题
        return title;  // 返回工单标题
    }

    public void setTitle(String title) {  // 设置工单标题
        this.title = title;  // 赋值工单标题
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取工单描述
        return description;  // 返回工单描述
    }

    public void setDescription(String description) {  // 设置工单描述
        this.description = description;  // 赋值工单描述
    }
}

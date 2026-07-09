package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解

/**
 * 系统配置更新请求 DTO
 * <p>
 * 管理员更新系统配置项，configKey 与 configValue 不能为空。
 * </p>
 */
public class SystemConfigRequest {  // 系统配置更新请求 DTO 类定义

    /** 配置键 */
    @NotBlank(message = "配置键不能为空")  // 非空字符串校验:配置键必填
    private String configKey;  // 配置项键名(如"invest.minAmount")

    /** 配置值 */
    @NotBlank(message = "配置值不能为空")  // 非空字符串校验:配置值必填
    private String configValue;  // 配置项值(统一以字符串存储,使用时按需转换)

    /** 配置描述（可选） */
    private String description;  // 配置描述(选填),说明该配置项用途

    // —— configKey 字段的 getter/setter ——
    public String getConfigKey() {  // 获取配置键
        return configKey;  // 返回配置键
    }

    public void setConfigKey(String configKey) {  // 设置配置键
        this.configKey = configKey;  // 赋值配置键
    }

    // —— configValue 字段的 getter/setter ——
    public String getConfigValue() {  // 获取配置值
        return configValue;  // 返回配置值
    }

    public void setConfigValue(String configValue) {  // 设置配置值
        this.configValue = configValue;  // 赋值配置值
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取配置描述
        return description;  // 返回配置描述
    }

    public void setDescription(String description) {  // 设置配置描述
        this.description = description;  // 赋值配置描述
    }
}

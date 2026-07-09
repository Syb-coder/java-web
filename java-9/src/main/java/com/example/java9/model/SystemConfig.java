package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 系统配置实体
 * <p>
 * 对应 system_configs 表，存储平台基础参数（如风控阈值、收益上限等）。
 * 运营管理员可通过后台动态调整，避免硬编码。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "system_configs")  // 映射到 system_configs 表
public class SystemConfig {  // 系统配置实体，键值对存储平台参数

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 配置键（唯一） */
    @Column(nullable = false, unique = true, length = 50)  // 非空且唯一，长度50
    private String configKey;  // 配置键，全局唯一标识，对应业务参数名

    /** 配置值 */
    @Column(nullable = false, length = 200)  // 非空，长度200，覆盖数值/字符串参数
    private String configValue;  // 配置值，统一用字符串存储，业务侧按需类型转换

    /** 配置说明 */
    @Column(length = 200)  // 长度200
    private String description;  // 配置说明，便于运营理解参数含义

    /** 更新时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime updatedAt;  // 更新时间戳，每次修改刷新，便于审计变更

    public SystemConfig() {  // JPA 无参构造器
    }

    public SystemConfig(String configKey, String configValue, String description) {  // 新增配置构造器
        this.configKey = configKey;  // 赋值配置键
        this.configValue = configValue;  // 赋值配置值
        this.description = description;  // 赋值配置说明
        this.updatedAt = LocalDateTime.now();  // 服务端生成更新时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getConfigKey() {  // 获取配置键
        return configKey;
    }

    public void setConfigKey(String configKey) {  // 设置配置键
        this.configKey = configKey;
    }

    public String getConfigValue() {  // 获取配置值
        return configValue;
    }

    public void setConfigValue(String configValue) {  // 设置配置值
        this.configValue = configValue;
    }

    public String getDescription() {  // 获取配置说明
        return description;
    }

    public void setDescription(String description) {  // 设置配置说明
        this.description = description;
    }

    public LocalDateTime getUpdatedAt() {  // 获取更新时间
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {  // 设置更新时间
        this.updatedAt = updatedAt;
    }
}

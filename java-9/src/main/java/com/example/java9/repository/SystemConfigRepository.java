package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.SystemConfig; // 引入系统配置实体，对应 system_configs 表
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.Optional; // 引入 Optional 包装类

/**
 * 系统配置 Repository
 */
@Repository // 标识为持久层 Bean
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> { // 继承 JPA，主键 Long

    /** 根据配置键查询配置值 */
    Optional<SystemConfig> findByConfigKey(String configKey); // 业务运行时按 key 读取阈值（如风控大额阈值、最低投资额）
}

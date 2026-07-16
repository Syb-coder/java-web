package com.example.java11.repository;

import com.example.java11.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 系统配置数据访问层
 * <p>
 * 提供对 system_configs 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * 按配置键查找配置项
     *
     * @param key 配置键
     * @return 配置对象，不存在时返回 empty
     */
    Optional<SystemConfig> findByConfigKey(String key);
}

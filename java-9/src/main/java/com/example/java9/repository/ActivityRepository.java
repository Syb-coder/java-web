package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.Activity; // 引入营销活动实体，对应 activities 表
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 营销活动 Repository
 */
@Repository // 标识为持久层 Bean
public interface ActivityRepository extends JpaRepository<Activity, Long> { // 继承 JPA，主键 Long

    /** 查询进行中的活动 */
    List<Activity> findByActiveTrue(); // Spring Data 解析 True 后缀为 active = true 条件，C 端首页展示当前可参与活动
}

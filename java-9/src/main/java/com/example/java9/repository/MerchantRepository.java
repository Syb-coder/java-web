package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.Merchant; // 引入商户实体，对应 merchants 表
import com.example.java9.model.MerchantStatus; // 引入商户状态枚举（PENDING/APPROVED/REJECTED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器
import java.util.Optional; // 引入 Optional 包装类

/**
 * 商户 Repository
 */
@Repository // 标识为持久层 Bean
public interface MerchantRepository extends JpaRepository<Merchant, Long> { // 继承 JPA，主键 Long

    /** 根据用户名查询商户（登录校验） */
    Optional<Merchant> findByUsername(String username); // 商户登录时凭用户名定位账号

    /** 根据状态查询商户列表（运营审核用） */
    List<Merchant> findByStatus(MerchantStatus status); // 运营后台拉取待审核(PENDING)或已通过(APPROVED)商户清单
}

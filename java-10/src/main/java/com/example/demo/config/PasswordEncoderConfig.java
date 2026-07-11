package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密器配置
 * <p>
 * 为什么单独配置为 Bean：便于在 UserService 和 DataInitializer 中统一注入，
 * 保证全系统使用相同的加密策略，避免各处自行实例化导致算法不一致。
 * 为什么用 BCrypt：自带盐值（salt），每次加密结果不同，有效防御彩虹表攻击。
 * </p>
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 注册 BCryptPasswordEncoder 为 Spring Bean
     *
     * @return PasswordEncoder 实例，使用默认强度（strength=10）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

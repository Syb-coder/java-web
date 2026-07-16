package com.example.java12;  // 顶层包，与 artifactId 一致

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网文论坛启动类
 * <p>
 * Spring Boot 应用入口，通过 {@link SpringBootApplication} 注解启用自动配置、
 * 组件扫描与 Spring Boot 配置。启动后默认监听 8090 端口。
 * </p>
 */
@SpringBootApplication  // 开启自动配置 + 组件扫描 + 配置类
public class Java12Application {

    /**
     * main 方法：Spring Boot 应用启动入口
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        SpringApplication.run(Java12Application.class, args);
    }
}

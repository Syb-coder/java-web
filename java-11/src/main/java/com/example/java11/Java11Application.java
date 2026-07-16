package com.example.java11;  // 项目根包，所有业务代码均在此包或其子包下

import org.springframework.boot.SpringApplication;  // Spring Boot 启动入口
import org.springframework.boot.autoconfigure.SpringBootApplication;  // 自动配置注解

/**
 * 二次元讨论网站 - 应用启动入口
 * <p>
 * 负责引导 Spring 容器启动，自动扫描 com.example.java11 下的所有组件。
 * 启动后访问 http://localhost:8089/ 即可使用前台，http://localhost:8089/admin.html 进入后台。
 * </p>
 */
@SpringBootApplication  // 开启自动配置、组件扫描与 Spring Boot 配置
public class Java11Application {  // 主启动类

    /**
     * 应用主入口
     *
     * @param args 启动参数，可覆盖 application.properties 中的配置
     */
    public static void main(String[] args) {  // JVM 入口
        SpringApplication.run(Java11Application.class, args);  // 委托给 SpringApplication 启动内嵌 Tomcat
    }
}

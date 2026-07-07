package com.example.java5; // 声明包路径，归属项目根包

// ===== Spring Boot 启动相关 =====
import org.springframework.boot.SpringApplication;          // Spring Boot 启动入口
import org.springframework.boot.autoconfigure.SpringBootApplication; // 自动配置注解

/**
 * java-5 应用启动类
 * <p>
 * 职责：Spring Boot 应用入口，main 方法启动内嵌 Tomcat 并初始化 IoC 容器。
 * </p>
 * @SpringBootApplication 是组合注解，等同于：
 * - @SpringBootConfiguration：标记为配置类
 * - @EnableAutoConfiguration：启用自动配置（根据依赖自动装配 Bean）
 * - @ComponentScan：扫描当前包及子包的 @Component/@Service/@Repository/@Controller
 */
@SpringBootApplication // 启用 Spring Boot 自动配置与组件扫描
public class Java5Application {

    /**
     * 应用入口方法
     * <p>
     * 职责：启动 Spring 容器，加载内嵌 Tomcat，监听 8084 端口。
     * </p>
     *
     * @param args 命令行参数，可覆盖 application.properties 中的配置
     */
    public static void main(String[] args) {
        SpringApplication.run(Java5Application.class, args); // 启动应用
    }

}

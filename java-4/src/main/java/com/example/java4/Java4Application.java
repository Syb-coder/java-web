// 声明包路径，与项目 artifactId 保持一致
package com.example.java4;

// 导入 Spring Boot 启动注解
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园图书借阅管理系统 - 主启动类
 * <p>
 * 职责：作为 Spring Boot 应用的入口，负责初始化 IoC 容器、自动装配 Bean、启动内嵌 Tomcat。
 * </p>
 * <p>
 * 设计说明：
 * - @SpringBootApplication 是组合注解，等价于 @Configuration + @EnableAutoConfiguration + @ComponentScan；
 * - 默认扫描包路径为当前包 com.example.java4 及其子包，所有 Controller/Service/Repository 均可被自动发现；
 * - 启动后通过 http://localhost:8090 访问用户端首页，/admin.html 访问后台管理端。
 * </p>
 */
@SpringBootApplication // 声明本类为 Spring Boot 应用入口，启用自动装配与组件扫描
public class Java4Application {

    /**
     * 程序入口方法
     * <p>
     * SpringApplication.run 会完成以下工作：
     * 1. 创建 Spring 应用上下文；
     * 2. 读取 application.properties 配置；
     * 3. 启动内嵌 Tomcat 监听 8090 端口；
     * 4. 执行 DataInitializer 等 CommandLineRunner 初始化种子数据。
     * </p>
     *
     * @param args 启动参数，可传入 --server.port=xxxx 等覆盖配置
     */
    public static void main(String[] args) {
        // 委托 SpringApplication 启动，传入当前主类与命令行参数
        SpringApplication.run(Java4Application.class, args);
    }
}

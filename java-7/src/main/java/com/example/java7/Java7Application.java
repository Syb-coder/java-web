package com.example.java7; // 声明类的包路径为 com.example.java7，便于 JVM 类加载与 Spring 组件扫描

import org.springframework.boot.SpringApplication; // 引入 Spring Boot 启动工具类，提供 run 方法引导应用启动
import org.springframework.boot.autoconfigure.SpringBootApplication; // 引入 Spring Boot 核心注解，启用自动配置与组件扫描

/**
 * 个人图书借阅管理系统启动类
 * <p>
 * 职责：作为 Spring Boot 应用入口，自动扫描 com.example.java7 包下的所有组件，
 * 完成 IoC 容器初始化、内嵌 Tomcat 启动、JPA 仓储装配等工作。
 * </p>
 * <p>
 * 技术要点：
 * 1. @SpringBootApplication 组合了 @SpringBootConfiguration、@EnableAutoConfiguration、@ComponentScan 三个注解；
 * 2. 默认扫描范围为启动类所在包及其子包；
 * 3. 通过 SpringApplication.run 静态方法完成 Spring 应用上下文的创建与启动。
 * </p>
 */
@SpringBootApplication // Spring Boot 核心注解，启用自动配置、组件扫描，并将本类标记为配置类
public class Java7Application { // 定义公共启动类 Java7Application，类名需与文件名一致

    /**
     * 应用程序主入口方法
     * <p>
     * 该方法是 JVM 启动应用时调用的入口，通过委托 SpringApplication.run 完成以下工作：
     * 1. 创建 Spring 应用上下文（ApplicationContext）；
     * 2. 执行自动配置逻辑，装配 Bean；
     * 3. 启动内嵌 Tomcat 服务器，监听 HTTP 请求；
     * 4. 初始化 JPA 实体管理器与仓储代理。
     * </p>
     *
     * @param args 启动参数，由 JVM 透传，本系统未使用
     */
    public static void main(String[] args) { // 主入口方法，JVM 约定的应用启动入口，static 修饰使其可被 JVM 直接调用
        // 委托 SpringApplication.run 完成上下文初始化与 Tomcat 启动，传入启动类字节码与命令行参数
        SpringApplication.run(Java7Application.class, args);
    }
}

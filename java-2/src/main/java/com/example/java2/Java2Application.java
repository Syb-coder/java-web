// 声明当前类所在的包路径，Spring Boot 通过包扫描机制加载该类
package com.example.java2;

// 导入 Spring Boot 启动入口注解
import org.springframework.boot.SpringApplication;
// 导入 Spring Boot 自动配置注解
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网络安全知识学习平台启动类
 * <p>
 * 职责：作为 Spring Boot 应用入口，自动扫描 com.example.java2 包下的所有组件，
 * 完成 IoC 容器初始化、内嵌 Tomcat 启动、JPA 仓储装配等工作。
 * </p>
 */
// @SpringBootApplication 是复合注解，等价于：
//   @SpringBootConfiguration（标识配置类）
//   @EnableAutoConfiguration（启用自动装配）
//   @ComponentScan（扫描当前包及子包下的所有 @Component/@Service/@Controller 等组件）
@SpringBootApplication
public class Java2Application {

    /**
     * 应用程序主入口方法
     *
     * @param args 启动参数，由 JVM 透传，本系统未使用
     */
    public static void main(String[] args) {
        // 委托 SpringApplication.run 完成上下文初始化与 Tomcat 启动
        // 该方法会创建 Spring 容器、扫描注解、初始化数据源、启动内嵌 Tomcat、注册 DispatcherServlet
        SpringApplication.run(Java2Application.class, args);
    }
}

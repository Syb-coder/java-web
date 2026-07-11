package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生信息管理系统启动类
 * <p>
 * 为什么使用 @SpringBootApplication：
 * 该注解组合了 @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan，
 * 是 Spring Boot 应用的标准入口，能自动扫描 com.example.demo 包及其子包下的所有组件。
 * </p>
 */
@SpringBootApplication
public class StudentInfoApplication {

    /**
     * 应用程序入口方法
     *
     * @param args 启动参数，可传递给 Spring 容器
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentInfoApplication.class, args);
    }
}

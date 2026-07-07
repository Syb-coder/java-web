package com.example.java6; // 声明当前类所在的包路径，位于项目根包，是 Spring Boot 组件扫描的起点

import org.springframework.boot.SpringApplication; // 导入 Spring Boot 启动引导类，用于启动内嵌 Web 容器并初始化 ApplicationContext
import org.springframework.boot.autoconfigure.SpringBootApplication; // 导入 Spring Boot 应用注解，组合了 @Configuration + @EnableAutoConfiguration + @ComponentScan

/**
 * 国家网络安全宣传官网 - 启动类
 *
 * <p>基于 Spring Boot 4.1.0 + JDK 25 + H2 文件数据库，
 * 提供新闻资讯、安全知识、政策法规、举报中心四大模块。</p>
 */
@SpringBootApplication // 标记为 Spring Boot 应用入口，自动开启组件扫描（扫描 com.example.java6 及其子包）与自动配置（根据依赖自动装配 Bean）
public class Java6Application {

	/**
	 * 应用程序入口方法
	 *
	 * @param args 启动参数
	 */
	public static void main(String[] args) { // JVM 启动时调用，args 为命令行传入的启动参数（如 --server.port=8085）
		SpringApplication.run(Java6Application.class, args); // 启动 Spring Boot 应用：创建 ApplicationContext、启动内嵌 Tomcat、扫描注册 Bean、执行 ApplicationRunner 等
	}

}

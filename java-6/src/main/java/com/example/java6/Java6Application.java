package com.example.java6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 国家网络安全宣传官网 - 启动类
 *
 * <p>基于 Spring Boot 4.1.0 + JDK 25 + H2 文件数据库，
 * 提供新闻资讯、安全知识、政策法规、举报中心四大模块。</p>
 */
@SpringBootApplication
public class Java6Application {

	/**
	 * 应用程序入口方法
	 *
	 * @param args 启动参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(Java6Application.class, args);
	}

}

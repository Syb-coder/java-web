package com.example.java9; // 项目根包，Spring Boot 默认从此包向下扫描组件

import org.springframework.boot.SpringApplication; // 引入启动入口工具类
import org.springframework.boot.autoconfigure.SpringBootApplication; // 引入自动配置注解

/**
 * 金融理财支付平台网站 - 主启动类
 * <p>
 * 职责：
 * 1. 启动 Spring Boot 容器，加载所有自动配置；
 * 2. 扫描 com.example.java9 包下的组件（Controller、Service、Repository 等）；
 * 3. 启动内嵌 Tomcat，监听 8088 端口，提供 Web 服务。
 * </p>
 * <p>
 * 平台架构概述：
 * - C端用户前台：注册登录、资产管理、理财投资、支付收银、营销活动、客服工单；
 * - B端商户后台：商户入驻、收款订单、对账结算；
 * - 运营管理后台：用户/商户/产品/活动统一管理；
 * - 风控合规后台：基于规则引擎的风险识别与合规审计；
 * - 底层支付理财业务：资金清算、收益计算、交易流水。
 * </p>
 */
@SpringBootApplication // 复合注解：@Configuration + @EnableAutoConfiguration + @ComponentScan，开启自动配置与组件扫描
public class Java9Application {

	/**
	 * 程序入口方法
	 *
	 * @param args 启动参数（本项目未使用）
	 */
	public static void main(String[] args) { // JVM 入口
		SpringApplication.run(Java9Application.class, args); // 启动 Spring 容器、加载自动配置、启动内嵌 Tomcat
	}

}

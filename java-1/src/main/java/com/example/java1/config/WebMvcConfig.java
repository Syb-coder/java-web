// 声明包路径，归类为 config 配置层，存放 Web MVC 相关配置
package com.example.java1.config;

import org.springframework.context.annotation.Configuration; // 引入 @Configuration，声明为配置类
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // 引入拦截器注册器，用于注册自定义拦截器
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 引入 Web MVC 配置接口，自定义 MVC 行为

/**
 * Web MVC 配置
 * <p>注册登录拦截器，作用于所有 /api/** 路径。</p>
 * <p>
 * 设计说明：采用统一拦截 /api/** 再由拦截器内部精细化放行的策略，
 * 避免在注册阶段列举大量 excludePathPatterns，权限规则集中可维护，
 * 便于后续扩展新的鉴权规则而无需修改配置类。
 * </p>
 */
@Configuration // 声明为配置类，由容器扫描并处理其中的 @Bean 与回调方法
public class WebMvcConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer 接口以自定义 MVC 配置

    private final LoginInterceptor loginInterceptor; // 登录拦截器依赖，final 保证构造后不可变

    /**
     * 构造方法注入拦截器
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param loginInterceptor 登录拦截器
     */
    public WebMvcConfig(LoginInterceptor loginInterceptor) { // 构造方法注入依赖
        this.loginInterceptor = loginInterceptor; // 赋值登录拦截器
    }

    @Override // 重写拦截器注册回调方法，在容器初始化时注册自定义拦截器
    public void addInterceptors(InterceptorRegistry registry) { // registry 由框架注入，用于添加拦截器
        // 拦截所有 API 请求，由拦截器内部按方法与路径精细化放行
        // 原因：统一入口便于权限规则集中维护，避免遗漏新增路径的鉴权
        registry.addInterceptor(loginInterceptor) // 注册登录拦截器
                .addPathPatterns("/api/**"); // 仅拦截 /api/** 下的请求，放行静态资源与非 API 路径
    }
}

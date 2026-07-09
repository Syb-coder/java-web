package com.example.java9.config; // 声明 Config 层包路径

import org.springframework.context.annotation.Configuration; // 引入 @Configuration，标识为 Spring 配置类
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // 引入拦截器注册器，用于注册自定义拦截器
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 引入 WebMvc 配置回调接口

/**
 * Web MVC 配置
 * <p>
 * 注册登录拦截器，作用于所有 /api/** 路径，
 * 由拦截器内部按 HTTP 方法与路径精细化放行。
 * </p>
 */
@Configuration // 标识为配置类，由 Spring 容器扫描并应用其中定义的回调
public class WebMvcConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer 扩展 Spring MVC 默认配置

    private final LoginInterceptor loginInterceptor; // 持有登录拦截器引用，由构造器注入

    public WebMvcConfig(LoginInterceptor loginInterceptor) { // 构造器注入，Spring 自动传入拦截器 Bean
        this.loginInterceptor = loginInterceptor; // 保存拦截器引用
    }

    @Override // 重写注册拦截器回调方法
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有 API 请求
        registry.addInterceptor(loginInterceptor) // 将登录拦截器加入拦截器链
                .addPathPatterns("/api/**"); // 仅匹配 /api/** 路径，静态资源与其他端点不受影响
    }
}

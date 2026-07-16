package com.example.java11.config;  // 配置层包，集中存放 Web 配置与拦截器

import org.springframework.beans.factory.annotation.Autowired;  // 字段注入注解
import org.springframework.context.annotation.Configuration;  // 配置类标识
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;  // 拦截器注册器
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  // Web MVC 配置回调

/**
 * Web MVC 配置类
 * <p>
 * 注册登录拦截器，保护 /api/** 下的写操作接口。
 * 静态资源（HTML/CSS/JS）由 Spring Boot 默认的静态资源处理器服务，无需额外配置。
 * </p>
 */
@Configuration  // 声明为 Spring 配置类，会被自动扫描
public class WebMvcConfig implements WebMvcConfigurer {  // 实现 WebMvcConfigurer 以自定义 MVC 配置

    private final LoginInterceptor loginInterceptor;  // 登录拦截器，由 Spring 注入

    /**
     * 构造器注入拦截器
     *
     * @param loginInterceptor 登录拦截器实例
     */
    @Autowired  // 自动注入
    public WebMvcConfig(LoginInterceptor loginInterceptor) {  // 构造器注入优于字段注入，便于测试
        this.loginInterceptor = loginInterceptor;  // 赋值
    }

    /**
     * 注册拦截器，仅拦截 /api/** 路径，静态资源放行
     *
     * @param registry 拦截器注册器
     */
    @Override  // 重写父接口方法
    public void addInterceptors(InterceptorRegistry registry) {  // 拦截器注册回调
        registry.addInterceptor(loginInterceptor)  // 注册登录拦截器
                .addPathPatterns("/api/**")  // 仅拦截 API 路径
                .excludePathPatterns(  // 排除以下路径不拦截
                        "/api/auth/login",  // 用户登录
                        "/api/auth/register",  // 用户注册
                        "/api/auth/admin/login",  // 管理员登录
                        "/error"  // Spring Boot 错误页
                );  // 其余路径按 LoginInterceptor 内部规则放行
    }
}

package com.example.java6.config; // 声明当前类所在的包路径，属于应用配置层（Config）

import org.springframework.context.annotation.Configuration; // 导入配置类注解，标识此类为 Spring 配置类，会被容器扫描并处理
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // 导入拦截器注册器，用于注册自定义拦截器并配置拦截路径
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 导入 Web MVC 配置回调接口，通过重写其方法自定义 Spring MVC 行为

/**
 * Web MVC 配置
 *
 * <p>注册登录拦截器，作用于所有 /api/** 路径。</p>
 */
@Configuration // 标记为 Spring 配置类，容器启动时加载此类并应用其中的 Bean 与配置
public class WebMvcConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer 接口，重写 addInterceptors 方法注册拦截器

    private final LoginInterceptor loginInterceptor; // 通过构造器注入登录拦截器，声明为 final 保证不可变（线程安全）

    public WebMvcConfig(LoginInterceptor loginInterceptor) { // 构造器注入：Spring 自动装配 LoginInterceptor（由 @Component 注册）
        this.loginInterceptor = loginInterceptor; // 完成字段赋值
    }

    @Override // 重写 WebMvcConfigurer 的 addInterceptors 方法，在 Spring MVC 初始化时注册拦截器
    public void addInterceptors(InterceptorRegistry registry) { // registry 由 Spring 自动传入，用于向拦截器链添加自定义拦截器
        // 拦截所有 API 请求，由拦截器内部按方法与路径精细化放行
        // 设计意图：统一拦截 /api/** 下所有请求，由 LoginInterceptor 内部按规则判断是否放行
        // 不使用 excludePathPatterns 是因为放行规则涉及 HTTP 方法（GET 全放行），路径匹配无法表达方法维度
        registry.addInterceptor(loginInterceptor) // 将登录拦截器加入拦截器链
                .addPathPatterns("/api/**"); // 拦截所有以 /api/ 开头的请求，静态资源与非 API 路径不受影响
    }
}

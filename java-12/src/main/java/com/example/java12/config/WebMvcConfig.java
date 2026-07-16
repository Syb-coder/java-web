package com.example.java12.config;  // 配置层包，存放拦截器与 Web 配置

import org.springframework.context.annotation.Configuration;  // 配置类标识
import org.springframework.web.servlet.config.annotation.CorsRegistry;  // CORS 注册器
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;  // 拦截器注册器
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  // Web MVC 配置回调

/**
 * Web MVC 配置类
 * <p>
 * 注册认证拦截器，保护 /api/** 下的接口。
 * 配置 CORS 跨域支持，允许前端 Vue 开发服务器（localhost:5173）访问后端 API。
 * 静态资源由 Spring Boot 默认的静态资源处理器服务。
 * </p>
 */
@Configuration  // 声明为 Spring 配置类，会被自动扫描
public class WebMvcConfig implements WebMvcConfigurer {  // 实现 WebMvcConfigurer 以自定义 MVC 配置

    /** 认证拦截器，由 Spring 注入 */
    private final AuthInterceptor authInterceptor;

    /**
     * 构造器注入拦截器
     *
     * @param authInterceptor 认证拦截器实例
     */
    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 注册拦截器，拦截所有 /api/** 路径
     * <p>
     * 不使用 excludePathPatterns，因为拦截器内部已处理公开路径的放行逻辑。
     * </p>
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有 API 路径
                .excludePathPatterns("/error");  // 排除 Spring Boot 错误页
    }

    /**
     * 配置 CORS 跨域支持
     * <p>
     * 允许前端开发服务器（Vite 默认端口 5173）跨域访问后端 API。
     * 允许所有请求方法和请求头，允许携带凭证。
     * </p>
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对所有 API 路径启用 CORS
                .allowedOriginPatterns("*")  // 允许所有来源（开发环境）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的 HTTP 方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带凭证
                .maxAge(3600);  // 预检请求缓存时间（秒）
    }
}

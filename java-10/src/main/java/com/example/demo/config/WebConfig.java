package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：CORS 跨域设置
 * <p>
 * 为什么需要 CORS：前端 Vite 开发服务器运行在 5173/5174，后端在 8080，
 * 浏览器同源策略会阻止跨域请求，必须显式允许前端域名访问。
 * 为什么用 WebMvcConfigurer 而非 Spring Security：本项目仅引入 spring-security-crypto
 * 用于密码加密，未引入完整的 Spring Security，因此通过 WebMvcConfigurer 配置即可。
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS 映射规则
     * <p>
     * 允许的前端源：localhost:5173（Vite 默认）和 localhost:5174（备用端口）
     * 允许所有 HTTP 方法和请求头，支持携带凭证（Cookie）
     * </p>
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

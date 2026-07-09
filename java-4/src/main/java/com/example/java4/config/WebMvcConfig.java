// 声明包路径，存放 Web 配置类
package com.example.java4.config;

// 导入 Spring MVC 配置注解
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * <p>
 * 注册登录拦截器，作用于所有 /api/** 路径。
 * 静态资源（/、/admin.html 等）由 Spring Boot 默认的静态资源处理器服务，不受拦截器影响。
 * </p>
 */
@Configuration // 声明为 Spring 配置类
public class WebMvcConfig implements WebMvcConfigurer {

    /** 登录拦截器（由容器注入） */
    private final LoginInterceptor loginInterceptor;

    /**
     * 构造方法注入依赖
     *
     * @param loginInterceptor 登录拦截器
     */
    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有 API 请求，由拦截器内部按方法与路径精细化放行
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**");
    }
}

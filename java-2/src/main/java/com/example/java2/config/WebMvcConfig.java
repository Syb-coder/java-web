// 声明包路径
package com.example.java2.config;

// 导入 Spring Web MVC 配置注解
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * <p>注册登录拦截器，作用于所有 /api/** 路径。</p>
 */
// @Configuration 标识此类为 Spring 配置类，容器启动时扫描并注册其中定义的 Bean，
// 实现 WebMvcConfigurer 接口可自定义 Spring MVC 的拦截器、跨域、消息转换器等全局行为
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 通过构造注入 LoginInterceptor，Spring 会自动装配 Bean，便于单元测试替换 mock
    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    // addInterceptors 是 WebMvcConfigurer 的回调方法，容器启动时由 Spring MVC 调用注册自定义拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有 API 请求，由拦截器内部按方法与路径精细化放行
        // 仅匹配 /api/** 是为了排除静态资源与非 API 路径，避免拦截器对前端静态文件误伤
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**");
    }
}

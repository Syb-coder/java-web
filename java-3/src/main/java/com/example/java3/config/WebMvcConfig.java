// 声明当前类所在的包路径
package com.example.java3.config;

// 导入 @Configuration，标识该类为 Spring 配置类，会被容器加载
import org.springframework.context.annotation.Configuration;
// 导入 InterceptorRegistry，用于注册拦截器并指定拦截路径
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// 导入 WebMvcConfigurer，用于自定义 Spring MVC 配置（拦截器、跨域、视图解析器等）
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * <p>
 * 注册登录拦截器，作用于所有 /api/** 路径。
 * </p>
 */
// @Configuration：标识为配置类，容器启动时加载并处理其中的 @Bean 与回调方法
@Configuration
// 实现 WebMvcConfigurer 接口，重写其方法以扩展 MVC 行为
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入的登录拦截器实例，由 Spring 容器通过构造器注入
    private final LoginInterceptor loginInterceptor;

    // 构造器注入：Spring 自动将 LoginInterceptor Bean 注入到此处的参数
    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        // 赋值给成员变量，供后续 addInterceptors 使用
        this.loginInterceptor = loginInterceptor;
    }

    // 重写 addInterceptors 方法，注册自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将 LoginInterceptor 注册到拦截器链
        registry.addInterceptor(loginInterceptor)
                // addPathPatterns：指定拦截路径，/api/** 表示拦截所有以 /api/ 开头的请求
                .addPathPatterns("/api/**");
    }
}

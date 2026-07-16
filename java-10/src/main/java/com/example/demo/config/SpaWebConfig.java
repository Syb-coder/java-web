package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * SPA 路由转发配置：解决 Vue Router History 模式下刷新页面 404 的问题
 * <p>
 * 为什么需要：Vue 使用 createWebHistory() 时，URL 如 /dashboard、/student 由前端路由管理，
 * 后端没有对应的 Controller。浏览器直接访问或刷新这些路径时，Spring Boot 会返回 404。
 * 解决方案：所有非 /api/** 的请求，如果找不到静态资源，统一返回 index.html，由前端路由处理。
 * </p>
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    /**
     * 添加资源处理器：将所有非 API 请求 fallback 到 index.html
     * <p>
     * 规则：
     * - /api/** 由 Controller 处理，不经过此规则
     * - /h2-console 由 H2 控制台处理
     * - 存在的静态资源（js/css/图片）正常返回
     * - 其他所有路径返回 index.html，交给前端 Vue Router 处理
     * </p>
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // 先尝试查找实际存在的静态资源
                        Resource resource = location.createRelative(resourcePath);
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        // 找不到静态资源时，返回 index.html（SPA 入口），排除 API 和 H2 控制台路径
                        if (!resourcePath.startsWith("api/") && !resourcePath.startsWith("h2-console")) {
                            return new ClassPathResource("/static/index.html");
                        }
                        return null;
                    }
                });
    }
}
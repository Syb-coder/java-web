package com.example.java12.util;  // 工具类包

import org.springframework.stereotype.Component;  // Spring 组件注解

import java.util.UUID;  // UUID 生成工具
import java.util.concurrent.ConcurrentHashMap;  // 线程安全 HashMap

/**
 * Token 工具类
 * <p>
 * 基于 UUID + ConcurrentHashMap 管理用户登录态。
 * 登录成功时生成 Token 并存入内存映射，后续请求通过 Authorization 请求头携带 Token，
 * 拦截器校验 Token 有效性并获取对应用户 ID。
 * </p>
 * <p>
 * 注意：内存存储方式在应用重启后失效，用户需重新登录。适合演示项目，
 * 生产环境应替换为 Redis 或 JWT 方案。
 * </p>
 */
@Component  // 声明为 Spring 组件，由容器管理单例
public class TokenUtil {

    /** Token → 用户 ID 的映射表（线程安全） */
    private final ConcurrentHashMap<String, Long> tokenMap = new ConcurrentHashMap<>();

    /**
     * 生成 Token 并绑定用户 ID
     *
     * @param userId 用户 ID
     * @return 生成的 Token 字符串（32位无横线 UUID）
     */
    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenMap.put(token, userId);
        return token;
    }

    /**
     * 根据 Token 获取用户 ID
     *
     * @param token Token 字符串
     * @return 用户 ID（Token 无效时返回 null）
     */
    public Long getUserId(String token) {
        if (token == null) {
            return null;
        }
        return tokenMap.get(token);
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token Token 字符串
     * @return true 表示 Token 有效
     */
    public boolean isValid(String token) {
        return token != null && tokenMap.containsKey(token);
    }

    /**
     * 移除 Token（登出时调用）
     *
     * @param token Token 字符串
     */
    public void removeToken(String token) {
        if (token != null) {
            tokenMap.remove(token);
        }
    }
}

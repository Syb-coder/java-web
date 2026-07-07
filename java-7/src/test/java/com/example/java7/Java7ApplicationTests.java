package com.example.java7;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用上下文加载测试
 * <p>
 * 职责：验证 Spring Boot 应用能够正常启动并加载所有组件。
 * </p>
 */
@SpringBootTest
class Java7ApplicationTests {

    /**
     * 上下文加载测试
     */
    @Test
    void contextLoads() {
        // 仅验证上下文加载，无业务断言
    }
}

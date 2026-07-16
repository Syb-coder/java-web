package com.example.java12.config;  // 配置层包

import com.example.java12.model.Plate;  // 板块实体
import com.example.java12.model.Role;  // 角色枚举
import com.example.java12.model.User;  // 用户实体
import com.example.java12.repository.PlateRepository;  // 板块数据访问层
import com.example.java12.repository.UserRepository;  // 用户数据访问层
import org.springframework.boot.CommandLineRunner;  // 启动后执行接口
import org.springframework.context.annotation.Bean;  // Bean 注解
import org.springframework.context.annotation.Configuration;  // 配置类注解
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // BCrypt 密码编码器

/**
 * 数据初始化器
 * <p>
 * 应用启动时自动初始化种子数据：
 * <ol>
 *   <li>管理员账号（account: admin，password: admin123）</li>
 *   <li>默认板块（玄幻、都市、仙侠、科幻、历史、言情）</li>
 * </ol>
 * 仅在对应数据不存在时创建，避免重复初始化。
 * </p>
 */
@Configuration  // 声明为 Spring 配置类
public class DataInitializer {

    /** 默认管理员账号 */
    private static final String ADMIN_ACCOUNT = "admin";
    /** 默认管理员昵称 */
    private static final String ADMIN_NICKNAME = "系统管理员";
    /** 默认管理员密码（明文，会被 BCrypt 加密后存储） */
    private static final String ADMIN_PASSWORD = "admin123";

    /**
     * CommandLineRunner Bean：启动后执行数据初始化
     *
     * @param userRepository 用户数据访问层
     * @param plateRepository 板块数据访问层
     * @return CommandLineRunner 实例
     */
    @Bean  // 注册为 Spring Bean，启动后自动执行
    public CommandLineRunner initData(UserRepository userRepository, PlateRepository plateRepository) {
        return args -> {
            // 1. 初始化管理员账号（仅在不存在时创建）
            if (userRepository.findByAccount(ADMIN_ACCOUNT).isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                User admin = new User(ADMIN_ACCOUNT, ADMIN_NICKNAME, encoder.encode(ADMIN_PASSWORD));
                admin.setRole(Role.ADMIN);  // 设置为管理员角色
                userRepository.save(admin);
                System.out.println("[数据初始化] 管理员账号已创建: " + ADMIN_ACCOUNT + " / " + ADMIN_PASSWORD);
            }

            // 2. 初始化默认板块（仅在无板块数据时创建）
            if (plateRepository.count() == 0) {
                String[][] defaultPlates = {
                        {"玄幻", "东方玄幻、异世大陆等幻想类网文"},
                        {"都市", "都市生活、职场、商战等现实题材网文"},
                        {"仙侠", "修真、仙侠、古典神话类网文"},
                        {"科幻", "未来科技、星际、末世等科幻类网文"},
                        {"历史", "历史穿越、架空历史等历史类网文"},
                        {"言情", "现代言情、古代言情等情感类网文"}
                };
                int sortOrder = 1;  // 排序序号从1开始
                for (String[] plateData : defaultPlates) {
                    Plate plate = new Plate(plateData[0], plateData[1]);
                    plate.setSortOrder(sortOrder++);
                    plateRepository.save(plate);
                }
                System.out.println("[数据初始化] 默认板块已创建: 共 " + defaultPlates.length + " 个");
            }
        };
    }
}

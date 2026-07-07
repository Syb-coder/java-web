// 声明包路径，存放枚举类型
package com.example.java1.model; // 声明包路径为 com.example.java1.model，与实体类同包便于关联引用

/**
 * 读者类型枚举
 * <p>
 * 校园场景下区分学生与教师，二者借阅规则不同：
 * <ul>
 *   <li>STUDENT（学生）：借阅上限 5 本，单次借期 30 天；</li>
 *   <li>TEACHER（教师）：借阅上限 10 本，单次借期 60 天。</li>
 * </ul>
 * 借阅规则常量集中在本枚举中，避免在业务代码中散落魔法值。
 * </p>
 * <p>
 * 设计说明：
 * - 将业务规则常量集中在枚举中，遵循单一职责原则，便于规则统一维护与调整；
 * - 使用 public static final 常量而非魔法值，提升可读性与可维护性；
 * - maxBorrow/loanDays 方法集中类型判断，避免业务层重复 if-else 散落。
 * </p>
 */
public enum ReaderType { // 定义公共枚举 ReaderType，封装读者类型与借阅规则

    /** 学生：借阅上限 5 本，借期 30 天 */
    STUDENT, // 学生类型，借阅规则较严格，上限低借期短

    /** 教师：借阅上限 10 本，借期 60 天 */
    TEACHER; // 教师类型，借阅规则较宽松，上限高借期长；分号结尾表示后续还有成员

    /** 学生借阅上限（本） */
    public static final int STUDENT_MAX_BORROW = 5; // 学生最多同时借 5 本，防止占用过多馆藏
    /** 教师借阅上限（本） */
    public static final int TEACHER_MAX_BORROW = 10; // 教师最多同时借 10 本，因教学科研需求更大
    /** 学生单次借期（天） */
    public static final int STUDENT_LOAN_DAYS = 30; // 学生借期 30 天，兼顾流通率与阅读需求
    /** 教师单次借期（天） */
    public static final int TEACHER_LOAN_DAYS = 60; // 教师借期 60 天，适配科研周期较长
    /** 续借延长期（天），学生教师一致 */
    public static final int RENEW_EXTEND_DAYS = 15; // 续借统一延长 15 天，简化规则便于管理
    /** 续借最大次数 */
    public static final int MAX_RENEW_COUNT = 1; // 最多续借 1 次，防止长期占用图书
    /** 超期罚款单价（元/天） */
    public static final double OVERDUE_FINE_PER_DAY = 0.5; // 超期每日 0.5 元，温和督促归还

    /**
     * 根据读者类型获取借阅上限
     *
     * @param type 读者类型
     * @return 借阅上限（本）
     */
    public static int maxBorrow(ReaderType type) { // 静态方法，根据类型返回借阅上限
        // 教师返回 10，学生返回 5，集中管理避免散落判断
        return type == TEACHER ? TEACHER_MAX_BORROW : STUDENT_MAX_BORROW; // 三元运算符简化分支，null 安全由调用方保证
    }

    /**
     * 根据读者类型获取单次借期
     *
     * @param type 读者类型
     * @return 借期（天）
     */
    public static int loanDays(ReaderType type) { // 静态方法，根据类型返回借期
        // 教师返回 60 天，学生返回 30 天
        return type == TEACHER ? TEACHER_LOAN_DAYS : STUDENT_LOAN_DAYS; // 集中判断避免业务层散落魔法值
    }
}

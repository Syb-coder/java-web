package com.example.java7.model; // 声明包路径为 com.example.java7.model，与领域模型同包

/**
 * 借阅状态枚举
 * <p>
 * 职责：定义借阅记录的生命周期状态，使用枚举集中管理状态值，
 * 避免散落的字符串常量导致的状态判断错误。
 * </p>
 * 状态流转：借出(BORROWED) -> 归还(RETURNED) 或 逾期(OVERDUE) -> 归还(RETURNED)
 * <p>
 * 设计要点：
 * 1. 使用枚举确保状态取值的合法性，编译期校验；
 * 2. 关联中文显示名称，便于前端直接渲染状态标签；
 * 3. 状态流转由服务层控制，枚举本身不包含行为逻辑，保持职责单一。
 * </p>
 */
public enum BorrowStatus { // 定义公共枚举 BorrowStatus，表示借阅记录的状态

    /** 借出：图书已被借出但尚未归还，且未超过应还日期 */
    BORROWED("借出中"), // 借出状态，构造时传入中文显示名称

    /** 已归还：图书已正常归还 */
    RETURNED("已归还"), // 已归还状态

    /** 逾期：超过应还日期仍未归还 */
    OVERDUE("已逾期"); // 逾期状态，末尾分号结束枚举常量列表

    /** 状态中文显示名称 */
    private final String displayName; // 中文显示名称字段，final 修饰保证不可变

    /**
     * 枚举构造方法
     * <p>
     * 枚举构造方法默认为 private，由 JVM 在类加载时调用，
     * 用于初始化每个枚举常量关联的显示名称。
     * </p>
     *
     * @param displayName 中文显示名称
     */
    BorrowStatus(String displayName) { // 枚举构造方法
        this.displayName = displayName; // 将传入的显示名称赋值给成员变量
    }

    /**
     * 获取状态中文显示名称
     * <p>
     * 提供给前端展示状态标签使用，避免前端硬编码状态中文映射，
     * 保证状态显示的一致性。
     * </p>
     *
     * @return 显示名称
     */
    public String getDisplayName() { // 公共访问方法，返回状态的中文显示名称
        return displayName; // 返回成员变量 displayName 的值
    }
}

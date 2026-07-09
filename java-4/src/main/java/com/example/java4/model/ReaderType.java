// 声明包路径，存放枚举类型
package com.example.java4.model;

/**
 * 读者类型枚举
 * <p>
 * 校园读者分为学生与教师两类，不同类型享有不同的借阅上限与借期：
 * <ul>
 *   <li>STUDENT（学生）：可借 5 本，借期 30 天；</li>
 *   <li>TEACHER（教师）：可借 10 本，借期 60 天。</li>
 * </ul>
 * </p>
 * <p>
 * 设计说明：使用枚举而非魔法字符串，保证类型安全；借阅规则封装在枚举内部，
 * 避免业务层散落硬编码，后续调整规则只需修改枚举一处。
 * </p>
 */
public enum ReaderType {

    /** 学生读者：借阅上限 5 本，借期 30 天 */
    STUDENT(5, 30),

    /** 教师读者：借阅上限 10 本，借期 60 天 */
    TEACHER(10, 60);

    /** 借阅上限（最大同时在借图书数量） */
    private final int maxBorrowCount;

    /** 借期天数（从借出日起计算应还日期） */
    private final int borrowDays;

    /**
     * 枚举构造方法
     *
     * @param maxBorrowCount 借阅上限
     * @param borrowDays     借期天数
     */
    ReaderType(int maxBorrowCount, int borrowDays) {
        this.maxBorrowCount = maxBorrowCount;
        this.borrowDays = borrowDays;
    }

    /** 获取借阅上限 */
    public int getMaxBorrowCount() {
        return maxBorrowCount;
    }

    /** 获取借期天数 */
    public int getBorrowDays() {
        return borrowDays;
    }
}

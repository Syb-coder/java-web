// 声明包路径，存放枚举类型
package com.example.java1.model; // 声明包路径为 com.example.java1.model，与实体类同包便于关联引用

/**
 * 借阅状态枚举
 * <p>
 * 描述借阅记录的生命周期状态：
 * <ul>
 *   <li>BORROWED：已借出未归还；</li>
 *   <li>RETURNED：已归还；</li>
 *   <li>OVERDUE：已逾期（由系统在查询时根据应还日期动态判定，或归还时若超期则标记）。</li>
 * </ul>
 * </p>
 * <p>
 * 设计说明：
 * - 枚举配合 @Enumerated(EnumType.STRING) 以字符串形式持久化，保证数据库可读性；
 * - 选用枚举而非魔法字符串，借助编译器类型检查避免拼写错误；
 * - OVERDUE 既是状态也可由 dueDate 动态计算，二者并存以简化查询过滤。
 * </p>
 */
public enum BorrowStatus { // 定义公共枚举 BorrowStatus，封装借阅生命周期状态

    /** 已借出未归还 */
    BORROWED, // 已借出状态，借阅成功后立即置为此状态，期间图书可被续借或归还

    /** 已归还 */
    RETURNED, // 已归还状态，归还后置为此状态，标志借阅流程闭环

    /** 已逾期 */
    OVERDUE // 已逾期状态，系统在应还日期之后且未归还时标记，用于罚款计算与催还提醒
}

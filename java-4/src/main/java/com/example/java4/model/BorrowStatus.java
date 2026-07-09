// 声明包路径，存放枚举类型
package com.example.java4.model;

/**
 * 借阅状态枚举
 * <p>
 * 描述借阅记录的生命周期状态：
 * <ul>
 *   <li>BORROWING：借阅中，图书尚未归还；</li>
 *   <li>RETURNED：已归还，借阅流程结束；</li>
 *   <li>OVERDUE：已逾期，超过应还日期仍未归还。</li>
 * </ul>
 * </p>
 * <p>
 * 设计说明：OVERDUE 是逻辑衍生状态，由定时任务或查询时根据 dueDate 动态判定，
 * 持久化时仍可能存为 BORROWING，由业务层在查询时统一修正，避免状态不一致。
 * </p>
 */
public enum BorrowStatus {

    /** 借阅中：图书已借出尚未归还 */
    BORROWING,

    /** 已归还：图书已归还，借阅流程结束 */
    RETURNED,

    /** 已逾期：超过应还日期仍未归还 */
    OVERDUE
}

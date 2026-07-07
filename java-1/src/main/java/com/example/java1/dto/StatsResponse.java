// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

/**
 * 首页统计响应 DTO
 * <p>
 * 聚合图书与借阅的核心指标，供首页数据看板展示。
 * </p>
 */
// 全部字段使用 long 基础类型而非 Long 包装类型，统计值不存在 null 语义，避免拆箱 NPE
public record StatsResponse(
        long totalBooks,        // 图书种类总数，按图书条目计数（非副本数）
        long totalCopies,       // 副本总数，所有图书 totalCopies 之和，反映馆藏规模
        long availableCopies,   // 可借副本总数，所有图书 availableCopies 之和，反映当前可借资源
        long totalReaders,      // 读者总数，注册读者数量，反映用户规模
        long borrowedCount,     // 借出中数量，当前未归还的借阅记录数
        long returnedCount,     // 已归还数量，历史已归还的借阅记录数
        long overdueCount,      // 逾期数量，超期未还或超期归还的记录数，预警指标
        double totalFine        // 累计罚款金额，超期产生的罚款总和，财务对账用
) {
}

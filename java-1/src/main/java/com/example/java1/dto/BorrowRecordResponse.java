// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入借阅记录实体类，用于 from 转换方法
import com.example.java1.model.BorrowRecord;
// 导入借阅状态枚举（BORROWED / RETURNED / OVERDUE），响应中透传给前端展示状态标签
import com.example.java1.model.BorrowStatus;

// 导入日期类（不含时分秒），用于借出/应还/归还日期
import java.time.LocalDate;

/**
 * 借阅记录响应 DTO
 * <p>
 * 对外返回借阅记录信息，包含图书与读者的冗余字段，便于前端直接展示。
 * </p>
 */
// 冗余 bookTitle/readerName/readerNo 字段避免前端二次查询关联实体，减少 HTTP 请求
public record BorrowRecordResponse(
        Long id, // 借阅记录主键
        Long bookId, // 图书 ID，前端跳转图书详情使用
        String bookTitle, // 图书书名（冗余字段），列表直接展示避免再查图书接口
        Long readerId, // 读者 ID
        String readerName, // 读者姓名（冗余字段），管理员视图直接展示
        String readerNo, // 学号/工号（冗余字段），便于定位读者
        LocalDate borrowDate, // 借出日期
        LocalDate dueDate, // 应还日期，前端用于逾期判定与倒计时展示
        LocalDate returnDate, // 实际归还日期，未归还时为 null
        BorrowStatus status, // 借阅状态，前端映射为状态标签颜色
        Integer renewCount, // 续借次数，限制最多续借 1 次
        Double fine, // 罚款金额，超期按 0.5 元/天计算
        String remark, // 备注信息
        // 是否逾期（动态计算，应还日期早于今天且未归还时为 true）
        boolean overdue // 动态字段：实体中无此列，响应时实时计算以反映当前逾期状态
) {
    /**
     * 从实体构造响应
     * <p>动态计算逾期标志：状态为 BORROWED 且应还日期早于今天。</p>
     *
     * @param record 借阅记录实体
     * @return 响应 DTO
     */
    public static BorrowRecordResponse from(BorrowRecord record) {
        // 逾期判定：当前状态为借出中，且应还日期早于今天
        // 实时不存库的原因：逾期状态随时间变化，存库需定时任务扫描，个人系统简化为查询时计算
        boolean isOverdue = record.getStatus() == BorrowStatus.BORROWED // 仅借出中状态才需判定，已归还的不算
                && record.getDueDate() != null // 应还日期非空才比较，防御空指针
                && LocalDate.now().isAfter(record.getDueDate()); // 当前日期晚于应还日期即逾期
        return new BorrowRecordResponse(
                record.getId(), // 透传记录主键
                record.getBook().getId(), // 透传图书 ID
                record.getBook().getTitle(), // 透传图书书名（冗余）
                record.getReader().getId(), // 透传读者 ID
                record.getReader().getName(), // 透传读者姓名（冗余）
                record.getReader().getReaderNo(), // 透传学号/工号（冗余）
                record.getBorrowDate(), // 透传借出日期
                record.getDueDate(), // 透传应还日期
                record.getReturnDate(), // 透传归还日期
                record.getStatus(), // 透传借阅状态
                record.getRenewCount(), // 透传续借次数
                record.getFine(), // 透传罚款金额
                record.getRemark(), // 透传统备注
                isOverdue // 设置动态计算的逾期标志
        );
    }
}

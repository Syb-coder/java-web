// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.TestRecord;

/**
 * 答题记录响应 DTO
 * <p>
 * 用于个人中心展示答题历史。
 * </p>
 *
 * @param id           记录 ID
 * @param categoryId   分类 ID
 * @param categoryName 分类名称
 * @param totalCount   总题数
 * @param correctCount 答对题数
 * @param score        得分
 * @param createTime   答题时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；用于个人中心展示答题历史列表
public record TestRecordResponse(
        Long id,           // 答题记录主键 ID
        Long categoryId,   // 本次答题分类 ID，0 表示跨分类综合测试
        String categoryName,// 所属分类名称，由 Service 层关联查询后传入
        int totalCount,    // 总题数
        int correctCount,  // 答对题数
        int score,         // 本次得分
        String createTime  // 答题时间字符串
) {
    /**
     * 由答题记录实体构造响应
     *
     * @param record      答题记录实体
     * @param categoryName 分类名称
     * @return 答题记录响应
     */
    // 使用静态工厂方法 from()：分类名需外部传入，工厂方法集中处理实体到 DTO 的字段映射与时间格式化
    public static TestRecordResponse from(TestRecord record, String categoryName) {
        return new TestRecordResponse(
                record.getId(),
                record.getCategoryId(),
                categoryName,
                record.getTotalCount(),
                record.getCorrectCount(),
                record.getScore(),
                record.getCreateTime() != null ? record.getCreateTime().toString() : null
        );
    }
}

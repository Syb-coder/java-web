// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入图书实体类，用于 from 转换方法
import com.example.java1.model.Book;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 图书响应 DTO
 * <p>
 * 对外返回图书信息，避免直接暴露实体（防止版本号等内部字段泄露）。
 * </p>
 */
// 响应 DTO 不含校验注解，仅定义对外暴露的字段集合，隔离实体与 API 契约
public record BookResponse(
        Long id, // 图书主键 ID，前端用于借阅/修改请求定位
        String title, // 书名，列表与详情页展示
        String author, // 作者，检索结果展示
        String isbn, // ISBN 编号，详情页展示
        String category, // 分类，用于分类筛选标签
        String publisher, // 出版社，详情页展示
        Integer publishYear, // 出版年份，详情页展示
        String description, // 简介，详情页展示
        Integer totalCopies, // 总副本数，详情页展示馆藏总量
        Integer availableCopies, // 可借副本数，列表页展示是否可借（0 则禁用借书按钮）
        String location, // 存放位置，便于线下找书
        LocalDateTime updateTime // 最后修改时间
) {
    /**
     * 从实体构造响应
     *
     * @param book 图书实体
     * @return 响应 DTO
     */
    // 静态工厂方法优于构造方法，语义清晰且避免实体字段变更直接影响 DTO 构造
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(), // 透传主键
                book.getTitle(), // 透传书名
                book.getAuthor(), // 透传作者
                book.getIsbn(), // 透传 ISBN
                book.getCategory(), // 透传分类
                book.getPublisher(), // 透传出版社
                book.getPublishYear(), // 透传出版年份
                book.getDescription(), // 透传简介
                book.getTotalCopies(), // 透传总副本数
                book.getAvailableCopies(), // 透传可借副本数
                book.getLocation(), // 透传存放位置
                book.getUpdateTime() // 透传最后修改时间
        );
    }
}

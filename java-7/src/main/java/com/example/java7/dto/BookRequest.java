package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

import com.example.java7.model.BookCategory; // 引入图书分类枚举，作为 category 字段类型
import jakarta.validation.constraints.Min; // 引入 @Min 校验注解，校验数值最小值
import jakarta.validation.constraints.NotBlank; // 引入 @NotBlank 校验注解，校验字符串非空白
import jakarta.validation.constraints.NotNull; // 引入 @NotNull 校验注解，校验非 null

/**
 * 图书新增/编辑请求 DTO
 * <p>
 * 职责：接收前端图书表单数据，使用 Jakarta Validation 进行参数校验，
 * 避免无效数据进入业务层。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>DTO 与实体分离，防止前端直接操作 JPA 实体导致意外字段覆盖</li>
 *   <li>校验注解在 Controller 层通过 @Valid 触发，校验失败返回 400 Bad Request</li>
 *   <li>选填字段不添加校验注解，允许为 null</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class BookRequest { // 图书请求 DTO 类定义

    /** 书名，必填 */
    @NotBlank(message = "书名不能为空") // 校验注解：字符串非 null 且去除空白后长度大于 0，否则抛出校验异常
    private String title; // 书名字段，存储图书标题

    /** 作者，必填 */
    @NotBlank(message = "作者不能为空") // 校验注解：作者非空白字符串
    private String author; // 作者字段，存储图书作者

    /** ISBN，选填 */
    private String isbn; // ISBN 字段，国际标准书号，选填无校验

    /** 分类，必填 */
    @NotNull(message = "分类不能为空") // 校验注解：分类枚举非 null（枚举类型不能用 @NotBlank）
    private BookCategory category; // 分类字段，使用枚举约束取值范围

    /** 出版社，选填 */
    private String publisher; // 出版社字段，选填无校验

    /** 出版年份，选填 */
    private Integer publishYear; // 出版年份字段，使用 Integer 包装类允许为 null

    /** 简介，选填 */
    private String description; // 简介字段，存储图书描述信息

    /** 总馆藏数量，必填且 >= 1 */
    @NotNull(message = "馆藏数量不能为空") // 校验注解：馆藏数量非 null
    @Min(value = 1, message = "馆藏数量至少为 1") // 校验注解：馆藏数量最小值为 1，防止录入 0 或负数
    private Integer totalCopies; // 总馆藏数量字段，记录图书副本总数

    /** 存放位置，选填 */
    private String location; // 存放位置字段，记录图书在书架上的物理位置

    // ===== Getter / Setter =====

    public String getTitle() { // Getter 方法：返回书名
        return title; // 返回 title 字段值
    }

    public void setTitle(String title) { // Setter 方法：设置书名
        this.title = title; // 将入参赋值给实例字段 title
    }

    public String getAuthor() { // Getter 方法：返回作者
        return author; // 返回 author 字段值
    }

    public void setAuthor(String author) { // Setter 方法：设置作者
        this.author = author; // 将入参赋值给实例字段 author
    }

    public String getIsbn() { // Getter 方法：返回 ISBN
        return isbn; // 返回 isbn 字段值
    }

    public void setIsbn(String isbn) { // Setter 方法：设置 ISBN
        this.isbn = isbn; // 将入参赋值给实例字段 isbn
    }

    public BookCategory getCategory() { // Getter 方法：返回图书分类
        return category; // 返回 category 字段值
    }

    public void setCategory(BookCategory category) { // Setter 方法：设置图书分类
        this.category = category; // 将入参赋值给实例字段 category
    }

    public String getPublisher() { // Getter 方法：返回出版社
        return publisher; // 返回 publisher 字段值
    }

    public void setPublisher(String publisher) { // Setter 方法：设置出版社
        this.publisher = publisher; // 将入参赋值给实例字段 publisher
    }

    public Integer getPublishYear() { // Getter 方法：返回出版年份
        return publishYear; // 返回 publishYear 字段值
    }

    public void setPublishYear(Integer publishYear) { // Setter 方法：设置出版年份
        this.publishYear = publishYear; // 将入参赋值给实例字段 publishYear
    }

    public String getDescription() { // Getter 方法：返回简介
        return description; // 返回 description 字段值
    }

    public void setDescription(String description) { // Setter 方法：设置简介
        this.description = description; // 将入参赋值给实例字段 description
    }

    public Integer getTotalCopies() { // Getter 方法：返回总馆藏数量
        return totalCopies; // 返回 totalCopies 字段值
    }

    public void setTotalCopies(Integer totalCopies) { // Setter 方法：设置总馆藏数量
        this.totalCopies = totalCopies; // 将入参赋值给实例字段 totalCopies
    }

    public String getLocation() { // Getter 方法：返回存放位置
        return location; // 返回 location 字段值
    }

    public void setLocation(String location) { // Setter 方法：设置存放位置
        this.location = location; // 将入参赋值给实例字段 location
    }
}

package com.example.java7.dto; // 声明包路径，归属 dto 数据传输对象层

import com.example.java7.model.Book; // 引入 Book 实体类，用于构造响应对象
import com.example.java7.model.BookCategory; // 引入图书分类枚举，作为 category 字段类型

import java.time.LocalDateTime; // 引入 LocalDateTime，作为创建时间字段类型

/**
 * 图书响应 DTO
 * <p>
 * 职责：将 Book 实体转换为前端可用的展示对象，
 * 隐藏 version 等内部字段，仅暴露业务相关属性。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>响应 DTO 与实体分离，避免 JPA 懒加载导致的序列化问题</li>
 *   <li>通过构造函数从实体构建，确保字段映射集中管理</li>
 *   <li>新增 categoryDisplayName 冗余字段，避免前端再查枚举字典</li>
 * </ul>
 * </p>
 *
 * @author example
 */
public class BookResponse { // 图书响应 DTO 类定义

    /** 图书 ID */
    private Long id; // 图书唯一标识，对应数据库主键

    /** 书名 */
    private String title; // 书名字段，前端展示用

    /** 作者 */
    private String author; // 作者字段，前端展示用

    /** ISBN */
    private String isbn; // ISBN 字段，国际标准书号

    /** 分类枚举 */
    private BookCategory category; // 分类字段，枚举类型，用于业务判断

    /** 分类中文显示名 */
    private String categoryDisplayName; // 分类中文显示名，冗余字段，避免前端维护枚举字典

    /** 出版社 */
    private String publisher; // 出版社字段，前端展示用

    /** 出版年份 */
    private Integer publishYear; // 出版年份字段，使用 Integer 允许为 null

    /** 简介 */
    private String description; // 简介字段，存储图书描述

    /** 总馆藏数量 */
    private Integer totalCopies; // 总馆藏数量，记录副本总数

    /** 可借数量 */
    private Integer availableCopies; // 可借数量，记录当前可借副本数

    /** 存放位置 */
    private String location; // 存放位置字段，记录物理位置

    /** 创建时间 */
    private LocalDateTime createTime; // 创建时间字段，记录图书录入时间

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /**
     * 从实体构造响应对象
     * <p>
     * 通过构造函数完成实体到 DTO 的字段映射，集中管理转换逻辑，
     * 避免散落在各处的手动赋值。categoryDisplayName 通过枚举方法获取。
     * </p>
     *
     * @param book 图书实体
     */
    public BookResponse(Book book) { // 构造函数：接收 Book 实体进行字段映射
        this.id = book.getId(); // 映射图书 ID
        this.title = book.getTitle(); // 映射书名
        this.author = book.getAuthor(); // 映射作者
        this.isbn = book.getIsbn(); // 映射 ISBN
        this.category = book.getCategory(); // 映射分类枚举
        this.categoryDisplayName = book.getCategory() != null // 三元表达式：分类非 null 时获取显示名，否则设为 null，避免 NPE
                ? book.getCategory().getDisplayName() : null; // 调用枚举的 getDisplayName 方法获取中文显示名
        this.publisher = book.getPublisher(); // 映射出版社
        this.publishYear = book.getPublishYear(); // 映射出版年份
        this.description = book.getDescription(); // 映射简介
        this.totalCopies = book.getTotalCopies(); // 映射总馆藏数量
        this.availableCopies = book.getAvailableCopies(); // 映射可借数量
        this.location = book.getLocation(); // 映射存放位置
        this.createTime = book.getCreateTime(); // 映射创建时间
        this.updateTime = book.getUpdateTime();
    }

    // ===== Getter / Setter =====

    public Long getId() { // Getter 方法：返回图书 ID
        return id; // 返回 id 字段值
    }

    public void setId(Long id) { // Setter 方法：设置图书 ID
        this.id = id; // 将入参赋值给实例字段 id
    }

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

    public BookCategory getCategory() { // Getter 方法：返回分类枚举
        return category; // 返回 category 字段值
    }

    public void setCategory(BookCategory category) { // Setter 方法：设置分类枚举
        this.category = category; // 将入参赋值给实例字段 category
    }

    public String getCategoryDisplayName() { // Getter 方法：返回分类中文显示名
        return categoryDisplayName; // 返回 categoryDisplayName 字段值
    }

    public void setCategoryDisplayName(String categoryDisplayName) { // Setter 方法：设置分类中文显示名
        this.categoryDisplayName = categoryDisplayName; // 将入参赋值给实例字段 categoryDisplayName
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

    public Integer getAvailableCopies() { // Getter 方法：返回可借数量
        return availableCopies; // 返回 availableCopies 字段值
    }

    public void setAvailableCopies(Integer availableCopies) { // Setter 方法：设置可借数量
        this.availableCopies = availableCopies; // 将入参赋值给实例字段 availableCopies
    }

    public String getLocation() { // Getter 方法：返回存放位置
        return location; // 返回 location 字段值
    }

    public void setLocation(String location) { // Setter 方法：设置存放位置
        this.location = location; // 将入参赋值给实例字段 location
    }

    public LocalDateTime getCreateTime() { // Getter 方法：返回创建时间
        return createTime; // 返回 createTime 字段值
    }

    public void setCreateTime(LocalDateTime createTime) { // Setter 方法：设置创建时间
        this.createTime = createTime; // 将入参赋值给实例字段 createTime
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

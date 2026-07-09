// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

import java.time.LocalDateTime;

/**
 * 分类响应 DTO
 */
public class CategoryResponse {

    /** 分类 ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 排序序号 */
    private Integer sortOrder;

    /** 该分类下图书数量 */
    private long bookCount;

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法 */
    public CategoryResponse() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public long getBookCount() { return bookCount; }
    public void setBookCount(long bookCount) { this.bookCount = bookCount; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

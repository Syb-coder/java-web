// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 图书创建/修改请求 DTO
 * <p>
 * 后台管理端新增或修改图书时提交的数据。
 * </p>
 */
public class BookRequest {

    /** 书名 */
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名不能超过 200 个字符")
    private String title;

    /** 作者 */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者不能超过 100 个字符")
    private String author;

    /** ISBN（可选） */
    @Size(max = 20, message = "ISBN 不能超过 20 个字符")
    private String isbn;

    /** 分类 ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 出版社（可选） */
    @Size(max = 100, message = "出版社不能超过 100 个字符")
    private String publisher;

    /** 出版年份（可选） */
    @Min(value = 1000, message = "出版年份不合法")
    private Integer publishYear;

    /** 图书简介（可选） */
    @Size(max = 1000, message = "简介不能超过 1000 个字符")
    private String description;

    /** 封面图片 URL（可选） */
    @Size(max = 500, message = "封面图片 URL 不能超过 500 个字符")
    private String coverImage;

    /** 总馆藏数量 */
    @NotNull(message = "总数量不能为空")
    @Min(value = 1, message = "总数量至少为 1")
    private Integer totalCopies;

    /** 存放位置（可选） */
    @Size(max = 50, message = "存放位置不能超过 50 个字符")
    private String location;

    // ===== Getter / Setter =====
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public Integer getPublishYear() { return publishYear; }
    public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

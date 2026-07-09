// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

/**
 * 借阅车项响应 DTO
 * <p>
 * 返回给前端的借阅车项展示数据，包含图书基本信息。
 * </p>
 */
public class CartItemResponse {

    /** 借阅车项 ID */
    private Long id;

    /** 图书 ID */
    private Long bookId;

    /** 书名 */
    private String bookTitle;

    /** 作者 */
    private String bookAuthor;

    /** 分类名称 */
    private String categoryName;

    /** 图书封面 */
    private String coverImage;

    /** 可借数量 */
    private Integer availableCopies;

    /** 加入时间 */
    private String createTime;

    /** 无参构造方法 */
    public CartItemResponse() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}

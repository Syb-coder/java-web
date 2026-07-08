// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Announcement 实体类，用于构造响应 DTO
import com.example.java3.model.Announcement;

// 导入 DateTimeFormatter，用于将 LocalDateTime 格式化为前端可读字符串
import java.time.format.DateTimeFormatter;

/**
 * 公告响应 DTO
 * <p>
 * 返回给前端的公告数据，前端按 pinned 字段优先展示置顶公告。
 * </p>
 */
public class AnnouncementResponse {

    // 时间格式化器，统一格式为 yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 公告主键 ID
    private Long id;
    // 公告标题
    private String title;
    // 公告内容
    private String content;
    // 是否置顶标识
    private Boolean pinned;
    // 发布管理员 ID
    private Long adminId;
    // 发布时间字符串（已格式化）
    private String createdAt;

    /**
     * 由 Announcement 实体构造响应
     * <p>
     * 直接拷贝实体字段到 DTO，并将时间格式化为字符串便于前端展示。
     * </p>
     *
     * @param a 公告实体
     */
    public AnnouncementResponse(Announcement a) {
        this.id = a.getId();                                                // 赋值公告 ID
        this.title = a.getTitle();                                          // 赋值标题
        this.content = a.getContent();                                      // 赋值内容
        this.pinned = a.getPinned();                                        // 赋值置顶标识
        this.adminId = a.getAdminId();                                      // 赋值管理员 ID
        // 发布时间非空时格式化为字符串，否则置 null，避免 NPE
        this.createdAt = a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : null;
    }

    // 获取公告 ID
    public Long getId() {
        return id;
    }

    // 设置公告 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取标题
    public String getTitle() {
        return title;
    }

    // 设置标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取内容
    public String getContent() {
        return content;
    }

    // 设置内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取是否置顶标识
    public Boolean getPinned() {
        return pinned;
    }

    // 设置是否置顶标识
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    // 获取发布管理员 ID
    public Long getAdminId() {
        return adminId;
    }

    // 设置发布管理员 ID
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    // 获取发布时间字符串
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置发布时间字符串
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

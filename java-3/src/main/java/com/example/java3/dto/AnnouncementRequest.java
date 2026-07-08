// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 公告请求 DTO
 * <p>
 * 管理员发布公告时提交的数据，支持置顶配置。
 * </p>
 */
public class AnnouncementRequest {

    // 标题字段，必填，限制最长 100 字符
    /** 标题 */
    @NotBlank(message = "标题不能为空")
    // Size：限定标题最长 100 字符
    @Size(max = 100, message = "标题长度不能超过 100")
    private String title;

    // 内容字段，必填，限制最长 2000 字符
    /** 内容 */
    @NotBlank(message = "内容不能为空")
    // Size：限定内容最长 2000 字符
    @Size(max = 2000, message = "内容长度不能超过 2000")
    private String content;

    // 是否置顶字段，可选；true 表示置顶展示
    /** 是否置顶 */
    private Boolean pinned;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

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
}

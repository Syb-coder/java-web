// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 意见反馈请求 DTO
 * <p>
 * 学生用户向管理员提交意见反馈，管理员可在后台查看并回复。
 * </p>
 */
public class FeedbackRequest {

    // 标题字段，必填，限制最长 100 字符
    /** 标题 */
    @NotBlank(message = "标题不能为空")
    // Size：限定标题最长 100 字符
    @Size(max = 100, message = "标题长度不能超过 100")
    private String title;

    // 内容字段，必填，限制最长 1000 字符
    /** 内容 */
    @NotBlank(message = "内容不能为空")
    // Size：限定内容最长 1000 字符
    @Size(max = 1000, message = "内容长度不能超过 1000")
    private String content;

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
}

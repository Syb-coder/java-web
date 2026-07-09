// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.NotNull;

/**
 * 归还请求 DTO
 * <p>
 * 读者归还图书或管理员处理归还时提交的数据。
 * </p>
 */
public class ReturnRequest {

    /** 借阅记录 ID */
    @NotNull(message = "借阅记录 ID 不能为空")
    private Long recordId;

    // ===== Getter / Setter =====
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
}

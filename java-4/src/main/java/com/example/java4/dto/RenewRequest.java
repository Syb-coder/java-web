// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.NotNull;

/**
 * 续借请求 DTO
 * <p>
 * 读者对指定借阅记录发起续借，每次续借延长 15 天，最多续借 1 次。
 * </p>
 */
public class RenewRequest {

    /** 借阅记录 ID */
    @NotNull(message = "借阅记录 ID 不能为空")
    private Long recordId;

    // ===== Getter / Setter =====
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
}

// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 读者响应 DTO
 * <p>
 * 返回给前端（管理端）的读者展示数据，不含密码等敏感字段。
 * </p>
 */
public class ReaderResponse {

    /** 读者 ID */
    private Long id;

    /** 学号/工号 */
    private String readerNo;

    /** 姓名 */
    private String name;

    /** 读者类型：STUDENT/TEACHER */
    private String readerType;

    /** 所属院系 */
    private String department;

    /** 联系电话 */
    private String phone;

    /** 当前借阅数 */
    private Integer currentBorrowCount;

    /** 借阅上限 */
    private Integer maxBorrowCount;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法 */
    public ReaderResponse() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReaderNo() { return readerNo; }
    public void setReaderNo(String readerNo) { this.readerNo = readerNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getCurrentBorrowCount() { return currentBorrowCount; }
    public void setCurrentBorrowCount(Integer currentBorrowCount) { this.currentBorrowCount = currentBorrowCount; }
    public Integer getMaxBorrowCount() { return maxBorrowCount; }
    public void setMaxBorrowCount(Integer maxBorrowCount) { this.maxBorrowCount = maxBorrowCount; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}

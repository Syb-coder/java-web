// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回用户基本信息与会话令牌，前端据此存储登录态并展示用户信息。
 * </p>
 */
public class LoginResponse {

    /** 用户 ID */
    private Long id;

    /** 用户名/学号/工号 */
    private String username;

    /** 显示名称（读者姓名或管理员真实姓名） */
    private String displayName;

    /** 用户角色：reader=读者，admin=管理员 */
    private String role;

    /** 读者类型（仅读者登录时有值）：STUDENT/TEACHER */
    private String readerType;

    /** 所属院系（仅读者登录时有值） */
    private String department;

    /**
     * 构造方法
     *
     * @param id           用户 ID
     * @param username     用户名/学号/工号
     * @param displayName  显示名称
     * @param role         用户角色
     * @param readerType   读者类型（可空）
     * @param department   院系（可空）
     */
    public LoginResponse(Long id, String username, String displayName, String role,
                         String readerType, String department) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.readerType = readerType;
        this.department = department;
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}

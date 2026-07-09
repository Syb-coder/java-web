// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.Size;

/**
 * 读者个人信息修改请求 DTO
 * <p>
 * 读者在用户端自行修改个人信息时提交的数据，仅允许修改姓名、院系、电话，
 * 不允许修改学号/工号与读者类型（需管理员操作）。
 * </p>
 */
public class UserProfileRequest {

    /** 姓名 */
    @Size(max = 50, message = "姓名不能超过 50 个字符")
    private String name;

    /** 所属院系 */
    @Size(max = 50, message = "院系名称不能超过 50 个字符")
    private String department;

    /** 联系电话 */
    @Size(max = 20, message = "电话号码不能超过 20 个字符")
    private String phone;

    // ===== Getter / Setter =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

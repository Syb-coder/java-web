// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 读者注册请求 DTO
 * <p>
 * 用户端读者自助注册时提交的数据，后端校验后创建 Reader 实体。
 * </p>
 */
public class RegisterRequest {

    /** 学号/工号（唯一，作为登录账号） */
    @NotBlank(message = "学号/工号不能为空")
    @Size(min = 3, max = 20, message = "学号/工号长度需在 3-20 个字符之间")
    private String readerNo;

    /** 登录密码（明文，后端 BCrypt 加密后存储） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在 6-20 个字符之间")
    private String password;

    /** 读者姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过 50 个字符")
    private String name;

    /** 读者类型：STUDENT=学生，TEACHER=教师 */
    @NotBlank(message = "读者类型不能为空")
    @Pattern(regexp = "STUDENT|TEACHER", message = "读者类型必须为 STUDENT 或 TEACHER")
    private String readerType;

    /** 所属院系（可选） */
    @Size(max = 50, message = "院系名称不能超过 50 个字符")
    private String department;

    /** 联系电话（可选） */
    @Size(max = 20, message = "电话号码不能超过 20 个字符")
    private String phone;

    // ===== Getter / Setter =====
    public String getReaderNo() { return readerNo; }
    public void setReaderNo(String readerNo) { this.readerNo = readerNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

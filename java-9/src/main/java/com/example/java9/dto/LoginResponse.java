package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回账户基本信息与角色标识，
 * 前端据此决定跳转页面与菜单展示。
 * </p>
 */
public class LoginResponse {  // 登录响应 DTO 类定义,封装登录成功后返回给前端的数据

    /** 账户 ID */
    private Long id;  // 账户唯一标识(主键),使用 Long 包装类型以支持 null

    /** 用户名 */
    private String username;  // 登录账号名

    /** 显示名称（真实姓名/商户名） */
    private String displayName;  // 用于前端展示的名称:普通用户为真实姓名,商户为商户名称

    /** 账户角色：USER/MERCHANT/OPERATION/RISK */
    private String role;  // 账户角色枚举字符串:USER(普通用户)/MERCHANT(商户)/OPERATION(运营)/RISK(风控)

    public LoginResponse(Long id, String username, String displayName, String role) {  // 全参构造函数,便于一次性初始化所有字段
        this.id = id;  // 初始化账户 ID
        this.username = username;  // 初始化用户名
        this.displayName = displayName;  // 初始化显示名称
        this.role = role;  // 初始化角色
    }

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取账户 ID
        return id;  // 返回账户 ID
    }

    public void setId(Long id) {  // 设置账户 ID
        this.id = id;  // 赋值账户 ID
    }

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 赋值用户名
    }

    // —— displayName 字段的 getter/setter ——
    public String getDisplayName() {  // 获取显示名称
        return displayName;  // 返回显示名称
    }

    public void setDisplayName(String displayName) {  // 设置显示名称
        this.displayName = displayName;  // 赋值显示名称
    }

    // —— role 字段的 getter/setter ——
    public String getRole() {  // 获取角色
        return role;  // 返回角色字符串
    }

    public void setRole(String role) {  // 设置角色
        this.role = role;  // 赋值角色
    }
}

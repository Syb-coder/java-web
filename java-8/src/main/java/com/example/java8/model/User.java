// 声明包路径
package com.example.java8.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 前台用户实体
 * <p>
 * 注册并使用服装定制服务的消费者账号。密码使用 BCrypt 加密存储。
 * 一个用户可拥有多组量体数据（{@link Measurement}）、多个订单（{@link Order}）。
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 昵称，用于展示 */
    @Column(length = 50)
    private String nickname;

    /** 联系手机号（可选） */
    @Column(length = 20)
    private String phone;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public User() {
    }

    /**
     * 全参构造方法
     *
     * @param username 用户名
     * @param password 已 BCrypt 加密的密码
     * @param nickname 昵称
     * @param phone    手机号
     */
    public User(String username, String password, String nickname, String phone) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

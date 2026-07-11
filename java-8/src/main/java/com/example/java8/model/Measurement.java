// 声明包路径
package com.example.java8.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// 导入外键关联注解
import jakarta.persistence.JoinColumn;
// 导入多对一关系注解
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;
import jakarta.persistence.PreUpdate;

/**
 * 量体数据实体
 * <p>
 * 客户自助录入的身体尺寸数据。一个用户可拥有多组量体数据，
 * 如「日常版」「修身版」「宽松版」，下单时按需选择其一。
 * 字段命名遵循服装行业通用规范，单位均为 cm/kg。
 * </p>
 */
@Entity
@Table(name = "measurements")
public class Measurement {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户（多对一：多个量体数据属于一个用户） */
    // @ManyToOne 表示多个 Measurement 关联一个 User
    @ManyToOne
    // @JoinColumn 指定外键列名为 user_id，非空约束
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 数据名称，如「日常版」「修身版」 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 身高（cm） */
    private Double height;

    /** 体重（kg） */
    private Double weight;

    /** 颈围（cm）：喉结下方绕颈一周 */
    private Double neckCircumference;

    /** 肩宽（cm）：左右肩峰点之间的水平距离 */
    private Double shoulderWidth;

    /** 胸围（cm）：乳头水平绕胸一周 */
    private Double chestCircumference;

    /** 腰围（cm）：腰部最细处水平绕一周 */
    private Double waistCircumference;

    /** 臀围（cm）：臀部最丰满处水平绕一周 */
    private Double hipCircumference;

    /** 衣长（cm）：自颈侧根点垂至所需衣摆位置 */
    private Double clothesLength;

    /** 袖长（cm）：自肩峰点经手肘至手腕骨 */
    private Double sleeveLength;

    /** 裤长（cm）：自腰围线下垂至脚踝骨 */
    private Double pantsLength;

    /** 大腿围（cm）：大腿根部最粗处水平绕一周 */
    private Double thighCircumference;

    /** 备注：体型特征、习惯性着装松紧等 */
    @Column(length = 500)
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 无参构造方法 */
    public Measurement() {
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getNeckCircumference() { return neckCircumference; }
    public void setNeckCircumference(Double neckCircumference) { this.neckCircumference = neckCircumference; }
    public Double getShoulderWidth() { return shoulderWidth; }
    public void setShoulderWidth(Double shoulderWidth) { this.shoulderWidth = shoulderWidth; }
    public Double getChestCircumference() { return chestCircumference; }
    public void setChestCircumference(Double chestCircumference) { this.chestCircumference = chestCircumference; }
    public Double getWaistCircumference() { return waistCircumference; }
    public void setWaistCircumference(Double waistCircumference) { this.waistCircumference = waistCircumference; }
    public Double getHipCircumference() { return hipCircumference; }
    public void setHipCircumference(Double hipCircumference) { this.hipCircumference = hipCircumference; }
    public Double getClothesLength() { return clothesLength; }
    public void setClothesLength(Double clothesLength) { this.clothesLength = clothesLength; }
    public Double getSleeveLength() { return sleeveLength; }
    public void setSleeveLength(Double sleeveLength) { this.sleeveLength = sleeveLength; }
    public Double getPantsLength() { return pantsLength; }
    public void setPantsLength(Double pantsLength) { this.pantsLength = pantsLength; }
    public Double getThighCircumference() { return thighCircumference; }
    public void setThighCircumference(Double thighCircumference) { this.thighCircumference = thighCircumference; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    @PreUpdate
    public void onPreUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

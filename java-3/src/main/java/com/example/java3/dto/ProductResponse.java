// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Product 实体类，用于构造响应 DTO
import com.example.java3.model.Product;

// 导入 DateTimeFormatter，用于将 LocalDateTime 格式化为前端可读字符串
import java.time.format.DateTimeFormatter;

/**
 * 商品响应 DTO
 * <p>
 * 在 Product 实体基础上补充分类名称、卖家昵称、点赞/收藏数等冗余字段，便于前端展示。
 * </p>
 */
public class ProductResponse {

    // 时间格式化器，统一格式为 yyyy-MM-dd HH:mm:ss，供 createdAt 字段转换使用
    /** 时间格式化器 */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 商品 ID
    private Long id;
    // 商品标题
    private String title;
    // 商品描述
    private String description;
    // 期望价格
    private Double price;
    // 原价
    private Double originalPrice;
    // 图片 URL 字符串（多张分号分隔）
    private String images;
    // 新旧程度
    private String conditionLevel;
    // 分类 ID
    private Long categoryId;
    // 分类名称（冗余字段，由 Service 层补充）
    private String categoryName;
    // 卖家用户 ID
    private Long sellerId;
    // 卖家昵称（冗余字段，由 Service 层补充）
    private String sellerName;
    // 审核状态：PENDING/APPROVED/REJECTED
    private String auditStatus;
    // 审核备注（驳回原因）
    private String auditRemark;
    // 是否已售出
    private Boolean sold;
    // 点赞数（冗余字段，初始 0）
    private Long likeCount;
    // 收藏数（冗余字段，初始 0）
    private Long favoriteCount;
    // 创建时间字符串（已格式化）
    private String createdAt;
    // 最后修改时间字符串（已格式化）
    private String updateTime;

    /**
     * 由实体构造基础响应（不含分类名/卖家名/点赞数，由 Service 层补充）
     * <p>
     * 将 Product 实体字段拷贝到 DTO，并将枚举类型转换为字符串便于前端解析。
     * </p>
     *
     * @param p 商品实体
     */
    public ProductResponse(Product p) {
        this.id = p.getId();                                            // 赋值商品 ID
        this.title = p.getTitle();                                      // 赋值标题
        this.description = p.getDescription();                          // 赋值描述
        this.price = p.getPrice();                                      // 赋值期望价格
        this.originalPrice = p.getOriginalPrice();                      // 赋值原价
        this.images = p.getImages();                                    // 赋值图片 URL
        this.conditionLevel = p.getConditionLevel();                    // 赋值新旧程度
        this.categoryId = p.getCategoryId();                            // 赋值分类 ID
        this.sellerId = p.getSellerId();                                // 赋值卖家 ID
        // 审核状态枚举转字符串，前端据此判断是否显示上架/驳回标识
        this.auditStatus = p.getAuditStatus().name();
        this.auditRemark = p.getAuditRemark();                          // 赋值审核备注
        this.sold = p.getSold();                                        // 赋值是否售出
        this.likeCount = 0L;                                            // 点赞数初始为 0，由 Service 层查询后填充
        this.favoriteCount = 0L;                                        // 收藏数初始为 0，由 Service 层查询后填充
        // 创建时间非空时格式化为字符串，否则置 null，避免 NPE
        this.createdAt = p.getCreatedAt() != null ? p.getCreatedAt().format(FMT) : null;
        // 最后修改时间非空时格式化，否则置 null，避免 NPE
        this.updateTime = p.getUpdateTime() != null ? p.getUpdateTime().format(FMT) : null;
    }

    // 获取商品 ID
    public Long getId() {
        return id;
    }

    // 设置商品 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取商品标题
    public String getTitle() {
        return title;
    }

    // 设置商品标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取商品描述
    public String getDescription() {
        return description;
    }

    // 设置商品描述
    public void setDescription(String description) {
        this.description = description;
    }

    // 获取期望价格
    public Double getPrice() {
        return price;
    }

    // 设置期望价格
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取原价
    public Double getOriginalPrice() {
        return originalPrice;
    }

    // 设置原价
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    // 获取图片 URL 字符串
    public String getImages() {
        return images;
    }

    // 设置图片 URL 字符串
    public void setImages(String images) {
        this.images = images;
    }

    // 获取新旧程度
    public String getConditionLevel() {
        return conditionLevel;
    }

    // 设置新旧程度
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    // 获取分类 ID
    public Long getCategoryId() {
        return categoryId;
    }

    // 设置分类 ID
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    // 获取分类名称
    public String getCategoryName() {
        return categoryName;
    }

    // 设置分类名称
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // 获取卖家用户 ID
    public Long getSellerId() {
        return sellerId;
    }

    // 设置卖家用户 ID
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    // 获取卖家昵称
    public String getSellerName() {
        return sellerName;
    }

    // 设置卖家昵称
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    // 获取审核状态字符串
    public String getAuditStatus() {
        return auditStatus;
    }

    // 设置审核状态字符串
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    // 获取审核备注
    public String getAuditRemark() {
        return auditRemark;
    }

    // 设置审核备注
    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    // 获取是否售出标识
    public Boolean getSold() {
        return sold;
    }

    // 设置是否售出标识
    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    // 获取点赞数
    public Long getLikeCount() {
        return likeCount;
    }

    // 设置点赞数
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    // 获取收藏数
    public Long getFavoriteCount() {
        return favoriteCount;
    }

    // 设置收藏数
    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    // 获取创建时间字符串
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置创建时间字符串
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // 获取最后修改时间
    public String getUpdateTime() {
        return updateTime;
    }

    // 设置最后修改时间
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

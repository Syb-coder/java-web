// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Order 实体类，用于构造响应 DTO
import com.example.java3.model.Order;

// 导入 DateTimeFormatter，用于将 LocalDateTime 格式化为前端可读字符串
import java.time.format.DateTimeFormatter;

/**
 * 订单响应 DTO
 * <p>
 * 返回给前端的订单数据，补齐商品标题、买卖双方昵称等冗余字段，便于订单列表展示。
 * </p>
 */
public class OrderResponse {

    // 时间格式化器，统一格式为 yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 订单主键 ID
    private Long id;
    // 订单编号（业务唯一标识，前端展示用）
    private String orderNo;
    // 商品 ID
    private Long productId;
    // 商品标题（冗余字段）
    private String productTitle;
    // 买家用户 ID
    private Long buyerId;
    // 买家昵称（冗余字段）
    private String buyerName;
    // 卖家用户 ID
    private Long sellerId;
    // 卖家昵称（冗余字段）
    private String sellerName;
    // 成交价格
    private Double price;
    // 订单状态：PENDING/PAID/COMPLETED/CANCELLED 等
    private String status;
    // 买家留言
    private String buyerRemark;
    // 纠纷处理备注
    private String disputeRemark;
    // 下单时间字符串（已格式化）
    private String createdAt;
    // 完成时间字符串（已格式化）
    private String completedAt;
    // 最后修改时间字符串（已格式化）
    private String updateTime;

    /**
     * 由 Order 实体构造响应
     * <p>
     * Service 层查询商品标题、买卖双方昵称后传入，避免前端多次请求关联信息。
     * </p>
     *
     * @param o            订单实体
     * @param productTitle 商品标题
     * @param buyerName    买家昵称
     * @param sellerName   卖家昵称
     */
    public OrderResponse(Order o, String productTitle, String buyerName, String sellerName) {
        this.id = o.getId();                                                // 赋值订单 ID
        this.orderNo = o.getOrderNo();                                      // 赋值订单编号
        this.productId = o.getProductId();                                  // 赋值商品 ID
        this.productTitle = productTitle;                                   // 赋值商品标题
        this.buyerId = o.getBuyerId();                                      // 赋值买家 ID
        this.buyerName = buyerName;                                         // 赋值买家昵称
        this.sellerId = o.getSellerId();                                    // 赋值卖家 ID
        this.sellerName = sellerName;                                       // 赋值卖家昵称
        this.price = o.getPrice();                                          // 赋值成交价格
        // 订单状态枚举转字符串，前端据此渲染状态标签
        this.status = o.getStatus().name();
        this.buyerRemark = o.getBuyerRemark();                              // 赋值买家留言
        this.disputeRemark = o.getDisputeRemark();                          // 赋值纠纷备注
        // 下单时间非空时格式化，否则置 null，避免 NPE
        this.createdAt = o.getCreatedAt() != null ? o.getCreatedAt().format(FMT) : null;
        // 完成时间非空时格式化，否则置 null，避免 NPE（订单未完成时为 null）
        this.completedAt = o.getCompletedAt() != null ? o.getCompletedAt().format(FMT) : null;
        // 最后修改时间非空时格式化，否则置 null
        this.updateTime = o.getUpdateTime() != null ? o.getUpdateTime().format(FMT) : null;
    }

    // 获取订单 ID
    public Long getId() {
        return id;
    }

    // 设置订单 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取订单编号
    public String getOrderNo() {
        return orderNo;
    }

    // 设置订单编号
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // 获取商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取商品标题
    public String getProductTitle() {
        return productTitle;
    }

    // 设置商品标题
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    // 获取买家用户 ID
    public Long getBuyerId() {
        return buyerId;
    }

    // 设置买家用户 ID
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    // 获取买家昵称
    public String getBuyerName() {
        return buyerName;
    }

    // 设置买家昵称
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
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

    // 获取成交价格
    public Double getPrice() {
        return price;
    }

    // 设置成交价格
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取订单状态字符串
    public String getStatus() {
        return status;
    }

    // 设置订单状态字符串
    public void setStatus(String status) {
        this.status = status;
    }

    // 获取买家留言
    public String getBuyerRemark() {
        return buyerRemark;
    }

    // 设置买家留言
    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    // 获取纠纷处理备注
    public String getDisputeRemark() {
        return disputeRemark;
    }

    // 设置纠纷处理备注
    public void setDisputeRemark(String disputeRemark) {
        this.disputeRemark = disputeRemark;
    }

    // 获取下单时间字符串
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置下单时间字符串
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // 获取完成时间字符串
    public String getCompletedAt() {
        return completedAt;
    }

    // 设置完成时间字符串
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
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

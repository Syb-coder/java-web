package com.example.java3.dto;

import com.example.java3.model.Order;

import java.time.format.DateTimeFormatter;

/**
 * 订单响应 DTO
 */
public class OrderResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String orderNo;
    private Long productId;
    private String productTitle;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private Double price;
    private String status;
    private String buyerRemark;
    private String disputeRemark;
    private String createdAt;
    private String completedAt;

    public OrderResponse(Order o, String productTitle, String buyerName, String sellerName) {
        this.id = o.getId();
        this.orderNo = o.getOrderNo();
        this.productId = o.getProductId();
        this.productTitle = productTitle;
        this.buyerId = o.getBuyerId();
        this.buyerName = buyerName;
        this.sellerId = o.getSellerId();
        this.sellerName = sellerName;
        this.price = o.getPrice();
        this.status = o.getStatus().name();
        this.buyerRemark = o.getBuyerRemark();
        this.disputeRemark = o.getDisputeRemark();
        this.createdAt = o.getCreatedAt() != null ? o.getCreatedAt().format(FMT) : null;
        this.completedAt = o.getCompletedAt() != null ? o.getCompletedAt().format(FMT) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getDisputeRemark() {
        return disputeRemark;
    }

    public void setDisputeRemark(String disputeRemark) {
        this.disputeRemark = disputeRemark;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}

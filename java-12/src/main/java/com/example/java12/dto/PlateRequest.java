package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 创建/编辑板块请求 DTO
 */
public class PlateRequest {

    /** 板块名称（唯一） */
    @NotBlank(message = "板块名称不能为空")
    @Size(min = 1, max = 30, message = "板块名称长度需为1-30字符")
    private String name;

    /** 板块描述 */
    @Size(max = 500, message = "描述最长500字符")
    private String description;

    /** 板块图标 URL */
    @Size(max = 500, message = "图标URL过长")
    private String icon;

    /** 排序序号 */
    private Integer sortOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

package com.SWP391.G3PCoffee.model;

import lombok.Data;

@Data
public class ProductSalesDTO {
    private Integer productId;
    private String productName;
    private Long totalQuantity;

    public ProductSalesDTO(Integer productId, String productName, Long totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }
}
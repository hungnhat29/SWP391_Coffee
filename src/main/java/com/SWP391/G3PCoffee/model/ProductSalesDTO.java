package com.SWP391.G3PCoffee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO implements Serializable {
    private Integer productId;
    private String productName;
    private Long totalQuantity;
}
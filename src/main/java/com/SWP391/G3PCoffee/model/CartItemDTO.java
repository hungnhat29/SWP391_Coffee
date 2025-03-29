package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CartItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String sizeInfo;
    private String toppingsInfo;
    private Integer categoryId;
    private Boolean isPromotionalItem = false;
    private Integer relatedPromotionId;
}
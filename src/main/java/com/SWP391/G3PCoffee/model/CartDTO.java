package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private Integer userId;
    private String sessionId;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String couponCode;
    private List<AppliedPromotionDTO> appliedPromotions = new ArrayList<>();
}
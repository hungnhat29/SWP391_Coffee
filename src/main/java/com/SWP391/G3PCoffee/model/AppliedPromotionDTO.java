package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AppliedPromotionDTO {
    private Integer promotionId;
    private String promotionName;
    private String promotionType;
    private String description;
    private BigDecimal discountAmount;
    private String couponCode;
    private Boolean isNotification = false;
    private Integer freeProductId;
    private Integer freeProductQuantity;
}
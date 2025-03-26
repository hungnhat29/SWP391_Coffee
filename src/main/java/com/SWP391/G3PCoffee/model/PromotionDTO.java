package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class PromotionDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private String promotionType;
    private Integer priority;
    private Boolean stackable;
    private Integer usageLimit;
    private Integer usageCount;
    private List<PromotionRuleDTO> rules = new ArrayList<>();
    private List<PromotionActionDTO> actions = new ArrayList<>();
    private List<PromotionCouponDTO> coupons = new ArrayList<>();
    // Add this property to PromotionDTO.java
    private List<String> couponCodes = new ArrayList<>();
}

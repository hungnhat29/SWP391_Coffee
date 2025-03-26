package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active = true;
    private String promotionType;
    private Integer priority = 0;
    private Boolean stackable = false;
    private Integer usageLimit;
    private List<PromotionRuleDTO> rules = new ArrayList<>();
    private List<PromotionActionDTO> actions = new ArrayList<>();
    private List<String> couponCodes = new ArrayList<>();
}
package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class PromotionRuleDTO {
    private Integer id;
    private String ruleType;
    private Map<String, Object> ruleData;

}
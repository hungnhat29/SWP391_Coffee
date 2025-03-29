package com.SWP391.G3PCoffee.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPromotionDTO {
    private Integer id;
    private Integer userId;
    private Integer promotionId;  // hoặc PromotionDTO nếu muốn chi tiết
    private String promotionName;
    private String promotionDescription;
    private String promotionType;
    private Integer usageCount;
    private Boolean isRedeemed;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
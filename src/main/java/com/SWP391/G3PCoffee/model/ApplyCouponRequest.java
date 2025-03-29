package com.SWP391.G3PCoffee.model;

import lombok.Data;

@Data
public class ApplyCouponRequest {
    private String couponCode;
    private Integer userId;
    private String sessionId;
}

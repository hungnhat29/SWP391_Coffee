package com.SWP391.G3PCoffee.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class MemberShipResponse {
    private String name;
    private String email;
    private String phone;
    private Long userId;
    private String rank;
    private Integer point;
}

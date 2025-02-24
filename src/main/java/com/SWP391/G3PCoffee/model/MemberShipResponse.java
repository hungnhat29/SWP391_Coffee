package com.SWP391.G3PCoffee.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberShipResponse {
    private String name;
    private String email;
    private String phone;
    private Long userId;
    private String rank;
    private Integer point;
}

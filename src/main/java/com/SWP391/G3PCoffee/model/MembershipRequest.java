package com.SWP391.G3PCoffee.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class MembershipRequest {
    private String rank;
    private List<Long> listUserId;
}

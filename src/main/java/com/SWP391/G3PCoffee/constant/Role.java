package com.SWP391.G3PCoffee.constant;

import lombok.Getter;

@Getter
public enum Role {
    CUSTOMER("customer"),
    ADMIN("admin"),
    MANAGER("manager");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

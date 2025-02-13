package com.SWP391.G3PCoffee.model;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private String confirmPassword;
}

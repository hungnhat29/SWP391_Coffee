package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.UserRegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/auth/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "register";
    }

    @GetMapping("/change_password")
    public String showChangePassPage() {
        return "change_password";
    }

}

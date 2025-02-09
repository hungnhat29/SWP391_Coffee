package com.SWP391.G3PCoffee.controller.Authentication;

import com.SWP391.G3PCoffee.security.JwtUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtils jwtUtil;

    public AuthController(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(username, password, new java.util.ArrayList<>()));
        model.addAttribute("token", token);
        return "dashboard";
    }
}

package com.SWP391.G3PCoffee.controller.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    @GetMapping("/profile")
    public String showProfilePage(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "redirect:/auth/login";
        }

        return "profile";
    }
}

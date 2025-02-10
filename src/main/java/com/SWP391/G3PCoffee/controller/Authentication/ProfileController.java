package com.SWP391.G3PCoffee.controller.Authentication;

import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        User user = userService.getCustomerByEmail(userDetails.getUsername());
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "profile";

    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("user") User updatedUser, Model model) {
        if (userDetails == null) return "redirect:/auth/login";

        User existingUser = userService.updateUser(userDetails, updatedUser);
        if (existingUser != null) {
            model.addAttribute("user", existingUser);
            model.addAttribute("successMessage", "Profile updated successfully!");

            return "profile";
        }

        return "redirect:/auth/login";
    }

}

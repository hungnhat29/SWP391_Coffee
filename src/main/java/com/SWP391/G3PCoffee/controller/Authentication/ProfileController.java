package com.SWP391.G3PCoffee.controller.Authentication;

import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            User user = userService.getCustomerByEmail(userDetails.getUsername());

            if (user != null) {
                model.addAttribute("user", user);
                return "profile";
            }
        }

        return "redirect:/auth/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            User existingUser = userService.updateUser(userDetails, updatedUser);
            if (existingUser != null) {
                model.addAttribute("user", existingUser);
                model.addAttribute("successMessage", "Profile updated successfully!");

                return "profile";
            }
        }

        return "redirect:/auth/login";
    }

}

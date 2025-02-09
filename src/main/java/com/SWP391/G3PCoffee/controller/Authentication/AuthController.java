package com.SWP391.G3PCoffee.controller.Authentication;

import com.SWP391.G3PCoffee.DTO.user.UserLoginDto;
import com.SWP391.G3PCoffee.DTO.user.UserRegisterDto;
import com.SWP391.G3PCoffee.security.JwtUtils;
import com.SWP391.G3PCoffee.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtils jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtils jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDto userDTO) {
        String jwtToken = userService.registerUser(userDTO);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("message", "Registration successful!");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDto loginDto) {
        Map<String, Object> response = userService.loginByEmailAndPassword(loginDto);
        return ResponseEntity.ok(response);
    }
}

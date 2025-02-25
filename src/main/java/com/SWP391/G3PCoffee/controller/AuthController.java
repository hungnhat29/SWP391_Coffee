package com.SWP391.G3PCoffee.controller;


import com.SWP391.G3PCoffee.model.UserLoginDto;
import com.SWP391.G3PCoffee.model.UserRegisterDto;
import com.SWP391.G3PCoffee.security.JwtAuthenticationFilter;
import com.SWP391.G3PCoffee.security.JwtUtils;
import com.SWP391.G3PCoffee.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtUtils jwtUtil;
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public AuthController(JwtUtils jwtUtil, UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDto userDTO, HttpServletResponse response) {
        try {
            String jwtToken = userService.registerUser(userDTO);

            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 ngày
            response.addCookie(cookie);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Registration successful!");
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto, HttpServletResponse response) {
        try {
            Map<String, String> loginData = userService.loginByEmailAndPassword(loginDto);

            String jwtToken = loginData.get("token");
            String role = loginData.get("role");

            // Tạo cookie JWT
            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // Cookie hết hạn sau 1 ngày
            response.addCookie(cookie);

            // Tạo phản hồi thành công
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful!");
            responseBody.put("role", role);

            return ResponseEntity.ok(responseBody);

        } catch (ResponseStatusException e) {
            // Trả về lỗi từ loginByEmailAndPassword
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/check-login")
    public ResponseEntity<Boolean> checkLogin(@AuthenticationPrincipal UserDetails userDetails,
                                              HttpServletRequest request) {
        if (userDetails == null) return ResponseEntity.ok(false);
        String jwtToken = jwtAuthenticationFilter.extractJwtFromCookies(request);
        if (jwtToken == null) return ResponseEntity.ok(false);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestBody Map<String, String> request) {
        String email = userDetails.getUsername();
        String password = request.get("password");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("cfPassword");
        logger.info("Attempting to change password for user: {}", password);
        Map<String, String> response = userService.changePassword(password, newPassword, confirmPassword, email);
        return ResponseEntity.ok(response);
    }

}

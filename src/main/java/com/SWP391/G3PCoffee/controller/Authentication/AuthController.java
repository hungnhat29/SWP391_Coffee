package com.SWP391.G3PCoffee.controller.Authentication;

import com.SWP391.G3PCoffee.DTO.user.UserLoginDto;
import com.SWP391.G3PCoffee.DTO.user.UserRegisterDto;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
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
        String jwtToken = userService.registerUser(userDTO);

        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Registration successful!");

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto, HttpServletResponse response) {
        Map<String, String> loginData = userService.loginByEmailAndPassword(loginDto);

        String jwtToken = loginData.get("token");
        String role = loginData.get("role");

        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful!");
        responseBody.put("role", role);

        return ResponseEntity.ok(responseBody);
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

}

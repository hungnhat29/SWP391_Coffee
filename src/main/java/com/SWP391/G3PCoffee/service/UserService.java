/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.service;

/**
 * @author hungp
 */
//import com.SWP391.G3PCoffee.dto.UserDTO;

import com.SWP391.G3PCoffee.DTO.user.UserLoginDto;
import com.SWP391.G3PCoffee.DTO.user.UserRegisterDto;
import com.SWP391.G3PCoffee.constant.Role;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.UserRepository;
import com.SWP391.G3PCoffee.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> "customer".equalsIgnoreCase(user.getName()))
                .toList();
    }

    public User getCustomerById(Long id) {
        return userRepository.findById(id)
                .filter(user -> Role.CUSTOMER.getValue().equalsIgnoreCase(user.getName()))
                .orElse(null);
    }

    public String registerUser(UserRegisterDto userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent() || userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setRole(Role.CUSTOMER.getValue());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtils.generateToken(userDetails);
    }

    public Map<String, Object> loginByEmailAndPassword(UserLoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");

        return response;
    }

    public User getCustomerByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> Role.CUSTOMER.getValue().equalsIgnoreCase(user.getRole()))
                .orElse(null);
    }
}

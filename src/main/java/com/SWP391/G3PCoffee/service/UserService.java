/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.service;

/**
 * @author hungp
 */
//import com.SWP391.G3PCoffee.dto.UserDTO;

import com.SWP391.G3PCoffee.model.UserLoginDto;
import com.SWP391.G3PCoffee.model.UserRegisterDto;
import com.SWP391.G3PCoffee.model.Role;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.UserRepository;
import com.SWP391.G3PCoffee.security.JwtUtils;
import com.SWP391.G3PCoffee.service.member_ship.MembershipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MembershipService membershipService;
    private final EmailContactService emailContactService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       UserDetailsService userDetailsService, AuthenticationManager authenticationManager, MembershipService membershipService, EmailContactService emailContactService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.membershipService = membershipService;
        this.emailContactService = emailContactService;
    }

    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> "customer".equalsIgnoreCase(user.getRole()))
                .toList();
    }

    public User getCustomerById(Long id) {
        return userRepository.findById(id)
                .filter(user -> Role.CUSTOMER.getValue().equalsIgnoreCase(user.getRole()))
                .orElse(null);
    }

    @Transactional
    public String registerUser(UserRegisterDto userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent() || userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        boolean verifyOtp = emailContactService.verifyOtp(userDTO.getEmail(), userDTO.getOtp());
        if(!verifyOtp){
            throw new RuntimeException("Invalid OTP!");
        }

        User user = new User();
        user.setName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setRole(Role.CUSTOMER.getValue());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

//        membershipService.initMemberShip(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtils.generateToken(userDetails);
    }

    public Map<String, String> loginByEmailAndPassword(UserLoginDto loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(), loginDto.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
            String token = jwtUtils.generateToken(userDetails);

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(Role.CUSTOMER.getValue());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);

            return response;
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu!");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi!");
        }
    }

    public User getCustomerByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> Role.CUSTOMER.getValue().equalsIgnoreCase(user.getRole()))
                .orElse(null);
    }

    @Transactional
    public User updateUser(UserDetails userDetails, User updatedUser) {
        User existingUser = getCustomerByEmail(userDetails.getUsername());

        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            userRepository.save(existingUser);
            return existingUser;
        }
        return null;
    }

    public Map<String, String> changePassword(String oldPassword, String newPassword,
                                              String confirmPassword, String email) {
        Map<String, String> response = new HashMap<>();
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            response.put("message", "Vui lòng điền đầy đủ tất cả các trường!");
            response.put("messageType", "error");
            return response;
        }

        User user = getCustomerByEmail(email);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            response.put("message", "Mật khẩu cũ không đúng!");
            response.put("messageType", "error");
            return response;
        }

        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            response.put("message", "Mật khẩu mới phải có ít nhất 6 ký tự và bao gồm cả chữ và số!");
            response.put("messageType", "error");
            return response;
        }


        if (!newPassword.equals(confirmPassword)) {
            response.put("message", "Mật khẩu mới không khớp!");
            response.put("messageType", "error");
            return response;
        }

        Objects.requireNonNull(user).setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        response.put("message", "Đổi mật khẩu thành công!");
        response.put("messageType", "success");

        return response;
    }
}

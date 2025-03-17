/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.service;

/**
 * @author hungp
 */
//import com.SWP391.G3PCoffee.dto.UserDTO;

import com.SWP391.G3PCoffee.model.*;
import com.SWP391.G3PCoffee.repository.UserRepository;
import com.SWP391.G3PCoffee.security.JwtUtils;
import jakarta.mail.MessagingException;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final EmailContactService emailContactService;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private Map<String, String> otpCache = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private final Set<String> verifiedEmails = new HashSet<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       UserDetailsService userDetailsService, EmailContactService emailContactService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.emailContactService = emailContactService;
        this.authenticationManager = authenticationManager;
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

        boolean verity = emailContactService.verifyOtp(userDTO.getEmail(), userDTO.getOtp());
        if(!verity){
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn!");
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

    public List<User> getAllCustomerByListUserId(List<Long> listUserId) {
        return userRepository.getListUserByListUserId(listUserId).stream()
                .filter(user -> "customer".equalsIgnoreCase(user.getRole()))
                .toList();
    }

    // Gửi OTP qua email
    public Map<String, String> sendOtp(String email) throws MessagingException {
        Map<String, String> response = new HashMap<>();
        User user = getCustomerByEmail(email);

        log.info("Fetching user with email: {}", user);

        if (user == null) {
            response.put("message", "Thông tin không tồn tại trong hệ thống.");
            response.put("messageType", "error");
            return response;
        }

        // Tạo OTP ngẫu nhiên (6 chữ số)
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Lưu OTP và thời gian hết hạn (10 phút)
        otpCache.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + (5 * 60 * 1000)); // 10 phút

        // Xóa OTP sau 10 phút
        scheduler.schedule(() -> {
            otpCache.remove(email);
            otpExpiry.remove(email);
        }, 5, TimeUnit.MINUTES);

        String body = "<p>Chào bạn,</p>"
                + "<p>Mã OTP để xác minh email của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã OTP có hiệu lực trong 5 phút. Vui lòng không cung cấp otp cho bất kì ai!</p>"
                + "<br/><p>Trân trọng,</p>";

        // Gửi email
        ContactRequest request = new ContactRequest();
        request.setEmail(email);
        request.setSubject("Mã OTP đặt lại mật khẩu");
        request.setMessage(body);
        emailContactService.SendMail(request);

        response.put("message", "Mã OTP đã được gửi tới email của bạn. Hãy kiểm tra hộp thư!");
        response.put("messageType", "success");
        return response;
    }

    // Xác minh OTP
    public Map<String, String> verifyOtp(String email, String otp) {
        Map<String, String> response = new HashMap<>();
        if (otpCache.containsKey(email) &&
                otpCache.get(email).equals(otp) &&
                System.currentTimeMillis() <= otpExpiry.getOrDefault(email, 0L)) {

            verifiedEmails.add(email); // Đánh dấu email đã xác minh
            response.put("message", "OTP hợp lệ. Bạn có thể đặt lại mật khẩu.");
            response.put("messageType", "success");
        } else {
            response.put("message", "OTP không hợp lệ hoặc đã hết hạn!");
            response.put("messageType", "error");
        }
        return response;
    }

    // Đặt lại mật khẩu
    public Map<String, String> resetPassword(String email, String newPassword, String confirmPassword) {
        Map<String, String> response = new HashMap<>();
        User user = getCustomerByEmail(email);

        if (user == null) {
            response.put("message", "Thông tin không tồn tại trong hệ thống.");
            response.put("messageType", "error");
            return response;
        }

        // Kiểm tra email đã xác minh OTP chưa
        if (!verifiedEmails.contains(email)) {
            response.put("message", "Bạn chưa xác minh OTP!");
            response.put("messageType", "error");
            return response;
        }

        // Kiểm tra độ mạnh của mật khẩu mới
        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            response.put("message", "Mật khẩu mới phải có ít nhất 6 ký tự và bao gồm cả chữ và số!");
            response.put("messageType", "error");
            return response;
        }

        // Kiểm tra xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            response.put("message", "Mật khẩu mới không khớp!");
            response.put("messageType", "error");
            return response;
        }

        // Kiểm tra mật khẩu mới không trùng với mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            response.put("message", "Mật khẩu mới không được trùng với mật khẩu cũ!");
            response.put("messageType", "error");
            return response;
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa trạng thái xác minh
        verifiedEmails.remove(email);

        response.put("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.");
        response.put("messageType", "success");
        return response;
    }

}
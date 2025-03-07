package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.ContactRequest;
import com.SWP391.G3PCoffee.service.EmailContactService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private EmailContactService emailContactService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody ContactRequest request) {
        try {
            emailContactService.sendContactEmail(request);
            return ResponseEntity.ok("Email sent successfully!");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        try {
            emailContactService.sendVerificationOtp(email);
            return ResponseEntity.ok ("OTP đã được gửi đến email: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }
}
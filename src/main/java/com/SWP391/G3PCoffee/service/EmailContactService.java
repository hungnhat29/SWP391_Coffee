package com.SWP391.G3PCoffee.service;

/**
 *
 * @author hungp
 */

import com.SWP391.G3PCoffee.model.ContactRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EmailContactService {

    @Autowired
    private JavaMailSender mailSender;
    private Map<String, String> otpCache = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void sendContactEmail(ContactRequest request) throws MessagingException {
        String toEmail = "hungpoporo@gmail.com"; // Email nhận thông báo
        String subject = "New Contact Message from " + request.getName();
        String content = "<h2>Contact Form Submission</h2>"
                + "<p><b>Name:</b> " + request.getName() + "</p>"
                + "<p><b>Email:</b> " + request.getEmail() + "</p>"
                + "<p><b>Subject:</b> " + request.getSubject() + "</p>"
                + "<p><b>Message:</b><br/>" + request.getMessage() + "</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // `true` để hỗ trợ HTML trong email

        mailSender.send(message);
    }

    public void SendMail(ContactRequest request) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
        helper.setTo(request.getEmail());
        helper.setSubject(request.getSubject());
        helper.setText(request.getMessage(), true); // `true` để hỗ trợ HTML trong email

        mailSender.send(message);
    }

    public void sendVerificationOtp(String email) throws MessagingException {
        String otp = generateOtp();
        // Lưu OTP vào cache
        otpCache.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + (5 * 60 * 1000));

        scheduler.schedule(() -> {
            otpCache.remove(email);
            otpExpiry.remove(email);
        }, 5, TimeUnit.MINUTES);

        // Nội dung email
        String subject = "Xác minh email - Mã OTP của bạn";
        String body = "<p>Chào bạn,</p>"
                + "<p>Mã OTP để xác minh email của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã OTP có hiệu lực trong 5 phút.Vui lòng nhập mã này để hoàn tất quá trình đăng ký.</p>"
                + "<br/><p>Trân trọng,</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(body, true); // `true` để hỗ trợ HTML trong email

        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String otpInput) {
        String cachedOtp = otpCache.get(email);
        if(cachedOtp != null && cachedOtp.equals(otpInput)) {
            // Sau khi xác minh thành công, có thể xóa OTP khỏi cache
//            otpCache.remove(email);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}

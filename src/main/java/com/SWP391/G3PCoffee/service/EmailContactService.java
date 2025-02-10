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

@Service
public class EmailContactService {

    @Autowired
    private JavaMailSender mailSender;

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
}

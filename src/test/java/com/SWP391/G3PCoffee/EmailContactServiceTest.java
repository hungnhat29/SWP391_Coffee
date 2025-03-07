package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.ContactRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailContactServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailContactService emailContactService;

    private ContactRequest contactRequest;

    @BeforeEach
    void setUp() {
        // Chuẩn bị đối tượng ContactRequest cho việc test
        contactRequest = new ContactRequest();
        contactRequest.setName("Test User");
        contactRequest.setEmail("test@example.com");
        contactRequest.setSubject("Test Subject");
        contactRequest.setMessage("This is a test message content.");
    }

    @Test
    @DisplayName("Test sendContactEmail - Success")
    void testSendContactEmail_Success() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> {
            emailContactService.sendContactEmail(contactRequest);
        });

        // Verify
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Test sendContactEmail - Exception When Sending")
    void testSendContactEmail_ExceptionWhenSending() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailContactService.sendContactEmail(contactRequest);
        });

        // Verify
        assertEquals("Failed to send email", exception.getMessage());
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Test sendContactEmail - Null Contact Request")
    void testSendContactEmail_NullContactRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            emailContactService.sendContactEmail(null);
        });

        // Verify
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Test sendContactEmail - Exception Creating Message")
    void testSendContactEmail_ExceptionCreatingMessage() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailContactService.sendContactEmail(contactRequest);
        });

        // Verify
        assertEquals("Failed to create message", exception.getMessage());
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private OrderService orderService;

    /**
     * Sends an order confirmation email to the customer
     *
     * @param order The order details
     * @throws MessagingException If there's an error sending the email
     */
    public void sendOrderConfirmationEmail(Order order) throws MessagingException {
        try {
            if (order == null || order.getCustomerEmail() == null || order.getCustomerEmail().isEmpty()) {
                logger.warning("Cannot send email - order or email is null");
                return;
            }

            // Get order items
            List<OrderItem> orderItems = orderService.getOrderItems(order.getId());

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("orderItems", orderItems);

            // Process the template
            String emailContent;
            try {
                emailContent = templateEngine.process("email/order-confirmation", context);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing email template", e);
                // Fallback to simple text email if template processing fails
                sendSimpleConfirmationEmail(order);
                return;
            }

            // Create the email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getCustomerEmail());
            helper.setSubject("G3P Coffee - Order Confirmation #" + order.getId());
            helper.setText(emailContent, true); // true indicates HTML content

            // Send email
            mailSender.send(message);
            logger.info("Order confirmation email sent successfully to: " + order.getCustomerEmail());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send order confirmation email", e);
            throw new MessagingException("Failed to send order confirmation email: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method to send a simple text email if the HTML template fails
     */
    private void sendSimpleConfirmationEmail(Order order) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(order.getCustomerEmail());
        helper.setSubject("G3P Coffee - Order Confirmation #" + order.getId());

        String content = "Dear " + order.getCustomerName() + ",\n\n" +
                "Thank you for your order #" + order.getId() + ".\n" +
                "Your order has been received and is being processed.\n\n" +
                "Payment Method: " + order.getPaymentMethod() + "\n" +
                "Total Amount: " + order.getOrderTotal() + " đ\n" +
                "Shipping Address: " + order.getShippingAddress() + "\n\n" +
                "For Cash on Delivery orders, please have the exact amount ready when our delivery person arrives.\n\n" +
                "If you have any questions about your order, please contact our customer support team.\n\n" +
                "Best regards,\n" +
                "G3P Coffee Team";

        helper.setText(content);

        mailSender.send(message);
        logger.info("Simple order confirmation email sent successfully to: " + order.getCustomerEmail());
    }
}
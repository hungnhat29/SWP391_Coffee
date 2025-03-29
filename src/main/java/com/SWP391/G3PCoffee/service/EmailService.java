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
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(order.getCustomerEmail());
        helper.setSubject("G3P Coffee - Order Confirmation #" + order.getId());

        String content = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "   <meta charset='UTF-8'>" +
                "   <style>" +
                "       body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }" +
                "       .container { background-color: #f4f4f4; padding: 20px; border-radius: 8px; }" +
                "       .header { background-color: #6F4E37; color: white; text-align: center; padding: 10px; border-radius: 8px 8px 0 0; }" +
                "       .content { background-color: white; padding: 20px; border-radius: 0 0 8px 8px; }" +
                "       table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                "       th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }" +
                "       th { background-color: #f2f2f2; }" +
                "       .footer { text-align: center; color: #777; margin-top: 20px; }" +
                "   </style>" +
                "</head>" +
                "<body>" +
                "   <div class='container'>" +
                "       <div class='header'>" +
                "           <h1>Order Confirmation</h1>" +
                "       </div>" +
                "       <div class='content'>" +
                "           <p>Dear " + order.getCustomerName() + ",</p>" +
                "           <p>Thank you for your order with G3P Coffee. Your order details are below:</p>" +
                "           <table>" +
                "               <tr>" +
                "                   <th>Order Number</th>" +
                "                   <td>#" + order.getId() + "</td>" +
                "               </tr>" +
                "               <tr>" +
                "                   <th>Payment Method</th>" +
                "                   <td>" + order.getPaymentMethod() + "</td>" +
                "               </tr>" +
                "               <tr>" +
                "                   <th>Total Amount</th>" +
                "                   <td>" + order.getOrderTotal() + " đ</td>" +
                "               </tr>" +
                "               <tr>" +
                "                   <th>Shipping Address</th>" +
                "                   <td>" + order.getShippingAddress() + "</td>" +
                "               </tr>" +
                "           </table>" +
                "           <p><strong>Important Note:</strong> For Cash on Delivery orders, please have the exact amount ready when our delivery person arrives.</p>" +
                "           <p>If you have any questions about your order, please contact our customer support team.</p>" +
                "           <p>Thank you for choosing G3P Coffee!</p>" +
                "           <p>Best regards,<br>G3P Coffee Team</p>" +
                "       </div>" +
                "       <div class='footer'>" +
                "           <p>© 2024 G3P Coffee. All rights reserved.</p>" +
                "       </div>" +
                "   </div>" +
                "</body>" +
                "</html>";

        helper.setText(content, true); // true indicates HTML content

        mailSender.send(message);
        logger.info("Simple order confirmation email sent successfully to: " + order.getCustomerEmail());
    }
}
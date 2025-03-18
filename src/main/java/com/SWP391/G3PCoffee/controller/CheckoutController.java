package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.service.*;
import com.SWP391.G3PCoffee.model.CheckoutRequest;
import com.SWP391.G3PCoffee.model.PaymentResponse;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // Helper method to get or create session ID (copied from CartController)
    private String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session, Authentication authentication) {
        // Get cart items based on authentication status
        List<Cart> cartItems;
        User user = null;

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            user = userService.getCustomerByEmail(email);

            if (user != null) {
                // User is authenticated, get cart by userId
                cartItems = cartService.getCartItems(user.getId().intValue(), null);
                model.addAttribute("isLoggedIn", true);
                // You can add user details here if needed
            } else {
                // Authentication exists but user not found (shouldn't normally happen)
                String sessionId = getOrCreateSessionId(session);
                cartItems = cartService.getCartItems(null, sessionId);
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            // User is not authenticated, use session-based cart
            String sessionId = getOrCreateSessionId(session);
            cartItems = cartService.getCartItems(null, sessionId);
            model.addAttribute("isLoggedIn", false);
        }

        // Check if cart is empty
        if (cartItems.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        // Calculate total amount
        BigDecimal totalAmount = cartItems.stream()
                .map(Cart::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "checkout";
    }

    @PostMapping("/api/checkout/process")
    @ResponseBody
    public Map<String, Object> processCheckout(
            @RequestBody CheckoutRequest checkoutRequest,
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get user information based on authentication
            Integer userId = null;
            String sessionId = null;

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    userId = user.getId().intValue();
                } else {
                    // Fallback to session if user not found
                    sessionId = getOrCreateSessionId(session);
                }
            } else {
                // Not authenticated, use session
                sessionId = getOrCreateSessionId(session);
            }

            // Validate checkout data
            if (checkoutRequest == null ||
                    checkoutRequest.getShippingAddress() == null ||
                    checkoutRequest.getPaymentMethod() == null) {

                response.put("success", false);
                response.put("message", "Missing required checkout information");
                return response;
            }

            // Get cart items based on user status
            List<Cart> cartItems;
            if (userId != null) {
                cartItems = cartService.getCartItems(userId, null);
            } else {
                if (sessionId == null || sessionId.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Invalid session");
                    return response;
                }
                cartItems = cartService.getCartItems(null, sessionId);
            }

            // Check if cart is empty
            if (cartItems.isEmpty()) {
                response.put("success", false);
                response.put("message", "Your cart is empty");
                return response;
            }

            String customerName = checkoutRequest.getCustomerName();
            String customerEmail = checkoutRequest.getCustomerEmail();
            String customerPhone = checkoutRequest.getCustomerPhone();

            // Create order with items
            Order order = orderService.createOrder(
                    userId,
                    sessionId,
                    cartItems,
                    checkoutRequest.getShippingAddress(),
                    checkoutRequest.getPaymentMethod(),
                    customerName,
                    customerEmail,
                    customerPhone,
                    checkoutRequest.getReceiveType()
            );

            // Process payment based on payment method
            if ("VNPAY".equalsIgnoreCase(checkoutRequest.getPaymentMethod())) {
                // Generate VNPAY payment URL
                String paymentUrl = paymentService.createVnPayPaymentUrl(
                        order.getId(),
                        order.getOrderTotal(),
                        checkoutRequest.getReturnUrl(),
                        session
                );

                response.put("success", true);
                response.put("redirectUrl", paymentUrl);
                response.put("orderId", order.getId());
            } else if ("COD".equalsIgnoreCase(checkoutRequest.getPaymentMethod())) {
                // For COD orders, send confirmation email
                try {
                    emailService.sendOrderConfirmationEmail(order);
                } catch (Exception e) {
                    // Log email error but don't block the order processing
                    System.err.println("Failed to send confirmation email: " + e.getMessage());
                    e.printStackTrace();
                }

                // Update order status to "pending"
                orderService.updateOrderStatus(order.getId(), "pending", "Cash on Delivery");

                // Clear cart after successful order creation
                if (userId != null) {
                    cartService.clearCart(userId, null);
                } else {
                    cartService.clearCart(null, sessionId);
                }

                response.put("success", true);
                response.put("redirectUrl", "/order/confirmation/" + order.getId());
                response.put("orderId", order.getId());
            } else {
                // Other payment methods
                response.put("success", true);
                response.put("redirectUrl", "/order/confirmation/" + order.getId());
                response.put("orderId", order.getId());

                // Clear cart after successful order creation
                if (userId != null) {
                    cartService.clearCart(userId, null);
                } else {
                    cartService.clearCart(null, sessionId);
                }
            }

            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing checkout: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/payment/vnpay-return")
    public String vnpayReturn(
            @RequestParam Map<String, String> queryParams,
            Model model,
            HttpSession session,
            Authentication authentication) {

        try {
            // Get user information based on authentication
            Integer userId = null;
            String sessionId = null;

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    userId = user.getId().intValue();
                } else {
                    // Fallback to session if user not found
                    sessionId = getOrCreateSessionId(session);
                }
            } else {
                // Not authenticated, use session
                sessionId = getOrCreateSessionId(session);
            }

            // Log all query parameters for debugging
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            PaymentResponse paymentResponse = paymentService.processVnPayReturn(queryParams);

            if (paymentResponse.isSuccess()) {
                // Update order status
                orderService.updateOrderStatus(
                        paymentResponse.getOrderId(),
                        "paid",
                        paymentResponse.getTransactionInfo()
                );

                // Clear cart after successful payment
                if (userId != null) {
                    cartService.clearCart(userId, null);
                } else {
                    if (sessionId != null && !sessionId.isEmpty()) {
                        cartService.clearCart(null, sessionId);
                    }
                }

                model.addAttribute("paymentSuccess", true);
                model.addAttribute("orderId", paymentResponse.getOrderId());
                return "redirect:/order/confirmation/" + paymentResponse.getOrderId();
            } else {
                model.addAttribute("paymentSuccess", false);
                model.addAttribute("errorMessage", paymentResponse.getMessage());
                return "payment-error";
            }

        } catch (Exception e) {
            // Log the full exception for debugging
            e.printStackTrace();

            model.addAttribute("paymentSuccess", false);
            model.addAttribute("errorMessage", "Error processing payment: " + e.getMessage());
            return "payment-error";
        }
    }

    @GetMapping("/order/confirmation/{orderId}")
    public String showOrderConfirmation(
            @PathVariable Integer orderId,
            Model model,
            Authentication authentication) {

        try {
            // Get order details
            Order order = orderService.getOrderById(orderId);

            // Verify that this order belongs to the current user or session
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null && order.getUserId() != null) {
                    // For authenticated users, verify the order belongs to them
                    if (!(user.getId().intValue() == order.getUserId())) {
                        return "redirect:/access-denied";
                    }
                }
                // For authenticated users viewing a session-based order,
                // we could add additional checks here
            }

            // Add order details to model
            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderService.getOrderItems(orderId));

            return "order-confirmation";

        } catch (Exception e) {
            return "redirect:/error";
        }
    }
}
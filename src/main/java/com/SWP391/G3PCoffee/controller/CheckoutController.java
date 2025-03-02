package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.service.CartService;
import com.SWP391.G3PCoffee.service.OrderService;
import com.SWP391.G3PCoffee.service.PaymentService;
import com.SWP391.G3PCoffee.model.CheckoutRequest;
import com.SWP391.G3PCoffee.model.PaymentResponse;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/checkout")
    public String showCheckoutPage(
            Model model,
            HttpSession session,
            @SessionAttribute(name = "userId", required = false) Integer userId) {

        // Get cart items based on user status (logged in or guest)
        List<Cart> cartItems;
        if (userId != null) {
            cartItems = cartService.getCartItems(userId, null);
        } else {
            String sessionId = (String) session.getAttribute("sessionId");
            if (sessionId == null || sessionId.isEmpty()) {
                return "redirect:/cart?error=empty";
            }
            cartItems = cartService.getCartItems(null, sessionId);
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

        // Add user information if logged in
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
            // You can add user details here if needed
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "checkout";
    }

    @PostMapping("/api/checkout/process")
    @ResponseBody
    public Map<String, Object> processCheckout(
            @RequestBody CheckoutRequest checkoutRequest,
            HttpSession session,
            @SessionAttribute(name = "userId", required = false) Integer userId) {

        Map<String, Object> response = new HashMap<>();

        try {
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
            String sessionId = null;
            if (userId != null) {
                cartItems = cartService.getCartItems(userId, null);
            } else {
                sessionId = (String) session.getAttribute("sessionId");
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

            // Create order with items
            Order order = orderService.createOrder(
                    userId,
                    sessionId,
                    cartItems,
                    checkoutRequest.getShippingAddress(),
                    checkoutRequest.getPaymentMethod()
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
            } else {
                // COD or other payment methods
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
            @SessionAttribute(name = "userId", required = false) Integer userId) {

        try {
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
                    String sessionId = (String) session.getAttribute("sessionId");
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
            @SessionAttribute(name = "userId", required = false) Integer userId) {

        try {
            Order order = orderService.getOrderById(orderId);

            // Verify that this order belongs to the current user or session
            if (userId != null) {
                if (!userId.equals(order.getUserId())) {
                    return "redirect:/access-denied";
                }
            } else {
                // For guest users, we could check session ID, but this might be less reliable
                // after payment redirection, so we'll skip detailed validation for guests
            }

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderService.getOrderItems(orderId));

            return "order-confirmation";

        } catch (Exception e) {
            return "redirect:/error";
        }
    }
}
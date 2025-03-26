package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.service.CartService;
import com.SWP391.G3PCoffee.service.PromotionService;
import com.SWP391.G3PCoffee.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;


@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService; // Add a dependency to UserService

    @Autowired
    private PromotionService promotionService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session, Authentication authentication) {
        // Retrieve user from authentication if available
        List<Cart> cartItems;
        User user = null;

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            user = userService.getCustomerByEmail(email);

            if (user != null) {
                // User is authenticated, get cart by userId
                cartItems = cartService.getCartItems(user.getId().intValue(), null);
            } else {
                // Authentication exists but user not found (shouldn't normally happen)
                String sessionId = getOrCreateSessionId(session);
                cartItems = cartService.getCartItems(null, sessionId);
            }
        } else {
            // User is not authenticated, use session-based cart
            String sessionId = getOrCreateSessionId(session);
            cartItems = cartService.getCartItems(null, sessionId);
        }

        // Calculate total as before
        BigDecimal totalAmount = calculateTotalAmount(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "cart";
    }

    // Helper method to get or create session ID
    private String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    // Existing calculateTotalAmount method
    private BigDecimal calculateTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .map(Cart::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // API endpoint for adding to cart
    @PostMapping("/api/cart")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestBody Cart cartItem,
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validation checks
            if (cartItem == null || cartItem.getProduct() == null || cartItem.getProduct().getId() == null) {
                response.put("success", false);
                response.put("message", "Invalid product information");
                return response;
            }

            // Set default quantity if needed
            if (cartItem.getQuantity() == null || cartItem.getQuantity() < 1) {
                cartItem.setQuantity(1);
            }

            // Clear any existing IDs to avoid overrides
            cartItem.setUserId(null);
            cartItem.setSessionId(null);

            // Assign userId or sessionId based on authentication
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    cartItem.setUserId(user.getId().intValue());
                } else {
                    // Fallback to session if user not found
                    cartItem.setSessionId(getOrCreateSessionId(session));
                }
            } else {
                // Not authenticated, use session
                cartItem.setSessionId(getOrCreateSessionId(session));
            }

            // Validate JSON format for size and toppings
            validateJsonFormat(cartItem, response);
            if (response.containsKey("success") && !(boolean) response.get("success")) {
                return response;
            }

            // Save cart item
            Cart savedItem = cartService.addToCart(cartItem);

            response.put("success", true);
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng!");
            response.put("redirectUrl", "/cart");
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    // New helper method to validate JSON
    private void validateJsonFormat(Cart cartItem, Map<String, Object> response) {
        try {
            if (cartItem.getSizeInfo() != null && !cartItem.getSizeInfo().isEmpty()) {
                objectMapper.readTree(cartItem.getSizeInfo());
            }

            if (cartItem.getToppingsInfo() != null && !cartItem.getToppingsInfo().isEmpty()) {
                objectMapper.readTree(cartItem.getToppingsInfo());
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid JSON format: " + e.getMessage());
        }
    }

    @PutMapping("/api/cart/{cartId}")
    @ResponseBody
    public Map<String, Object> updateCartItem(
            @PathVariable Integer cartId,
            @RequestBody Cart updatedItem,
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Verify the cart item belongs to the current user/session
            List<Cart> cartItems;

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    cartItems = cartService.getCartItems(user.getId().intValue(), null);
                } else {
                    String sessionId = getOrCreateSessionId(session);
                    cartItems = cartService.getCartItems(null, sessionId);
                }
            } else {
                String sessionId = getOrCreateSessionId(session);
                cartItems = cartService.getCartItems(null, sessionId);
            }

            // Check if the cartId exists in the user's cart
            boolean hasAccess = cartItems.stream()
                    .anyMatch(item -> item.getId().equals(cartId));

            if (!hasAccess) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm trong giỏ hàng");
                return response;
            }

            // If validation passes, update the cart item
            Cart updatedCartItem = cartService.updateCartItem(cartId, updatedItem);

            response.put("success", true);
            response.put("message", "Giỏ hàng đã được cập nhật");
            response.put("subTotal", updatedCartItem.getSubTotal());

            // Calculate and return new total for the entire cart
            BigDecimal totalAmount = calculateTotalAmount(
                    authentication != null && authentication.isAuthenticated()
                            ? cartService.getCartItems(userService.getCustomerByEmail(authentication.getName()).getId().intValue(), null)
                            : cartService.getCartItems(null, getOrCreateSessionId(session))
            );
            response.put("totalAmount", totalAmount);

            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi cập nhật giỏ hàng: " + e.getMessage());
            return response;
        }
    }

    @DeleteMapping("/api/cart/{cartId}")
    @ResponseBody
    public Map<String, Object> removeFromCart(
            @PathVariable Integer cartId,
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Verify the cart item belongs to the current user/session
            List<Cart> cartItems;

            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    cartItems = cartService.getCartItems(user.getId().intValue(), null);
                } else {
                    String sessionId = getOrCreateSessionId(session);
                    cartItems = cartService.getCartItems(null, sessionId);
                }
            } else {
                String sessionId = getOrCreateSessionId(session);
                cartItems = cartService.getCartItems(null, sessionId);
            }

            // Check if the cartId exists in the user's cart
            boolean hasAccess = cartItems.stream()
                    .anyMatch(item -> item.getId().equals(cartId));

            if (!hasAccess) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm trong giỏ hàng");
                return response;
            }

            // If validation passes, remove the cart item
            cartService.removeFromCart(cartId);

            // Calculate and return new total for the remaining cart
            BigDecimal totalAmount = calculateTotalAmount(
                    authentication != null && authentication.isAuthenticated() && userService.getCustomerByEmail(authentication.getName()) != null
                            ? cartService.getCartItems(userService.getCustomerByEmail(authentication.getName()).getId().intValue(), null)
                            : cartService.getCartItems(null, getOrCreateSessionId(session))
            );

            response.put("success", true);
            response.put("message", "Sản phẩm đã được xóa khỏi giỏ hàng");
            response.put("totalAmount", totalAmount);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể xóa sản phẩm: " + e.getMessage());
            return response;
        }
    }

    @DeleteMapping("/api/cart")
    @ResponseBody
    public Map<String, Object> clearCart(
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                User user = userService.getCustomerByEmail(email);

                if (user != null) {
                    // User is authenticated, clear cart by userId
                    cartService.clearCart(user.getId().intValue(), null);
                } else {
                    // Authentication exists but user not found (shouldn't normally happen)
                    String sessionId = getOrCreateSessionId(session);
                    cartService.clearCart(null, sessionId);
                }
            } else {
                // User is not authenticated, use session-based cart
                String sessionId = getOrCreateSessionId(session);
                cartService.clearCart(null, sessionId);
            }

            response.put("success", true);
            response.put("message", "Giỏ hàng đã được xóa");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể xóa giỏ hàng: " + e.getMessage());
            return response;
        }
    }


}
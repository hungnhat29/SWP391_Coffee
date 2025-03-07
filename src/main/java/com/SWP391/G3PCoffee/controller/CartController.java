package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.service.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session, @SessionAttribute(name = "userId", required = false) Integer userId) {
        // Lấy giỏ hàng dựa trên userId hoặc sessionId
        List<Cart> cartItems;
        if (userId != null) {
            cartItems = cartService.getCartItems(userId, null);
        } else {
            String sessionId = (String) session.getAttribute("sessionId");
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
                session.setAttribute("sessionId", sessionId);
            }
            cartItems = cartService.getCartItems(null, sessionId);
        }
        
        // Tính tổng giá trị giỏ hàng dựa trên subtotal của từng item
        BigDecimal totalAmount = calculateTotalAmount(cartItems);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart";
    }
    
    private BigDecimal calculateTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .map(Cart::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // API endpoint cho AJAX - sử dụng response đơn giản
    @PostMapping("/api/cart")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestBody Cart cartItem,
            HttpSession session,
            @SessionAttribute(name = "userId", required = false) Integer userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra dữ liệu đầu vào
            if (cartItem == null) {
                response.put("success", false);
                response.put("message", "Invalid cart item data");
                return response;
            }
            
            if (cartItem.getProduct() == null || cartItem.getProduct().getId() == null) {
                response.put("success", false);
                response.put("message", "Product information is missing");
                return response;
            }
            
            // Set số lượng mặc định nếu chưa có hoặc không hợp lệ
            if (cartItem.getQuantity() == null || cartItem.getQuantity() < 1) {
                cartItem.setQuantity(1);
            }
            
            // Xóa các giá trị userId và sessionId cũ để tránh ghi đè không đúng
            cartItem.setUserId(null);
            cartItem.setSessionId(null);
            
            // Thiết lập userId hoặc sessionId dựa trên trạng thái đăng nhập
            if (userId != null) {
                cartItem.setUserId(userId);
            } else {
                String sessionId = (String) session.getAttribute("sessionId");
                if (sessionId == null || sessionId.isEmpty()) {
                    sessionId = UUID.randomUUID().toString();
                    session.setAttribute("sessionId", sessionId);
                }
                cartItem.setSessionId(sessionId);
            }
            
            // Kiểm tra định dạng JSON cho sizeInfo và toppingsInfo
            try {
                if (cartItem.getSizeInfo() != null && !cartItem.getSizeInfo().isEmpty()) {
                    JsonNode sizeNode = objectMapper.readTree(cartItem.getSizeInfo());
                    // Có thể thêm kiểm tra thêm nếu cần
                }
                
                if (cartItem.getToppingsInfo() != null && !cartItem.getToppingsInfo().isEmpty()) {
                    JsonNode toppingsNode = objectMapper.readTree(cartItem.getToppingsInfo());
                    // Có thể thêm kiểm tra thêm nếu cần
                }
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Invalid JSON format: " + e.getMessage());
                return response;
            }
            
            // Lưu thông tin giỏ hàng (trong service đã tự động tính toán sub_total)
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
    
    @PutMapping("/api/cart/{cartId}")
@ResponseBody
public Map<String, Object> updateCartItem(
        @PathVariable Integer cartId,
        @RequestBody Cart updatedItem) {

    Map<String, Object> response = new HashMap<>();

    try {
        Cart updatedCartItem = cartService.updateCartItem(cartId, updatedItem);

        response.put("success", true);
        response.put("message", "Giỏ hàng đã được cập nhật");
        response.put("subTotal", updatedCartItem.getSubTotal()); // Trả về subTotal để frontend cập nhật
        return response;
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Không tìm thấy sản phẩm trong giỏ hàng");
        return response;
    }
}


    
    @DeleteMapping("/api/cart/{cartId}")
    @ResponseBody
    public Map<String, Object> removeFromCart(@PathVariable Integer cartId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            cartService.removeFromCart(cartId);
            response.put("success", true);
            response.put("message", "Sản phẩm đã được xóa khỏi giỏ hàng");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể xóa sản phẩm");
            return response;
        }
    }
    
    @DeleteMapping("/api/cart")
    @ResponseBody
    public Map<String, Object> clearCart(
            HttpSession session,
            @SessionAttribute(name = "userId", required = false) Integer userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (userId != null) {
                cartService.clearCart(userId, null);
            } else {
                String sessionId = (String) session.getAttribute("sessionId");
                if (sessionId != null && !sessionId.isEmpty()) {
                    cartService.clearCart(null, sessionId);
                }
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

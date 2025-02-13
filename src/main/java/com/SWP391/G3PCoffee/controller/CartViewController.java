//package com.SWP391.G3PCoffee.controller;
//
//import com.SWP391.G3PCoffee.model.Cart;
//import com.SWP391.G3PCoffee.model.CartRequest;
//import com.SWP391.G3PCoffee.service.CartService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpSession;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//public class CartViewController {
//
//    @Autowired
//    private CartService cartService;
//
//    // View cart page
//    @GetMapping("/cart")
//    public String viewCart(Model model, HttpSession session) {
//        // Get userId from session or use sessionId for guests
//        Integer userId = (Integer) session.getAttribute("userId");
//        String sessionId = session.getId();
//        
//        List<Cart> cartItems;
//        if (userId != null) {
//            cartItems = cartService.getCartItems(userId);
//        } else {
//            cartItems = cartService.getCartItemsBySessionId(sessionId);
//        }
//        
//        model.addAttribute("cartItems", cartItems);
//        return "test";
//    }
//
//    // API endpoint to add item to cart
//    @PostMapping("/api/cart/add")
//    @ResponseBody
//    public ResponseEntity<?> addToCart(@RequestBody CartRequest request, HttpSession session) {
//        try {
//            // Get userId from session or use sessionId for guests
//            Integer userId = (Integer) session.getAttribute("userId");
//            String sessionId = session.getId();
//            
//            Cart cartItem;
//            if (userId != null) {
//                cartItem = cartService.addToCart(userId, null, request.getProductId(), 
//                    request.getQuantity(), request.getSize(), request.getToppingName());
//            } else {
//                cartItem = cartService.addToCart(null, sessionId, request.getProductId(), 
//                    request.getQuantity(), request.getSize(), request.getToppingName());
//            }
//            
//            return ResponseEntity.ok(cartItem);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Failed to add item to cart: " + e.getMessage());
//        }
//    }
//
//    // API endpoint to get cart items count
//    @GetMapping("/api/cart/user/{userIdentifier}")
//    @ResponseBody
//    public ResponseEntity<?> getCartItems(@PathVariable String userIdentifier, HttpSession session) {
//        try {
//            List<Cart> cartItems;
//            if (userIdentifier.startsWith("-")) {
//                // Guest user with session ID
//                cartItems = cartService.getCartItemsBySessionId(session.getId());
//            } else {
//                // Registered user with user ID
//                cartItems = cartService.getCartItems(Integer.parseInt(userIdentifier));
//            }
//            return ResponseEntity.ok(cartItems);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Failed to get cart items: " + e.getMessage());
//        }
//    }
//}
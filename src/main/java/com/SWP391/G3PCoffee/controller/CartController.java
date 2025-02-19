package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<Cart> cartItems;
        
        if (userId != null) {
            cartItems = cartService.getCartByUserId(userId);
        } else {
            cartItems = cartService.getCartBySessionId(session.getId());
        }
        
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @GetMapping("/api/cart/items")
    @ResponseBody
    public ResponseEntity<List<Cart>> getCartItems(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            List<Cart> cartItems;
            
            if (userId != null) {
                cartItems = cartService.getCartByUserId(userId);
            } else {
                cartItems = cartService.getCartBySessionId(session.getId());
            }
            
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/cart/{cartId}/quantity/{quantity}")
    @ResponseBody
    public ResponseEntity<Cart> updateCartQuantity(
            @PathVariable int cartId,
            @PathVariable int quantity,
            HttpSession session) {
        try {
            Cart updatedItem = cartService.updateCartItem(cartId, quantity);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/api/cart/{cartId}")
    @ResponseBody
    public ResponseEntity<List<Cart>> removeFromCart(@PathVariable int cartId, HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            cartService.removeCartItem(cartId);
            
            // Return updated cart items after removal
            List<Cart> updatedCart;
            if (userId != null) {
                updatedCart = cartService.getCartByUserId(userId);
            } else {
                updatedCart = cartService.getCartBySessionId(session.getId());
            }
            
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<List<Cart>> addToCart(@RequestBody Cart cartItem, HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            
            // Set the user ID or session ID based on whether the user is logged in
            if (userId != null) {
                cartItem.setUserId(userId);
            } else {
                cartItem.setSessionId(session.getId());
            }
            
            // Add the item to the cart
            cartService.addToCart(cartItem);
            
            // Fetch and return the updated cart items
            List<Cart> updatedCart;
            if (userId != null) {
                updatedCart = cartService.getCartByUserId(userId);
            } else {
                updatedCart = cartService.getCartBySessionId(session.getId());
            }
            
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
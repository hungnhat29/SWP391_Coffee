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
        return "cart";
    }

    @GetMapping("/api/cart/items")
    @ResponseBody
    public ResponseEntity<List<Cart>> getCartItems(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            List<Cart> cartItems;
            System.out.println("Session ID: " + session.getId());
        System.out.println("User ID: " + userId);
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
    public ResponseEntity<?> updateCartQuantity(
            @PathVariable int cartId,
            @PathVariable int quantity,
            HttpSession session) {
        try {
            Cart updatedItem = cartService.updateCartItem(cartId, quantity);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/cart/{cartId}")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(@PathVariable int cartId, HttpSession session) {
        try {
            cartService.removeCartItem(cartId);
            // Reload the cart to reflect the changes
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/cart/add")
@ResponseBody
public ResponseEntity<List<Cart>> addToCart(@RequestBody Cart cartItem, HttpSession session) {
    try {
        // Call your CartService to add the item to the cart
        cartService.addToCart(cartItem);

        // Fetch the updated list of cart items after adding
        List<Cart> cartItems = cartService.getCartBySessionId(session.getId());

        // Return the updated list
        return ResponseEntity.ok(cartItems);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
    }
}


}


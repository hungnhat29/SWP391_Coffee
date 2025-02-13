package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    
    public List<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId);
    }
    
    public List<Cart> getCartBySessionId(String sessionId) {
        System.out.println("Fetching cart for sessionId: " + sessionId);
        return cartRepository.findBySessionId(sessionId);
    }
    
    public Cart addToCart(Cart cartItem) {
        if (cartItem.getUserId() == null) {
            cartItem.setSessionId(cartItem.getSessionId());
        }
        if (cartItem.getUserId() != null) {
            Cart existingItem = cartRepository.findByUserIdAndProductIdAndSizeAndToppingName(
                cartItem.getUserId(), cartItem.getProductId(), cartItem.getSize(), cartItem.getToppingName());
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(existingItem);
            }
        } else if (cartItem.getSessionId() != null) {
            Cart existingItem = cartRepository.findBySessionIdAndProductIdAndSizeAndToppingName(
                cartItem.getSessionId(), cartItem.getProductId(), cartItem.getSize(), cartItem.getToppingName());
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(existingItem);
            }
        }
        
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cartItem);
    }
    
    public Cart updateCartItem(int cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public void removeCartItem(int cartId) {
        cartRepository.deleteById(cartId);
    }
}
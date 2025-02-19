package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.CartRepository;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId);
    }
    
    public List<Cart> getCartBySessionId(String sessionId) {
        System.out.println("Fetching cart for sessionId: " + sessionId);
        return cartRepository.findBySessionId(sessionId);
    }
    
    public Cart addToCart(Cart cartItem) {
        // Fetch the product first
        Product product = productRepository.findById(cartItem.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Set the product
        cartItem.setProduct(product);
        
        // Check for user or session based cart
        if (cartItem.getUserId() != null) {
            // User is logged in
            Cart existingItem = cartRepository.findByUserIdAndProductIdAndSizeAndToppingName(
                cartItem.getUserId(), 
                product.getId(), 
                cartItem.getSize(), 
                cartItem.getToppingName()
            );
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(existingItem);
            }
        } else if (cartItem.getSessionId() != null) {
            // Anonymous user
            Cart existingItem = cartRepository.findBySessionIdAndProductIdAndSizeAndToppingName(
                cartItem.getSessionId(), 
                product.getId(), 
                cartItem.getSize(), 
                cartItem.getToppingName()
            );
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                existingItem.setUpdatedAt(LocalDateTime.now());
                return cartRepository.save(existingItem);
            }
        }
        
        // If no existing item found, save new cart item
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cartItem);
    }
    
    public Cart updateCartItem(int cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Make sure the Product is loaded
        Product product = productRepository.findById(cart.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        cart.setProduct(product);
        
        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    public void removeCartItem(int cartId) {
        cartRepository.deleteById(cartId);
    }
}
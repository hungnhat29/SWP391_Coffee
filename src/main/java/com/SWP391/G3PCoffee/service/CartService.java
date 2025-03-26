package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.repository.CartRepository;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Thêm sản phẩm vào giỏ hàng với kiểm tra size và topping
    @Transactional
    public Cart addToCart(Cart cartItem) {
        // Validate userId or sessionId
        if ((cartItem.getUserId() == null && cartItem.getSessionId() == null) ||
            (cartItem.getUserId() != null && cartItem.getSessionId() != null)) {
            throw new IllegalArgumentException("Either userId OR sessionId must be present, not both");
        }
        
        // Load đầy đủ đối tượng Product từ DB (bao gồm basePrice)
        Integer productId = cartItem.getProduct().getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        cartItem.setProduct(product);
        
        // Lấy giỏ hàng hiện tại cho người dùng hoặc phiên làm việc
        List<Cart> cartItems;
        if (cartItem.getUserId() != null) {
            cartItems = cartRepository.findByUserId(cartItem.getUserId());
        } else {
            cartItems = cartRepository.findBySessionId(cartItem.getSessionId());
        }
        
        // Kiểm tra xem trong giỏ đã có sản phẩm có cùng Product, size và toppings chưa
        Optional<Cart> existingItem = cartItems.stream()
            .filter(item -> item.getProduct().getId().equals(cartItem.getProduct().getId()))
            .filter(item -> isSameSizeAndToppings(item, cartItem))
            .findFirst();
        
        if (existingItem.isPresent()) {
            // Nếu đã có, cập nhật số lượng
            Cart item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItem.getQuantity());
            // @PreUpdate sẽ tự động gọi calculateSubTotal()
            return cartRepository.save(item);
        }
        
        // Nếu chưa có, lưu cart item mới (và @PrePersist sẽ tính toán sub_total)
        return cartRepository.save(cartItem);
    }
    
    // So sánh size và toppings của 2 cart item
    private boolean isSameSizeAndToppings(Cart item1, Cart item2) {
        try {
            // So sánh size info
            String size1 = item1.getSizeInfo();
            String size2 = item2.getSizeInfo();
            
            // Nếu cả hai đều null hoặc rỗng, coi như giống nhau
            if ((size1 == null || size1.isEmpty()) && (size2 == null || size2.isEmpty())) {
                // Tiếp tục kiểm tra topping
            } else if (size1 == null || size2 == null) {
                return false;
            } else {
                // Cố gắng parse JSON để so sánh
                try {
                    if (!objectMapper.readTree(size1).equals(objectMapper.readTree(size2))) {
                        return false;
                    }
                } catch (Exception e) {
                    // Nếu parse thất bại, so sánh trực tiếp chuỗi
                    if (!size1.equals(size2)) {
                        return false;
                    }
                }
            }
            
            // So sánh toppings info
            String toppings1 = item1.getToppingsInfo();
            String toppings2 = item2.getToppingsInfo();
            
            if ((toppings1 == null || toppings1.isEmpty()) && (toppings2 == null || toppings2.isEmpty())) {
                return true;
            } else if (toppings1 == null || toppings2 == null) {
                return false;
            } else {
                try {
                    return objectMapper.readTree(toppings1).equals(objectMapper.readTree(toppings2));
                } catch (Exception e) {
                    return toppings1.equals(toppings2);
                }
            }
        } catch (Exception e) {
            System.err.println("Error comparing cart items: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật số lượng sản phẩm
    @Transactional
    public Cart updateCartItem(Integer cartId, Cart updatedItem) {
        Cart existingItem = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
            
        existingItem.setQuantity(updatedItem.getQuantity());
        if (updatedItem.getSizeInfo() != null) {
            existingItem.setSizeInfo(updatedItem.getSizeInfo());
        }
        if (updatedItem.getToppingsInfo() != null) {
            existingItem.setToppingsInfo(updatedItem.getToppingsInfo());
        }
        
        // @PreUpdate sẽ tự động tính toán sub_total
        return cartRepository.save(existingItem);
    }
    
    // Xóa sản phẩm khỏi giỏ hàng
    @Transactional
    public void removeFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }
    
    // Lấy giỏ hàng theo userId hoặc sessionId
    public List<Cart> getCartItems(Integer userId, String sessionId) {
        List<Cart> items;
        if (userId != null) {
            items = cartRepository.findByUserId(userId);
        } else {
            items = cartRepository.findBySessionId(sessionId);
        }
        return items;
    }
    
    // Xóa toàn bộ giỏ hàng
    @Transactional
    public void clearCart(Integer userId, String sessionId) {
        if (userId != null) {
            cartRepository.deleteByUserId(userId);
        } else {
            cartRepository.deleteBySessionId(sessionId);
        }
    }

}

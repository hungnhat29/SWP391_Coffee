package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(Integer userId);
    List<Cart> findBySessionId(String sessionId);
    Cart findByUserIdAndProductIdAndSizeAndToppingName(Integer userId, Integer productId, String size, String toppingName);
    Cart findBySessionIdAndProductIdAndSizeAndToppingName(String sessionId, Integer productId, String size, String toppingName);
}
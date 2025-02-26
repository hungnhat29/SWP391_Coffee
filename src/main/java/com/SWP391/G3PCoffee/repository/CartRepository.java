package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    List<Cart> findByUserId(Integer userId);
    
    List<Cart> findBySessionId(String sessionId);
    
    List<Cart> findByUserIdAndProductId(Integer userId, Integer productId);
    
    List<Cart> findBySessionIdAndProductId(String sessionId, Integer productId);
    
    void deleteByUserId(Integer userId);
    
    void deleteBySessionId(String sessionId);
}
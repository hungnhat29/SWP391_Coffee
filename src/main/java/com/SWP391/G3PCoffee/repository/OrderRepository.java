package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<Order> findBySessionId(String sessionId);
    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);
    List<Order> findBySessionIdOrderByOrderDateDesc(String sessionId);

    Page<Order> findByUserIdOrderByOrderDateDesc(Integer userId, Pageable pageable);
    Page<Order> findBySessionIdOrderByOrderDateDesc(String sessionId, Pageable pageable);
}
package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.OrderItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);

    List<OrderItem> findAll();
}
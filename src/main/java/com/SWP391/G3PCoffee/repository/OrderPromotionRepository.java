package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, Integer> {
    List<OrderPromotion> findByOrderId(Integer orderId);
}
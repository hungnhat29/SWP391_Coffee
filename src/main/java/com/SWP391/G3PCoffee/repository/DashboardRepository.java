package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.DailySalesDTO;
import com.SWP391.G3PCoffee.model.ProductSalesDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface DashboardRepository extends JpaRepository<Order, Integer> {

    // Find completed orders
    List<Order> findByStatus(String status);
}
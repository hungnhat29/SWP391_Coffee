package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.*;
import com.SWP391.G3PCoffee.repository.OrderItemRepository;
import com.SWP391.G3PCoffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public BigDecimal getTotalSales() {
        List<Order> completedOrders = orderRepository.findByStatus("completed");
        return completedOrders.stream()
                .map(Order::getOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getNumberOfOrders() {
        return orderRepository.findByStatus("completed").size();
    }

    public BigDecimal getAverageOrderValue() {
        List<Order> completedOrders = orderRepository.findByStatus("completed");
        if (completedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = completedOrders.stream()
                .map(Order::getOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(completedOrders.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    public List<ProductSalesDTO> getTopSellingProducts(int limit) {
        List<Order> completedOrders = orderRepository.findByStatus("completed");
        List<Integer> completedOrderIds = completedOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // Sử dụng OrderItemDTO như một entity
        List<OrderItemDTO> orderItems = orderItemRepository.findAll()
                .stream()
                .filter(oi -> completedOrderIds.contains(oi.getOrder().getId()))
                .collect(Collectors.toList());

        Map<Integer, ProductSalesDTO> productSalesMap = new HashMap<>();
        for (OrderItemDTO oi : orderItems) {
            Integer productId = oi.getProduct().getId();
            String productName = oi.getProduct().getName();
            Integer quantity = oi.getQuantity();

            productSalesMap.compute(productId, (key, value) -> {
                if (value == null) {
                    return new ProductSalesDTO(productId, productName, (long) quantity);
                } else {
                    value.setTotalQuantity(value.getTotalQuantity() + quantity);
                    return value;
                }
            });
        }

        return productSalesMap.values().stream()
                .sorted((a, b) -> b.getTotalQuantity().compareTo(a.getTotalQuantity()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DailySalesDTO> getDailySales() {
        List<Order> completedOrders = orderRepository.findByStatus("completed");
        Map<LocalDate, BigDecimal> dailySalesMap = completedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().toLocalDate(),
                        Collectors.mapping(Order::getOrderTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return dailySalesMap.entrySet().stream()
                .map(entry -> new DailySalesDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailySalesDTO::getSaleDate))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getOrderStatusDistribution() {
        List<Order> allOrders = orderRepository.findAll();
        return allOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
}
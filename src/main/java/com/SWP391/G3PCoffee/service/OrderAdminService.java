package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.OrderDTO;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.repository.OrderRepository;
import com.SWP391.G3PCoffee.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderAdminService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Lấy lịch sử mua hàng của một người dùng
    public List<OrderDTO> getPurchaseHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return mapOrdersToDTOs(orders);
    }

    // Lấy tất cả đơn hàng
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return mapOrdersToDTOs(orders);
    }

    // Phương thức chung để ánh xạ từ Order sang OrderDTO
    private List<OrderDTO> mapOrdersToDTOs(List<Order> orders) {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            String firstProductImageUrl = null;
            String firstProductName = null;
            if (!items.isEmpty() && items.get(0).getProduct() != null) {
                firstProductImageUrl = items.get(0).getProduct().getImageUrl();
                firstProductName = items.get(0).getProduct().getName();
            }
            OrderDTO orderDTO = new OrderDTO(
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getOrderTotal(),
                    order.getStatus(),
                    order.getCustomerName(),
                    order.getCustomerEmail(),
                    order.getCustomerPhone(),
                    order.getShippingAddress(),
                    order.getPaymentMethod(),
                    firstProductImageUrl,
                    firstProductName,
                    items // Truyền danh sách orderItems
            );
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }

    // Lấy chi tiết đơn hàng theo ID
    public Order getOrderDetails(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }
}
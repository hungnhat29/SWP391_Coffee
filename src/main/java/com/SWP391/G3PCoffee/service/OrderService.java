package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.OrderRepository;
import com.SWP391.G3PCoffee.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;


    
    @Transactional
    public Order createOrder(
            Integer userId, 
            String sessionId, 
            List<Cart> cartItems, 
            String shippingAddress, 
            String paymentMethod, String customerName, String customerEmail,
            String customerPhone) {
        
        // Calculate order total
        BigDecimal orderTotal = cartItems.stream()
                .map(Cart::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create order
        Order order = new Order();
        if (userId != null) {
            order.setUserId(userId);
        } else {
            order.setUserId(null);
        }
        order.setSessionId(sessionId);
        order.setOrderTotal(orderTotal);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("pending");
        
        // Save order to get order ID
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSizeInfo(cartItem.getSizeInfo());
            orderItem.setToppingsInfo(cartItem.getToppingsInfo());
            orderItem.setSubTotal(cartItem.getSubTotal());

            order.setCustomerName(customerName);
            order.setCustomerEmail(customerEmail);
            order.setCustomerPhone(customerPhone);
            order.setCustomerAddress(shippingAddress);
            orderItemRepository.save(orderItem);
        }
        
        return savedOrder;
    }
    
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
    
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    @Transactional
    public Order updateOrderStatus(Integer orderId, String status, String transactionInfo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        // You could add transaction info to the order if needed
        
        return orderRepository.save(order);
    }
    
//    public List<Order> getUserOrders(Integer userId) {
//        return orderRepository.findByUserId(userId);
//    }
//
//    public List<Order> getGuestOrders(String sessionId) {
//        return orderRepository.findBySessionId(sessionId);
//    }
//
//    public List<Order> getOrdersByUserId(Integer userId) {
//        // Implement logic to fetch orders by user ID from database
//        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
//    }
//
//    public List<Order> getOrdersBySessionId(String sessionId) {
//        // Implement logic to fetch orders by session ID from database
//        return orderRepository.findBySessionIdOrderByOrderDateDesc(sessionId);
//    }

    // Add these methods to your existing OrderService class
    public Page<Order> getPagedOrdersByUserId(Integer userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);
    }

    public Page<Order> getPagedOrdersBySessionId(String sessionId, Pageable pageable) {
        return orderRepository.findBySessionIdOrderByOrderDateDesc(sessionId, pageable);
    }

    public Order getMostRecentOrderBySessionId(String sessionId) {
        return orderRepository.findFirstBySessionIdOrderByOrderDateDesc(sessionId);
    }

    public Page<Order> findByUserAndOrderId(User user, Long orderId, LocalDateTime startDate, Pageable pageable) {
        if (startDate != null) {
            return orderRepository.findByUserIdAndIdAndOrderDateAfter(user.getId(), orderId, startDate, pageable);
        } else {
            return orderRepository.findByUserIdAndId(user.getId(), orderId, pageable);
        }
    }

    public Page<Order> findByUserAndStatus(User user, String status, LocalDateTime startDate, Pageable pageable) {
        if (startDate != null) {
            return orderRepository.findByUserIdAndStatusContainingIgnoreCaseAndOrderDateAfter(user.getId(), status, startDate, pageable);
        } else {
            return orderRepository.findByUserIdAndStatusContainingIgnoreCase(user.getId(), status, pageable);
        }
    }

    public Page<Order> findByUserAndDate(User user, LocalDateTime startDate, Pageable pageable) {
        if (startDate != null) {
            return orderRepository.findByUserIdAndOrderDateAfter(user.getId(), startDate, pageable);
        } else {
            return orderRepository.findByUserId(user.getId(), pageable);
        }
    }
}
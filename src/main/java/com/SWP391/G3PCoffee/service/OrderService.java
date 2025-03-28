package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.constant.OrderStatus;
import com.SWP391.G3PCoffee.constant.TypeOrder;
import com.SWP391.G3PCoffee.repository.OrderRepository;
import com.SWP391.G3PCoffee.repository.OrderItemRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;
    
    @Transactional
    public Order createOrder(
            Integer userId, 
            String sessionId, 
            List<Cart> cartItems, 
            String shippingAddress, 
            String paymentMethod, String customerName, String customerEmail,
            String customerPhone,
            String receiveType) {
        
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
        order.setTypeOrder(Objects.equals(receiveType, "PICKUP") ? TypeOrder.PICKUP : TypeOrder.DELIVERY);
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

    public Page<Order> findByUserAndOrderIdExcludingStatuses(User user, Long orderId, LocalDateTime startDate, Pageable pageable) {
        List<String> excludedStatuses = Arrays.asList("completed", "canceled");
        if (startDate != null) {
            return orderRepository.findByUserIdAndIdAndStatusNotInAndOrderDateAfter(user.getId(), orderId, excludedStatuses, startDate, pageable);
        } else {
            return orderRepository.findByUserIdAndIdAndStatusNotIn(user.getId(), orderId, excludedStatuses, pageable);
        }
    }

    public Page<Order> findByUserAndStatusExcludingStatuses(User user, String status, LocalDateTime startDate, Pageable pageable) {
        List<String> excludedStatuses = Arrays.asList("completed", "canceled");
        if (startDate != null) {
            return orderRepository.findByUserIdAndStatusContainingIgnoreCaseAndStatusNotInAndOrderDateAfter(user.getId(), status, excludedStatuses, startDate, pageable);
        } else {
            return orderRepository.findByUserIdAndStatusContainingIgnoreCaseAndStatusNotIn(user.getId(), status, excludedStatuses, pageable);
        }
    }

    public Page<Order> findByUserAndDateExcludingStatuses(User user, LocalDateTime startDate, Pageable pageable) {
        List<String> excludedStatuses = Arrays.asList("completed", "canceled");
        if (startDate != null) {
            return orderRepository.findByUserIdAndStatusNotInAndOrderDateAfter(user.getId(), excludedStatuses, startDate, pageable);
        } else {
            return orderRepository.findByUserIdAndStatusNotIn(user.getId(), excludedStatuses, pageable);
        }
    }

    public Page<Order> getPagedOrdersByUserIdExcludingStatuses(Integer userId, Pageable pageable) {
        List<String> excludedStatuses = Arrays.asList("completed", "canceled");
        return orderRepository.findByUserIdAndStatusNotInOrderByOrderDateDesc(userId, excludedStatuses, pageable);
    }

    public Page<Order> getOrderByType(TypeOrder typeOrder, String email, Pageable pageable) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) {
            return null;
        }
        Long userId = user.getId();
        return orderRepository.getPageOrderUser(typeOrder, userId, pageable);
    }

    public Page<Order> getAllOrder(String email, Pageable pageable) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) {
            return null;
        }
        Long userId = user.getId();

        return orderRepository.getAllOrder(userId, pageable);
    }

    public Map<String, Object> cancelOrderByUser(String email, Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getCustomerByEmail(email);
        if (user == null) {
            response.put("message", "Can't not find user update status");
            response.put("type", "error");
            return response;
        }

        Long userId = user.getId();
        Order order = findOrderById(orderId, response);
        if (order == null) return response;

        if (!((Long) order.getUserId().longValue()).equals(userId)) {
            response.put("message", "Can't not find order of user to update status");
            response.put("type", "error");
            return response;
        }

        String status = order.getStatus();
        if (status != null && (!status.equals(OrderStatus.pending.name()) && !status.equals(OrderStatus.paid.name()))) {
            response.put("message", "Can't cancel order");
            response.put("type", "error");
            return response;
        }

        order.setStatus(OrderStatus.canceled.name());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        response.put("message", "cancel order successful");
        response.put("type", "success");
        return response;
    }

    public List<Order> getAllOrderAdmin() {
        return orderRepository.getAllOrderAdmin();
    }

    public Page<Order> getPageOrderAdminByType(TypeOrder typeOrder, Pageable pageable) {
        return orderRepository.getPageOrderByTypeAdmin(typeOrder, pageable);
    }

    public Map<String, Object> updateStatusOrder(Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        Order order = findOrderById(orderId, response);
        if (order == null) return response;

        OrderStatus status = OrderStatus.valueOf(order.getStatus());
        TypeOrder typeOrder = order.getTypeOrder();
        String payment = order.getPaymentMethod();
        String nextStatus = status.getNextStatus(typeOrder, payment).name();
        order.setStatus(nextStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        response.put("message", "update status order successful");
        response.put("type", "success");
        return response;
    }

    public Map<String, Object> cancelOrderByAdmin(Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        Order order = findOrderById(orderId, response);
        if (order == null) return response;

        order.setStatus(OrderStatus.canceled.name());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        response.put("message", "cancel order successful");
        response.put("type", "success");
        return response;
    }

    private Order findOrderById(Integer orderId, Map<String, Object> response) {
        return orderRepository.findById(orderId).orElseGet(() -> {
            response.put("message", "Can't find order to update status");
            response.put("type", "error");
            return null;
        });
    }

    public long getOrderCountByCustomerId(Integer userId) {
        if (userId == null) {
            return 0; // Return 0 if no userId is provided (e.g., guest orders)
        }
        return orderRepository.countByUserId(userId); // Assumes OrderRepository has a countByUserId method
    }
}
package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.OrderDTO;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.service.OrderAdminService;
import com.SWP391.G3PCoffee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@CrossOrigin(origins = "http://localhost:8080")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAdminService orderAdminService;

    // Hiển thị trang lịch sử mua hàng
    @GetMapping
    public String showOrdersPage(Model model) {
        return "AdminPurchaseHistory";
    }

    // API để lấy lịch sử mua hàng của một người dùng
    @GetMapping("/history/{userId}")
    @ResponseBody
    public List<OrderDTO> getPurchaseHistory(@PathVariable Integer userId) {
        List<OrderDTO> orders = orderAdminService.getPurchaseHistory(userId.longValue());
        if (orders.isEmpty()) {
            System.out.println("No orders found for userId: " + userId);
        } else {
            System.out.println("Found " + orders.size() + " orders for userId: " + userId);
        }
        return orders;
    }

    // API để lấy tất cả đơn hàng
    @GetMapping("/all")
    @ResponseBody
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = orderAdminService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found in the database.");
        } else {
            System.out.println("Found " + orders.size() + " orders in total.");
        }
        return orders;
    }

    // API để lấy chi tiết đơn hàng theo ID
    @GetMapping("/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderDetails(@PathVariable Integer orderId) {
        Order order = orderAdminService.getOrderDetails(orderId);
        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("orderItems", orderItems);
        System.out.println("Fetched order details for orderId: " + orderId);
        return response;
    }
}
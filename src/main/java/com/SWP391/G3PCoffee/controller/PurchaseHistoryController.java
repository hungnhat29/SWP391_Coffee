package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.service.OrderItemService;
import com.SWP391.G3PCoffee.service.OrderService;
import com.SWP391.G3PCoffee.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class PurchaseHistoryController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderItemService orderItemService;

    // Helper method to get or create session ID
    private String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    @GetMapping("/purchase-history")
    public String viewOrderHistory(
            Model model,
            HttpSession session,
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "viewOrderId", required = false) Integer viewOrderId) {

        // Convert page to zero-based index for Spring Pageable
        int pageIndex = page - 1;
        int pageSize = 5; // 5 orders per page
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<Order> orderPage;
        boolean isLoggedIn = false;

        if (authentication != null && authentication.isAuthenticated()) {
            // For logged-in users, get orders by user ID based on authentication
            String email = authentication.getName();
            User user = userService.getCustomerByEmail(email);

            if (user != null) {
                // Get paginated orders for authenticated user
                orderPage = orderService.getPagedOrdersByUserId(user.getId().intValue(), pageable);
                isLoggedIn = true;
            } else {
                // Authentication exists but user not found (shouldn't normally happen)
                String sessionId = getOrCreateSessionId(session);
                // Get the most recent order for guest users
                Order mostRecentOrder = orderService.getMostRecentOrderBySessionId(sessionId);
                List<Order> singleOrderList = new ArrayList<>();
                if (mostRecentOrder != null) {
                    singleOrderList.add(mostRecentOrder);
                }
                orderPage = new PageImpl<>(singleOrderList, pageable, singleOrderList.size());
                isLoggedIn = false;
            }
        } else {
            // For guest users, get the most recent order by session ID
            String sessionId = getOrCreateSessionId(session);
            Order mostRecentOrder = orderService.getMostRecentOrderBySessionId(sessionId);
            List<Order> singleOrderList = new ArrayList<>();
            if (mostRecentOrder != null) {
                singleOrderList.add(mostRecentOrder);
            }
            orderPage = new PageImpl<>(singleOrderList, pageable, singleOrderList.size());
            isLoggedIn = false;
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (orderPage.isEmpty()) {
            model.addAttribute("noOrders", true);
        } else {
            model.addAttribute("noOrders", false);

            // Add first product image URL for each order
            for (Order order : orderPage.getContent()) {
                List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
                if (!orderItems.isEmpty()) {
                    order.setFirstProductImageUrl(orderItems.get(0).getProduct().getImageUrl());
                }
            }
        }

        // If a specific order ID is provided for viewing details
        if (viewOrderId != null) {
            Order orderDetails = orderService.getOrderById(viewOrderId);
            if (orderDetails != null) {
                List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(viewOrderId);
                model.addAttribute("orderDetails", orderDetails);
                model.addAttribute("orderItems", orderItems);
                model.addAttribute("showOrderDetails", true);
            }
        }

        return "purchase-history";
    }
}
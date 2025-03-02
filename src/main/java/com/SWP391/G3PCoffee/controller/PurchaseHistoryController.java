//package com.SWP391.G3PCoffee.controller;
//
//import com.SWP391.G3PCoffee.model.Order;
//import com.SWP391.G3PCoffee.service.OrderService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.SessionAttribute;
//
//import java.util.List;
//
//@Controller
//public class PurchaseHistoryController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @GetMapping("/purchase-history")
//    public String showPurchaseHistory(
//            Model model,
//            HttpSession session,
//            @SessionAttribute(name = "userId", required = false) Integer userId) {
//
//        List<Order> orders;
//
//        // Check if user is logged in
//        if (userId != null) {
//            // Get orders for registered user
//            orders = orderService.getPaidOrdersByUserId(userId);
//            model.addAttribute("isLoggedIn", true);
//        } else {
//            // Get orders for guest user using session ID
//            String sessionId = (String) session.getAttribute("sessionId");
//            if (sessionId == null || sessionId.isEmpty()) {
//                model.addAttribute("orders", List.of());
//                model.addAttribute("message", "No purchase history found");
//                return "purchase-history";
//            }
//            orders = orderService.getPaidOrdersBySessionId(sessionId);
//            model.addAttribute("isLoggedIn", false);
//        }
//
//        model.addAttribute("orders", orders);
//
//        if (orders.isEmpty()) {
//            model.addAttribute("message", "No purchase history found");
//        }
//
//        return "purchase-history";
//    }
//}
package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.constant.OrderStatus;
import com.SWP391.G3PCoffee.constant.TypeOrder;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.service.OrderService;
import com.SWP391.G3PCoffee.service.PageService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class AdminOrderController {

    private final PageService pageService;
    private final OrderService orderService;

    @GetMapping()
    public String getPage() {
        return "orders";
    }

    @GetMapping("/get-all-order")
    public ResponseEntity<Map<String, Object>> getAllOrder(@AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getAllOrderAdmin();

        // Lấy role của user
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        // Trả về danh sách đơn hàng + role
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-order-items")
    public ResponseEntity<List<OrderItem>> getOrderItem(@RequestParam Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PostMapping("/canceled-order")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrderByAdmin(orderId));
    }

    @PostMapping("/update-order")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestParam Integer orderId) {
        return ResponseEntity.ok(orderService.updateStatusOrder(orderId));
    }

    @GetMapping("/get-next-status")
    public ResponseEntity<Map<String, String>> getNextOrderStatus(@RequestParam Integer orderId) {
        Map<String, String> response = new HashMap<>();
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            response.put("error", "Không tìm thấy đơn hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        OrderStatus status = OrderStatus.valueOf(order.getStatus());
        TypeOrder typeOrder = order.getTypeOrder();
        String payment = order.getPaymentMethod();
        String nextStatus = status.getNextStatus(typeOrder, payment).name();

        response.put("nextStatus", nextStatus);
        return ResponseEntity.ok(response);
    }

}
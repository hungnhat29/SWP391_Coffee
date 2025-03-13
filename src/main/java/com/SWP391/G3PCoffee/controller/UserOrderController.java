package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.constant.TypeOrder;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.service.OrderService;
import com.SWP391.G3PCoffee.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/user-order")
@RequiredArgsConstructor
public class UserOrderController {

    private final PageService pageService;
    private final OrderService orderService;

    @GetMapping("get-page")
    public String getPage() {
        return "user-order";
    }

    @GetMapping("/get-all-order")
    public ResponseEntity<Page<Order>> getAllOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "50") int size,
                                                   @RequestParam(defaultValue = "createdAt", required = false) String sortField,
                                                   @RequestParam(defaultValue = "desc") String sortDir) {
        String email = userDetails.getUsername();
        Pageable pageable = pageService.getPage(page, size, sortDir, sortField);
        return ResponseEntity.ok(orderService.getAllOrder(email, pageable));
    }

    @GetMapping("/get-order-by-type")
    public ResponseEntity<Page<Order>> getOrderByType(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "50") int size,
                                                      @RequestParam(defaultValue = "createdAt", required = false) String sortField,
                                                      @RequestParam(defaultValue = "desc") String sortDir,
                                                      @RequestParam TypeOrder typeOrder) {
        String email = userDetails.getUsername();
        Pageable pageable = pageService.getPage(page, size, sortDir, sortField);
        return ResponseEntity.ok(orderService.getOrderByType(typeOrder, email, pageable));
    }

    @PostMapping("/canceled-order")
    public ResponseEntity<Map<String, Object>> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam Integer orderId) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(orderService.cancelOrderByUser(email, orderId));
    }
}

package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String showDashboard(Model model) {
        model.addAttribute("totalSales", dashboardService.getTotalSales());
        model.addAttribute("numberOfOrders", dashboardService.getNumberOfOrders());
        model.addAttribute("averageOrderValue", dashboardService.getAverageOrderValue());
        model.addAttribute("topSellingProducts", dashboardService.getTopSellingProducts(5));
        model.addAttribute("dailySales", dashboardService.getDailySales());
        model.addAttribute("orderStatusDistribution", dashboardService.getOrderStatusDistribution());
        return "admin/dashboard";
    }
}
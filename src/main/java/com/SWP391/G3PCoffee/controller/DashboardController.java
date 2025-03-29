package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("admin/dashboard")
    public String showDashboard(Model model) {
        // Get all customers
        model.addAttribute("customers", dashboardService.getAllCustomers());

        // Get new customers count
        model.addAttribute("newCustomersCount", dashboardService.getNewCustomersCount());

        // Get revenue (daily, weekly, monthly)
        model.addAttribute("dailyRevenue", dashboardService.getDailyRevenue());
        model.addAttribute("weeklyRevenue", dashboardService.getWeeklyRevenue());
        model.addAttribute("monthlyRevenue", dashboardService.getMonthlyRevenue());

        // Get top selling products
        model.addAttribute("topSellingProducts", dashboardService.getTopSellingProducts());

        // Get orders for last 7 days
        model.addAttribute("recentOrders", dashboardService.getOrdersForLast7Days());

        return "AdminDashboard";
    }
}
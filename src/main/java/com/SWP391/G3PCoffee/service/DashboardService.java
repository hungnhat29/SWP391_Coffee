package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.OrderItem;
import com.SWP391.G3PCoffee.model.Product;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.OrderItemRepository;
import com.SWP391.G3PCoffee.repository.OrderRepository;
import com.SWP391.G3PCoffee.repository.ProductRepository;
import com.SWP391.G3PCoffee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // Lấy danh sách khách hàng
    public List<User> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> "customer".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    // Đếm số khách hàng mới trong 30 ngày qua
    public Long getNewCustomersCount() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.findAll().stream()
                .filter(user -> "customer".equals(user.getRole()) && user.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
    }

    // Tính doanh thu trong ngày hiện tại
    public Double getDailyRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<Order> orders = orderRepository.findByOrderDateBetween(startOfDay, endOfDay);
        return orders.stream()
                .mapToDouble(order -> order.getOrderTotal().doubleValue())
                .sum();
    }

    // Tính doanh thu trong tuần hiện tại (7 ngày qua)
    public Double getWeeklyRevenue() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        LocalDateTime endOfWeek = LocalDateTime.now();
        List<Order> orders = orderRepository.findByOrderDateBetween(startOfWeek, endOfWeek);
        return orders.stream()
                .mapToDouble(order -> order.getOrderTotal().doubleValue())
                .sum();
    }

    // Tính doanh thu hàng tháng (theo từng tháng trong năm hiện tại)
    public Map<Integer, Double> getMonthlyRevenue() {
        int currentYear = LocalDateTime.now().getYear();
        List<Order> orders = orderRepository.findAll();

        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyRevenue.put(month, 0.0);
        }

        for (Order order : orders) {
            if (order.getOrderDate().getYear() == currentYear && "completed".equalsIgnoreCase(order.getStatus())) {
                int month = order.getOrderDate().getMonthValue();
                double currentTotal = monthlyRevenue.getOrDefault(month, 0.0);
                monthlyRevenue.put(month, currentTotal + order.getOrderTotal().doubleValue());
            }
        }

        return monthlyRevenue;
    }

    // Lấy top sản phẩm bán chạy
    public List<Object[]> getTopSellingProducts() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        Map<Product, ProductSalesInfo> productSalesMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                Order order = orderRepository.findById(item.getOrderId()).orElse(null);
                if (order != null) {
                    ProductSalesInfo salesInfo = productSalesMap.getOrDefault(product, new ProductSalesInfo(0L, null, 0.0));
                    long newQuantity = salesInfo.getQuantity() + item.getQuantity();
                    LocalDateTime lastDate = salesInfo.getLastDate() != null && salesInfo.getLastDate().isAfter(order.getOrderDate())
                            ? salesInfo.getLastDate() : order.getOrderDate();
                    double totalRevenue = salesInfo.getTotalRevenue() + (item.getSubTotal().doubleValue() * item.getQuantity());

                    productSalesMap.put(product, new ProductSalesInfo(newQuantity, lastDate, totalRevenue));
                }
            }
        }

        return productSalesMap.entrySet().stream()
                .sorted(Map.Entry.<Product, ProductSalesInfo>comparingByValue(Comparator.comparing(ProductSalesInfo::getQuantity).reversed()))
                .limit(10)
                .map(entry -> new Object[]{entry.getKey(), entry.getValue().getQuantity(), entry.getValue().getLastDate(), entry.getValue().getTotalRevenue()})
                .collect(Collectors.toList());
    }

    // Lấy đơn hàng trong 7 ngày qua
    public List<Order> getOrdersForLast7Days() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    // Tính tỷ lệ khách hàng có tài khoản đã mua hàng (đơn hàng completed)
    public Double getCustomerPurchaseRatio() {
        List<User> customers = userRepository.findAll().stream()
                .filter(user -> "customer".equals(user.getRole()))
                .collect(Collectors.toList());

        if (customers.isEmpty()) {
            return 0.0;
        }


        List<Order> completedOrders = orderRepository.findAll().stream()
                .filter(order -> "completed".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());

        long customersWithOrders = completedOrders.stream()
//                .map(order -> order.get)
                .distinct()
                .count();

        // Tính tỷ lệ
        return (double) customersWithOrders / customers.size() * 100;
    }

    // Lớp nội bộ để lưu trữ thông tin bán hàng của sản phẩm
    private static class ProductSalesInfo {
        private final long quantity;
        private final LocalDateTime lastDate;
        private final double totalRevenue;

        public ProductSalesInfo(long quantity, LocalDateTime lastDate, double totalRevenue) {
            this.quantity = quantity;
            this.lastDate = lastDate;
            this.totalRevenue = totalRevenue;
        }

        public long getQuantity() {
            return quantity;
        }

        public LocalDateTime getLastDate() {
            return lastDate;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }
    }
}
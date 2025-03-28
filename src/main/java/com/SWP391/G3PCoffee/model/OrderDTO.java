package com.SWP391.G3PCoffee.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Integer id;
    private Integer userId;
    private LocalDateTime orderDate;
    private BigDecimal orderTotal;
    private String status;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String firstProductImageUrl;
    private String firstProductName;
    private List<OrderItem> orderItems; // Thêm thuộc tính orderItems

    // Constructor
    public OrderDTO(Integer id, Integer userId, LocalDateTime orderDate, BigDecimal orderTotal, String status,
                    String customerName, String customerEmail, String customerPhone, String shippingAddress,
                    String paymentMethod, String firstProductImageUrl, String firstProductName, List<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.status = status;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.firstProductImageUrl = firstProductImageUrl;
        this.firstProductName = firstProductName;
        this.orderItems = orderItems;
    }

    // Getters và setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getFirstProductImageUrl() { return firstProductImageUrl; }
    public void setFirstProductImageUrl(String firstProductImageUrl) { this.firstProductImageUrl = firstProductImageUrl; }
    public String getFirstProductName() { return firstProductName; }
    public void setFirstProductName(String firstProductName) { this.firstProductName = firstProductName; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
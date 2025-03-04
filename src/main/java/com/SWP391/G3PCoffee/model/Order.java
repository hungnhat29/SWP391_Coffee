package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    private String status;
    
    @Column(name = "order_total")
    private BigDecimal orderTotal;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "shipping_address")
    private String shippingAddress;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "pending";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String firstProductImageUrl;
}
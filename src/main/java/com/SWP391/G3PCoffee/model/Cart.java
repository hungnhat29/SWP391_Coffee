package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    private String size;
    private String toppingName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Method to get product name (convenience method)
    public String getProductName() {
        return product != null ? product.getName() : null;
    }
    
    // Method to get product image URL (convenience method)
    public String getProductImageUrl() {
        return product != null ? product.getImageUrl() : null;
    }
    
    // Method to get product price (convenience method)
    public BigDecimal getProductPrice() {
        return product != null ? product.getPrice() : null;
    }
}
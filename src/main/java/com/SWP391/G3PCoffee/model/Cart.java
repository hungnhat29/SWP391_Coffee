package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.Data;
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
    
    @Column(name = "product_id")
    private Integer productId;
    
    private Integer quantity;
    private String size;
    private String toppingName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
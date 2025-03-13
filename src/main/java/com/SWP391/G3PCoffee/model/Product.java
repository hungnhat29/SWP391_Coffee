package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "NTEXT")
    private String description;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;
    
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sizes", columnDefinition = "NVARCHAR(MAX)")
    private String sizes; // JSON String

    @Column(name = "toppings", columnDefinition = "NVARCHAR(MAX)")
    private String toppings; // JSON String

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product() {

    }

    public Product(String name, Integer id, String description, BigDecimal basePrice, String imageUrl, Integer categoryId,
                   String sizes, String toppings, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.sizes = sizes;
        this.toppings = toppings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}



package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "NTEXT")
    private String description;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String sizes;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String toppings;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product(Integer id, String name, String description, BigDecimal basePrice, Category category, String imageUrl,
                   String sizes, String toppings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.imageUrl = imageUrl;
        this.sizes = sizes;
        this.toppings = toppings;
    }

    public Product() {

    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
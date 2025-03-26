package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UserPromotion")
@Data
public class UserPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "is_redeemed")
    private Boolean isRedeemed = false;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "status")
    private String status; // ELIGIBLE, USED, EXPIRED

    @Column(name = "percent_discount")
    private BigDecimal percentDiscount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
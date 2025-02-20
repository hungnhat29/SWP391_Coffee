package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Vouchers")
public class Vouchers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long id;

    @Column(name = "voucher_code", nullable = false, unique = true)
    private String voucherCode;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false)
    private VoucherType voucherType;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;
    
    @Column(name = "min_spending", nullable = false)
    private Integer minSpending;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    public enum VoucherType {
        FIXED, PERCENT;

        @JsonCreator
        public static VoucherType fromString(String value) {
            return VoucherType.valueOf(value.toUpperCase());
        }

        @JsonValue
        public String toJson() {
            return this.name();
        }
        }
}

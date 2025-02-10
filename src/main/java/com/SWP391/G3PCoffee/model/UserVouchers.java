package com.SWP391.G3PCoffee.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User_Vouchers", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "voucher_id"}))
public class UserVouchers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    private Vouchers voucher;

    @Column(name = "redeemed_at", updatable = false)
    private LocalDateTime redeemedAt;

    @PrePersist
    protected void onRedeem() {
        this.redeemedAt = LocalDateTime.now();
    }
}

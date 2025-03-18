package com.SWP391.G3PCoffee.model;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private Integer quantity = 1; // Mặc định là 1 để tránh null

    @Column(name = "size_info", columnDefinition = "NVARCHAR(MAX)")
    private String sizeInfo;

    @Column(name = "toppings_info", columnDefinition = "NVARCHAR(MAX)")
    private String toppingsInfo;

    @Column(name = "sub_total", precision = 10, scale = 0)
    private BigDecimal subTotal;

    @Column(name = "receive_type")
    private String receiveType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private static final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper dùng chung

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateSubTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateSubTotal();
    }

    public void calculateSubTotal() {
        try {
            BigDecimal basePrice = product != null ? product.getBasePrice() : BigDecimal.ZERO;
            BigDecimal sizePrice = parseJsonPrice(sizeInfo, SizeInfo.class);
            BigDecimal toppingPrice = parseJsonArrayPrice(toppingsInfo, ToppingInfo[].class);

            BigDecimal itemPrice = basePrice.add(sizePrice).add(toppingPrice);
            this.subTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
        } catch (Exception e) {
            System.err.println("Lỗi tính toán subtotal: " + e.getMessage());
            this.subTotal = BigDecimal.ZERO;
        }
    }

    private <T> BigDecimal parseJsonPrice(String json, Class<T> clazz) {
        try {
            if (json != null && !json.isEmpty()) {
                T obj = objectMapper.readValue(json, clazz);
                if (obj instanceof SizeInfo) {
                    return ((SizeInfo) obj).getPrice() != null ? ((SizeInfo) obj).getPrice() : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi phân tích JSON: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private <T> BigDecimal parseJsonArrayPrice(String json, Class<T[]> clazz) {
        try {
            if (json != null && !json.isEmpty()) {
                T[] array = objectMapper.readValue(json, clazz);
                BigDecimal total = BigDecimal.ZERO;
                for (T item : array) {
                    if (item instanceof ToppingInfo && ((ToppingInfo) item).getPrice() != null) {
                        total = total.add(((ToppingInfo) item).getPrice());
                    }
                }
                return total;
            }
        } catch (Exception e) {
            System.err.println("Lỗi phân tích JSON (mảng): " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    @Data
    public static class SizeInfo {
        private String name;
        private BigDecimal price;
    }

    @Data
    public static class ToppingInfo {
        private String name;
        private BigDecimal price;
    }
}

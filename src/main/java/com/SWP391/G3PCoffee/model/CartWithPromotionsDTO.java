package com.SWP391.G3PCoffee.model;

import com.SWP391.G3PCoffee.model.Cart;
import com.SWP391.G3PCoffee.model.Promotion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing a shopping cart with applied promotions
 */
public class CartWithPromotionsDTO {
    private List<Cart> items;
    private List<AppliedPromotionDTO> appliedPromotions;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String couponCode;

    public CartWithPromotionsDTO() {
        this.items = new ArrayList<>();
        this.appliedPromotions = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    // Getters and Setters
    public List<Cart> getItems() {
        return items;
    }

    public void setItems(List<Cart> items) {
        this.items = items;
    }

    public List<AppliedPromotionDTO> getAppliedPromotions() {
        return appliedPromotions;
    }

    public void setAppliedPromotions(List<AppliedPromotionDTO> appliedPromotions) {
        this.appliedPromotions = appliedPromotions;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     * Calculates the total after applying all discounts
     */
    public void calculateTotal() {
        // Calculate subtotal from all cart items
        if (items != null && !items.isEmpty()) {
            this.subtotal = items.stream()
                    .map(Cart::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Calculate total discount from all applied promotions
        if (appliedPromotions != null && !appliedPromotions.isEmpty()) {
            this.discountAmount = appliedPromotions.stream()
                    .map(promo -> {
                        try {
                            return new BigDecimal(String.valueOf(promo.getDiscountAmount()));
                        } catch (NumberFormatException e) {
                            return BigDecimal.ZERO;
                        }
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Calculate final total (subtotal - discounts)
        this.total = this.subtotal.subtract(this.discountAmount);

        // Ensure total is not negative
        if (this.total.compareTo(BigDecimal.ZERO) < 0) {
            this.total = BigDecimal.ZERO;
        }
    }

    /**
     * Adds an applied promotion and recalculates the total
     */
    public void addAppliedPromotion(AppliedPromotionDTO promotion) {
        if (this.appliedPromotions == null) {
            this.appliedPromotions = new ArrayList<>();
        }

        this.appliedPromotions.add(promotion);
        calculateTotal();
    }

    /**
     * Removes an applied promotion and recalculates the total
     */
    public void removeAppliedPromotion(Long promotionId) {
        if (this.appliedPromotions != null) {
            this.appliedPromotions.removeIf(p -> p.getPromotionId().equals(promotionId));
            calculateTotal();
        }
    }
}

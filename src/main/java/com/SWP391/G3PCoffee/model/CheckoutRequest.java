/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.model;

/**
 *
 * @author hungp
 */
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutRequest {
    private String shippingAddress;
    private String paymentMethod; // "COD", "VNPAY", etc.
    private String returnUrl; // Return URL for payment gateway
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private BigDecimal totalAmount;
    private String receiveType;
}

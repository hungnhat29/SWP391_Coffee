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

@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private Integer orderId;
    private String transactionInfo;
    
    public static PaymentResponse success(Integer orderId, String transactionInfo) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setOrderId(orderId);
        response.setTransactionInfo(transactionInfo);
        return response;
    }
    
    public static PaymentResponse failure(String message) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
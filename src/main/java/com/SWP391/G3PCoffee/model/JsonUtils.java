/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.SWP391.G3PCoffee.model;

/**
 *
 * @author hungp
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.SWP391.G3PCoffee.model.Cart;

public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getSizeName(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "";
        }
        try {
            Cart.SizeInfo size = objectMapper.readValue(json, Cart.SizeInfo.class);
            return size.getName();
        } catch (Exception e) {
            System.err.println("Error parsing sizeInfo JSON: " + e.getMessage());
            return "";
        }
    }

    public static String getToppingsNames(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "";
        }
        try {
            Cart.ToppingInfo[] toppings = objectMapper.readValue(json, Cart.ToppingInfo[].class);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < toppings.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(toppings[i].getName());
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error parsing toppingsInfo JSON: " + e.getMessage());
            return "";
        }
    }
}

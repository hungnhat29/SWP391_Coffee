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
import java.util.List;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer categoryId = null;
    private String imageUrl;
    private List<SizeOption> sizes;
    private List<ToppingOption> toppings;

    @Data
    public static class SizeOption {
        private String name;
        private BigDecimal price;
    }

    @Data
    public static class ToppingOption {
        private String name;
        private BigDecimal price;
    }
}

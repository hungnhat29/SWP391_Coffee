package com.SWP391.G3PCoffee.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySalesDTO {
    private LocalDate saleDate;
    private BigDecimal dailySales;

    public DailySalesDTO(LocalDate saleDate, BigDecimal dailySales) {
        this.saleDate = saleDate;
        this.dailySales = dailySales;
    }
}
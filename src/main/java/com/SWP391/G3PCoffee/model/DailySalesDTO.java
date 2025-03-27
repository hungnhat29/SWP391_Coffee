package com.SWP391.G3PCoffee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesDTO implements Serializable {
    private LocalDate saleDate;
    private BigDecimal totalSales;
}
package com.portfolio.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DividendRequest {
    @NotBlank(message = "Symbol is required")
    private String symbol;
    @NotNull(message = "Per share amount is required")
    @DecimalMin(value = "0.0001", message = "Amount must be greater than 0")
    private BigDecimal perShareAmount;
    @NotNull(message = "Record date is required")
    private LocalDate recordDate;
}

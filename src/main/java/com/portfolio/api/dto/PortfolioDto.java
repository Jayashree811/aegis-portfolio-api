package com.portfolio.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PortfolioDto {
    private UUID id;
    @NotBlank(message = "Client name is required")
    private String clientName;
    private String riskProfile;
    @DecimalMin(value = "0.0", message = "Cash balance cannot be negative")
    private BigDecimal cashBalance = BigDecimal.ZERO;
}

package com.portfolio.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldingDto {
    private UUID id;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal averageCost;
}

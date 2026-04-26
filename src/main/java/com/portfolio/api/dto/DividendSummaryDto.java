package com.portfolio.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DividendSummaryDto {
    private String symbol;
    private BigDecimal totalDividend;
}
